package main.java.client;

import java.io.*;
import java.net.Socket;

public class Client {

    private String username;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private BufferedReader keyboardReader;

    public Client(String serverAddress, int serverPort, String username) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.keyboardReader = new BufferedReader(new InputStreamReader(System.in));
        this.username = username;
    }

    public void closeSocketAndBuffers() {
        try {
            if (reader != null) {
                reader.close();
            }
            if (writer != null) {
                writer.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String messageFromGroupChat;
                    while ((messageFromGroupChat = reader.readLine()) != null) {
                        System.out.println(messageFromGroupChat);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading message: " + e.getMessage());
                    closeSocketAndBuffers();
                }
            }
        }).start();
    }

    private void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    writer.write(username);
                    writer.newLine();
                    writer.flush();

                    String clientMessage;
                    while(socket.isConnected()) {
                        clientMessage = keyboardReader.readLine();
                        writer.write(username + ": " + clientMessage);
                        writer.newLine();
                        writer.flush();

                        if(clientMessage.contains("/name")) {
                            String newUsername = clientMessage.split(" ")[1];
                            setUsername(newUsername);
                        }

                        if(clientMessage.contains("/quit")) {
                            closeSocketAndBuffers();
                            break;
                        }

                    }
                } catch (IOException e) {
                    closeSocketAndBuffers();
                }
            }
        }).start();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your username for the group chat: ");
        String username = keyboardReader.readLine();

        Client client = new Client("localhost", 8080, username);
        client.readMessages();
        client.sendMessage();
    }

}
