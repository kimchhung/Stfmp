import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        // Connect to the server
        try (Socket connection = new Socket("localhost", 9999)) {

            // Tell users what they can do
            System.out.println("STFMP connected to port : 9999");
            System.out.println(" - Type 'write' :  write content to the file");
            System.out.println(" - Type 'view' :  view content of the file");
            System.out.println(" - Type 'close' :  close the connection.");
            // print key
            readKeyFromTheServer(connection);
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
                } else if (userInput.equals(Action.WRITE))
                    params = new Params("file.txt", "helloworld");
                else if (userInput.equals(Action.VIEW))
                    params = new Params("file.txt", null);

                // get response and print resultsRequest request = new
                // Request(Constants.PROTOCOL_VERSION, userInput, params);
                Request request = new Request(Constants.PROTOCOL_VERSION, userInput, params);
                sendRequestToTheServer(connection, request);

                Response response = readResponseFromTheServer(connection);
                System.out.println("Response: " + response.getResult());
            }
        } catch (IOException e) {
            System.out.println("Connection fail : " + e.getMessage());
        }

    }

    static void sendRequestToTheServer(Socket connection, Request request) throws IOException {
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        String encypted = Constants.encrypteString(request.toRawString());
        printWriter.write(encypted);
        printWriter.flush();
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
        System.out.println("Your Key :  " + Constants.loadKey());

    }

}