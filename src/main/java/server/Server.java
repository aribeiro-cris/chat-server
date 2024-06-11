package main.java.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private ServerSocket serverSocket;

    public Server(int port) {
        this.port = port;
    }

    /**
     * This method starts a chat server, listens for incoming client connections
     * Creates a separate thread to handle each client's communication.
     */
    public void init() {

        try {

        serverSocket = new ServerSocket(port);
        System.out.println("Chat Server is running...");

        while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clients = new ClientHandler(clientSocket);
                System.out.println(clients.getUsername() + " has connected.");

                Thread thread = new Thread(clients);
                thread.start();
            }

        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
            closeServerSocket();
        }
    }

    /**
     * Closes the server socket.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.init();
    }
}
