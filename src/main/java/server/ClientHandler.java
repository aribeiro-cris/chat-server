package main.java.server;
import main.java.client.Commands;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String username;
    public static CopyOnWriteArrayList<ClientHandler> availableClients = new CopyOnWriteArrayList<>();
    private Commands[] commands = Commands.values();

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = reader.readLine();
            availableClients.add(this);

            broadcastMessage("SERVER: " + username + " has joined the chat!");
        } catch (IOException e) {
            closeSocketAndBuffers();
        }
    }

    @Override
    public void run() {
        String messageClient;

        while (socket.isConnected()) {
            try {
                messageClient = reader.readLine();

                if (messageClient.equalsIgnoreCase(username + ": /quit")) {
                    System.out.println(username + " terminated connection.");
                    closeSocketAndBuffers();
                    break;
                } else if (messageClient.equalsIgnoreCase(username + ": /list")) {
                    listCommand();
                    continue;
                } else if (messageClient.contains(username + ": /whisper")) {
                    whisperCommand(messageClient);
                    continue;
                } else if (messageClient.contains(username + ": /name")) {
                    nameCommand(messageClient);
                    continue;
                } else if (messageClient.equalsIgnoreCase(username + ": /help")) {
                    helpCommand();
                    continue;
                }

                broadcastMessage(messageClient);
            } catch (IOException e) {
                closeSocketAndBuffers();
                break;
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler client : availableClients) {
            try {
                if (!client.username.equals(username)) {
                    client.writer.write(message);
                    client.writer.newLine();
                    client.writer.flush();
                }
            } catch (IOException e) {
                closeSocketAndBuffers();
                break;
            }
        }
    }

    public void closeSocketAndBuffers() {
        removeClient();
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

    public void removeClient() {
        availableClients.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat!");
    }

    public void helpCommand() throws IOException {
        System.out.println(username + " asked for the help command.");

        for (ClientHandler client : availableClients) {
            if (client.username.equals(username)) {
                for (Commands command : commands) {
                    client.writer.write(command + ": " + command.getDescription());
                    client.writer.newLine();
                    client.writer.flush();
                }
            }
        }
    }

    public void listCommand() throws IOException {
        System.out.println(username + " asked for the list command.");

        for (ClientHandler client : availableClients) {
            if (client.username.equals(username)) {
                client.writer.write(availableClients.toString());
                client.writer.newLine();
                client.writer.flush();
            }
        }
    }

    public void whisperCommand(String messageClient) throws IOException {
        String usernameToSendWhisper = messageClient.split(" ")[2]; //to make it better, index 2 is the substring after whisper
        System.out.println(username + " wants to send a private message to " + usernameToSendWhisper);

        int aux = messageClient.indexOf(usernameToSendWhisper); //auxiliary var to find the first index of the usernameToSendWhisper
        String whisper = messageClient.substring(aux + usernameToSendWhisper.length() + 1);

        for (ClientHandler client : availableClients) {
            if (client.username.equals(usernameToSendWhisper)) {
                client.writer.write(username + " whispered to you: " + whisper);
                client.writer.newLine();
                client.writer.flush();
            }
        }
    }

    public void nameCommand(String messageClient) {
        String newUsername = messageClient.split(" ")[2]; //to make it better, index 2 is the substring after name
        System.out.println(username + " has changed the username to " + newUsername);
        broadcastMessage(username + " has changed the username to " + newUsername);
        setUsername(newUsername);
    }

    @Override
    public String toString() {
        return "Username: '" + username + "'";
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
