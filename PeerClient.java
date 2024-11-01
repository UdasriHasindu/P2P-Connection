import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PeerClient {
    public static void main(String[] args) {

        try {
            System.out.println("Connecting to server...");
            Socket socket = new Socket("localhost", 5454);
            System.out.println("Connected to " + socket.getRemoteSocketAddress());

            // Get user input
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Enter the message: ");
            String message = userInput.readLine();

            // Send message to server
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println(message);

            // Receive and print server's response
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse = in.readLine();
            System.out.println("Server response: " + serverResponse);

            socket.close(); // Close socket when done
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
