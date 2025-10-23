// CollaborativeServer.java
import java.io.*; 
import java.net.*;
import java.util.*;

public class CollaborativeServer {
    private static final int PORT = 5000;
    private static final List<PrintWriter> clientWriters = new ArrayList<>();
    private static String documentContent = "";  // Store the current content of the document

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Collaborative Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                // Send the current document content to the new client
                out.println(documentContent);

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Update document content and broadcast to all clients
                    System.out.println("Updating document content to: " + inputLine);
                    // documentContent = inputLine;

                    // (new) not sure if thjis works
                    documentContent = inputLine.replace("__NEWLINE__", "\n");  // Restore newlines
                    synchronized (clientWriters) {
                        for (PrintWriter writer : clientWriters) {
                            writer.println(documentContent);
                        }
                    }

                }
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            } finally {
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}














