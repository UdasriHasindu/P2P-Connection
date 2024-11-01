import java.io.IOException;
import java.net.Socket;

public class PeerClient {
    public static void main(String[] args) {

        try {
            System.out.println("Connecting to server...");
            Socket socket = new Socket("localhost", 5454);
            System.out.println("Connected to " + socket.getRemoteSocketAddress());


            socket.close(); // Close socket when done
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
