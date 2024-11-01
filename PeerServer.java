import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(5454)) {
            System.out.println("Server starting...");
            Socket socket = serverSocket.accept();
            System.out.println("New connection from " + socket.getRemoteSocketAddress());

            // Create input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Read message from client
            String msg = in.readLine();
            System.out.println("Received from client: " + msg);

            // Send response back to client
            out.println("Server received: " + msg);

            socket.close(); // Close socket when done
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
