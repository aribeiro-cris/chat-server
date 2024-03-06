package main.java.server;

import main.java.client.ClientHandler;
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

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Chat Server is running...");
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();

                clients = new ClientHandler(clientSocket);
                System.out.println(clients.getUsername() + " has connected.");

                Thread thread = new Thread(clients);
                thread.start();

            } catch (IOException e) {
                closeServerSocket();
            }
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

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        server.start();
    }
}
