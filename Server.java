import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.Scanner;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server {

    private static final String KEY_STORE_PASSWORD = "88889999";

    public static void main(String[] args) {

        try {
            // Create an SSL context
            SSLContext context = SSLContext.getInstance("SSL");
            // Create a key management factory
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");

            // Create a keystore object
            KeyStore keyStore = KeyStore.getInstance("JKS");

            // Fill the keystore object
            FileInputStream fileInputStream = new FileInputStream("ssl/kimchhung.jks");
            keyStore.load(fileInputStream, KEY_STORE_PASSWORD.toCharArray());

            // Initialize the key management factory
            keyManagerFactory.init(keyStore, KEY_STORE_PASSWORD.toCharArray());

            // Initialize the context
            context.init(keyManagerFactory.getKeyManagers(), null, null);

            // Create secure socket for server
            SSLServerSocketFactory serverSocketFactory = context.getServerSocketFactory();
            SSLServerSocket serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(9999);

            boolean status = false;
            while (true) {
                // Wait for a client
                System.out.println("Waiting for client...");
                SSLSocket connection = (SSLSocket) serverSocket.accept();

                // true if has a client
                if (connection.isConnected()) {
                    status = true;
                    System.out.println("Client has joined the server. ");
                }

                InputStream inputStream = connection.getInputStream();
                System.out.println("bug here ? : " + inputStream);
                while (status) {
                    // Read request from the client
                    Scanner scanner = new Scanner(inputStream);
                    String encypted = scanner.nextLine();
                    System.out.println("Get Request : " + encypted);

                    String rawRequest = Constants.decrypteString(encypted);
                    Request request = Request.fromRawString(rawRequest);

                    System.out.println("Get Request : " + encypted);
                    System.out.println("To Raw Request : " + rawRequest);
                    // scanner.close();
                    switch (request.getAction()) {
                        case Action.WRITE:
                            WriteFileResponse(connection, request);
                            break;
                        case Action.VIEW:
                            viewFileResponse(connection, request);
                            break;
                        case Action.CLOSE:
                            System.out.println("Client left the server. ");
                            scanner.close();
                            connection.close();
                            status = false; // break the while loop
                            break;
                        default:
                            invalidResponse(connection);
                            break;
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void sendResponse(Socket connection, Response response) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        String encryted = Constants.encrypteString(response.toRawResponse());
        printWriter.write(encryted);
        printWriter.flush();
        System.out.println("Raw Response: " + response.toRawResponse());
        System.out.println("Responsed: " + encryted);
    }

    // private static void sendKeyResponse(Socket connection) throws IOException {
    // OutputStream outputStream = connection.getOutputStream();
    // PrintWriter printWriter = new PrintWriter(outputStream);

    // String key = "2";
    // Constants.storeKey(key);
    // Response response = new Response(Constants.PROTOCOL_VERSION, Status.OK, key);
    // System.out.println("Send Key(1-9) : " + key);

    // printWriter.write(response.toRawResponse());
    // printWriter.flush();
    // }

    private static void invalidResponse(Socket connection) throws IOException {
        Response response = new Response(Constants.PROTOCOL_VERSION, Status.INVALID, "Invalid request");
        System.out.println(Constants.PROTOCOL_VERSION + Status.INVALID + "Invalid Request. ");
        sendResponse(connection, response);
    }

    private static void WriteFileResponse(Socket connection, Request request) throws IOException {
        Params params = request.getParams();
        String fileName = params.getFilename();
        String content = params.getContent();

        String data = "";
        String status = "";

        try {
            File file = new File(fileName);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("Overide File already exists.");
            }

            PrintWriter writer = new PrintWriter(file);
            writer.print(content);
            writer.close();
            System.out.println("Successfully wrote to the file.");

            data = "The file has been written.";
            status = Status.OK;
        } catch (IOException e) {
            System.out.println("Write fail: " + e.getMessage());
            status = e.getMessage();
        }

        Response response = new Response(Constants.PROTOCOL_VERSION, status, data);
        sendResponse(connection, response);
    }

    private static void viewFileResponse(Socket connection, Request request) throws IOException {
        Params params = request.getParams();
        String fileName = params.getFilename();
        String data = "";
        String status = "";
        try {
            File myObj = new File(fileName);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                data += myReader.nextLine();
            }
            myReader.close();
            status = Status.OK;
        } catch (IOException e) {
            System.out.println("View fail: " + e.getMessage());
            data = "File not found";
            status = Status.NOT_FOUND;
        }
        Response response = new Response(Constants.PROTOCOL_VERSION, status, data);
        sendResponse(connection, response);
    }

}
