import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class PeerServer {
    public static final String FILE_DIRECTORY = "server_files";

    public static void main(String[] args) {
        // Create directory for storing files if it doesn't exist
        File directory = new File(FILE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        try (ServerSocket serverSocket = new ServerSocket(5454)) {
            System.out.println("Server starting...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            String command;
            while ((command = in.readLine()) != null) {
                switch (command.toUpperCase()) {
                    case "SF":  // Send File
                        out.println("ACK");
                        receiveFile(socket);
                        out.println("File received successfully");
                        break;
                    case "RF":  // Request File
                        String fileName = in.readLine(); // Read the file name requested by the client
                        System.out.println("Client requested file: " + fileName);
                        sendFile(fileName, out);
                        break;
                    case "VF":  // View Files
                        listFiles(out);
                        break;
                    case "EXIT":
                        System.out.println("Client disconnected.");
                        return;
                    default:
                        out.println("Unknown command.");
                        break;
                }
            }
        } catch (IOException e) {
            System.err.println("Client handler exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    private void receiveFile(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String fileName = dis.readUTF();
        long fileSize = dis.readLong();

        File file = new File(PeerServer.FILE_DIRECTORY + File.separator + fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while (totalRead < fileSize && (bytesRead = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }

        System.out.println("File received: " + fileName + " (Size: " + fileSize + " bytes)");
    }

    private void sendFile(String fileName, PrintWriter out) throws IOException {
        File file = new File(PeerServer.FILE_DIRECTORY + File.separator + fileName);

        if (!file.exists()) {
            out.println("File not found");
            System.out.println("File not found on server: " + fileName);
            return;
        }

        out.println("File found. Sending...");
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF(file.getName());
        dos.writeLong(file.length());

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) > 0) {
                dos.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("File sent: " + fileName);
    }

    private void listFiles(PrintWriter out) {
        File directory = new File(PeerServer.FILE_DIRECTORY);
        File[] files = directory.listFiles();
        List<String> fileNames = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    fileNames.add(file.getName());
                }
            }
        }

        if (fileNames.isEmpty()) {
            out.println("No files available on the server.");
        } else {
            out.println("Available files on the server:");
            for (String fileName : fileNames) {
                out.println("- " + fileName);
            }
        }
        out.println(""); // Send an empty line as a delimiter
    }
}
