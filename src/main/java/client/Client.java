package main.java.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private String username;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Scanner keyboardScanner;

    /**
     * Creates a client object to connect to a server at a given address and port
     * @param serverAddress the server address
     * @param serverPort the server port
     * @param username the client's username
     */
    public Client(String serverAddress, int serverPort, String username) {

        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.keyboardScanner = new Scanner(System.in);
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
            closeEverything();
        }

        this.username = username;
    }

    /**
     * Creates and starts a new thread with the provided runnable
     * @param runnable The runnable to execute in the new thread
     */
    private void createAndStartThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * This method starts a new thread to continuously read messages from the input stream
     */
    private void readMessages() {
        createAndStartThread(new Runnable() {
            @Override
            public void run() {

                try {
                    String messageFromGroupChat;
                    while ((messageFromGroupChat = reader.readLine()) != null) {
                        System.out.println(messageFromGroupChat);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading message: " + e.getMessage());
                    closeEverything();
                }
            }
        });
    }

    /**
     * This method starts a new thread to send messages to the server
     */
    private void sendMessage() {
        createAndStartThread(new Runnable() {
            @Override
            public void run() {
                writer.println(username);

                String clientMessage;
                while(socket.isConnected()) {
                    clientMessage = keyboardScanner.nextLine();
                    writer.println(username + ": " + clientMessage);

                    if(clientMessage.contains("/name")) {
                        String newUsername = clientMessage.split(" ", 2)[1];
                        setUsername(newUsername);
                    }

                    if(clientMessage.contains("/quit")) {
                        closeEverything();
                        break;
                    }
                }
            }
        });
    }

    /**
     * Method to close socket and IO streams
     */
    public void closeEverything() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void main(String[] args) {
        Scanner keyboardScanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = keyboardScanner.nextLine();

        Client client = new Client("localhost", 8080, username);
        client.readMessages();
        client.sendMessage();
    }

}
