import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(5454)) {
            System.out.println("Server starting...");
            Socket socket = serverSocket.accept();
            System.out.println("New connection from " + socket.getRemoteSocketAddress());

            socket.close(); // Close socket when done
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
