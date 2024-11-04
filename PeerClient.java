import java.io.*;
import java.net.*;
import java.util.Scanner;

public class PeerClient {
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to server...");
            Socket socket = new Socket("localhost", 5454);
            System.out.println("Connected to " + socket.getRemoteSocketAddress());

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            String command;
            while (true) {
                System.out.println("Choose an option: send_file, request_file, view_files, exit");
                command = scanner.nextLine();
                out.println(command);

                if ("exit".equalsIgnoreCase(command)) {
                    System.out.println("Disconnecting from server...");
                    break;
                } else if ("send_file".equalsIgnoreCase(command)) {
                    String ack = in.readLine();
                    if ("ACK".equals(ack)) {
                        sendFile(socket);
                        String serverResponse = in.readLine();
                        System.out.println("Server response: " + serverResponse);
                    }
                } else if ("request_file".equalsIgnoreCase(command)) {
                    System.out.println("Enter the file name to request:");
                    String fileName = scanner.nextLine(); // Ensure the user enters the exact file name
                    out.println(fileName);

                    // Read the server's response about file's availability
                    String response = in.readLine(); 
                    if ("File not found".equals(response)) {
                        System.out.println("Server response: " + response); // Inform user if file is missing
                    } else if ("File found. Sending...".equals(response)) {
                        // Now proceed to receive the file
                        receiveFile(socket); // Call the method to receive the file
                    } else {
                        System.out.println("Unexpected response from server: " + response);
                    }
                } else if ("view_files".equalsIgnoreCase(command)) {
                    System.out.println("Server files:");
                    String response;
                    while (!(response = in.readLine()).isEmpty()) { // Stop at empty line
                        System.out.println(response);
                    }
                } else {
                    String response = in.readLine();
                    System.out.println("Server response: " + response);
                }
            }

            socket.close();
            scanner.close();
        } catch (IOException e) {
            System.err.println("Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendFile(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path to send:");
        String filePath = scanner.nextLine();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found!");
            return;
        }

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

        System.out.println("File " + file.getName() + " sent successfully.");
    }

    private static void receiveFile(Socket socket) throws IOException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String serverFileName = dis.readUTF();
        long fileSize = dis.readLong();

        try (FileOutputStream fos = new FileOutputStream("client_" + serverFileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalRead = 0;

            while (totalRead < fileSize && (bytesRead = dis.read(buffer)) > 0) {
                fos.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
            }
        }

        System.out.println("File received: " + serverFileName + " (Size: " + fileSize + " bytes)");
    }
}
