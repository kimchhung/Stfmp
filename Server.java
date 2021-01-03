import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            boolean status = false;
            while (true) {
                // Wait for a client
                System.out.println("Waiting for client...");
                Socket connection = serverSocket.accept();

                // true if has a client
                if (connection.isConnected()) {
                    status = true;
                    System.out.println("Client has joined the server. ");
                    sendKeyResponse(connection);
                }

                InputStream inputStream = connection.getInputStream();

                while (status) {
                    // Read request from the client

                    Scanner scanner = new Scanner(inputStream);
                    String encypted = scanner.nextLine();
                    String rawRequest = Constants.decrypteString(encypted);
                    Request request = Request.fromRawString(rawRequest);

                    System.out.println("Request : " + encypted);
                    System.out.println("Raw Request : " + rawRequest);
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

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void sendResponse(Socket connection, Response response) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        String encryted = Constants.encrypteString(response.toRawResponse());
        printWriter.write(encryted);
        printWriter.flush();
    }

    private static void sendKeyResponse(Socket connection) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);

        String key = Constants.generateKey();
        Response response = new Response(Constants.PROTOCOL_VERSION, Status.OK, key);
        System.out.println("Send Key(1-9) : " + key);

        printWriter.write(response.toRawResponse());
        printWriter.flush();
    }

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
