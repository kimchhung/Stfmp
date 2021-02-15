import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Client {
    public static void main(String[] args) {

        System.setProperty("javax.net.ssl.trustStore", "ssl/trust-store-kimchhung.cacerts");
        System.setProperty("javax.net.ssl.trustStorePassword", "88889999");
        // Connect to the server
        try {
            SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket connection = (SSLSocket) socketFactory.createSocket("localhost", 9999);

            // Tell users what they can do
            System.out.println("You have connected to port : 9999");
            System.out.println(" - Type 'write' :  write content to the file");
            System.out.println(" - Type 'view' :  view content of the file");
            System.out.println(" - Type 'close' :  close the connection.");
            // print key

            // Read input from users, what they want to do
            InputStream keyboardInputStream = System.in;
            Scanner keyboadScanner = new Scanner(keyboardInputStream);

            while (true) {
                System.out.print("Type your action here: ");
                String userInput = keyboadScanner.nextLine();
                Params params = null;

                if (userInput.equals(Action.CLOSE)) {
                    Request request = new Request(Constants.PROTOCOL_VERSION, userInput, params);
                    sendRequestToTheServer(connection, request);
                    break;
                } else
                    params = getParamsFromInput(userInput);

                // get response and print resultsRequest request = new
                // Request(Constants.PROTOCOL_VERSION, userInput, params);
                Request request = new Request(Constants.PROTOCOL_VERSION, userInput, params);
                sendRequestToTheServer(connection, request);

                Response response = readResponseFromTheServer(connection);
                System.out.println("RawResponse : " + response.getResult());
                System.out.println("Results : " + response.getData());
            }
        } catch (IOException e) {
            System.out.println("Connection fail : " + e.getMessage());
        }

    }

    static Params getParamsFromInput(String act) {
        String filename = null;
        String content = null;

        if (act.equals(Action.WRITE)) {
            System.out.print("Type Filename: ");
            Scanner sc1 = new Scanner(System.in);
            filename = sc1.nextLine();
            System.out.print("Type Content:");
            Scanner sc2 = new Scanner(System.in);
            content = sc2.nextLine();

        } else if (act.equals(Action.VIEW)) {
            System.out.print("Type Filename: ");
            Scanner sc1 = new Scanner(System.in);
            filename = sc1.nextLine();

        }
        return new Params(filename, content);
    }

    static void sendRequestToTheServer(Socket connection, Request request) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        String encypted = Constants.encrypteString(request.toRawString());
        printWriter.write(request.toRawString() + "\r\n");
        printWriter.flush();
        System.out.println("Raw Request: " + request.toRawString());
        System.out.println("Requested: " + encypted);
    }

    static Response readResponseFromTheServer(Socket connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String encypted = scanner.nextLine();
        System.out.println("Response : " + encypted);
        String rawResponse = Constants.decrypteString(encypted);
        return Response.fromRawResponse(rawResponse);
    }

    static void readKeyFromTheServer(Socket connection) throws IOException {
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream);
        String rawResponse = scanner.nextLine();
        Response respone = Response.fromRawResponse(rawResponse);
        String key = respone.getData();
        Constants.storeKey(key);
        System.out.println("Your key is :  " + Constants.loadKey());

    }

}