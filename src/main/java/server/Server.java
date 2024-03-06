package main.java.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private ServerSocket serverSocket;
    private ClientHandler clients;
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

                clients = new ClientHandler(clientSocket);
                System.out.println(clients.getUsername() + " has connected.");

                Thread thread = new Thread(clients);
                thread.start();
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void closeServerSocket() {
        if(serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.init();
    }
}
