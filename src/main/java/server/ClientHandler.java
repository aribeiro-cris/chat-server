package main.java.server;
import main.java.client.Commands;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;
    public static CopyOnWriteArrayList<ClientHandler> availableClients = new CopyOnWriteArrayList<>();
    private final Commands[] commands = Commands.values();

    /**
     * This method initializes a ClientHandler object to handle communication with a client connected via the provided socket
     * @param socket the client's socket
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new PrintWriter(socket.getOutputStream(), true);
            this.username = reader.readLine();
            availableClients.add(this);

            broadcastMessage("SERVER: " + username + " has joined the chat!");
        } catch (IOException e) {
            closeEverything();
        }
    }

    /**
     * This method defines the behavior for handling communication with a client within a separate thread
     */
    @Override
    public void run() {
        String messageClient;

        while (socket.isConnected()) {
            try {
                messageClient = reader.readLine();

                if (messageClient == null) {
                    closeEverything();
                    break;
                }

                if (!executeCommand(messageClient)) {
                    broadcastMessage(messageClient);
                }

            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    private boolean executeCommand(String messageClient) throws IOException {
        if (messageClient.equalsIgnoreCase(username + ": /quit")) {
            closeEverything();
            return true;

        } else if (messageClient.equalsIgnoreCase(username + ": /list")) {
            executeListCommand();
            return true;

        } else if (messageClient.contains(username + ": /whisper")) {
            executeWhisperCommand(messageClient);
            return true;

        } else if (messageClient.contains(username + ": /name")) {
            executeNameCommand(messageClient);
            return true;

        } else if (messageClient.equalsIgnoreCase(username + ": /help")) {
            executeHelpCommand();
            return true;
        }
        return false;
    }

    /**
     * This method sends a message to all connected clients except the current client
     * @param message the message to broadcast
     */
    private void broadcastMessage(String message) {
        for (ClientHandler client : availableClients) {
            if (!client.username.equals(username)) {
                client.writer.println(message);
            }
        }
    }

    /**
     * This method closes all associated resources of the ClientHandler object
     */
    public void closeEverything() {
        removeClient();
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

    /**
     * This method removes the current client from the list of available clients
     */
    public void removeClient() {
        availableClients.remove(this);
        broadcastMessage("SERVER: " + username + " has left the chat!");
    }

    /**
     * This method handles the execution of the help command requested by a client
     */
    public void executeHelpCommand() {

        for (Commands command : commands) {
            writer.println(command + ": " + command.getDescription());
        }
    }

    /**
     * This method executes the list command requested by a client
     * It prints the list of currently available clients to the client who requested the list command
     */
    public void executeListCommand() {
        StringBuilder clientList = new StringBuilder("Connected clients:");

        for (ClientHandler client : availableClients) {
            clientList.append("\n").append(client.username);
        }
        writer.println(clientList);
    }

    /**
     * This method executes the whisper command requested by a client, sending a private message to another specific client
     * @param messageClient the message containing the whisper command
     */
    public void executeWhisperCommand(String messageClient) {
        String[] parts = messageClient.split(" ", 4);
        String recipient = parts[2];
        String whisperMessage = parts[3];

        for (ClientHandler client : availableClients) {
            if (client.username.equals(recipient)) {
                client.writer.println(username + " whispered to you: " + whisperMessage);
                return;
            }
        }
        writer.println("User " + recipient + " not found.");
    }

    /**
     * This method executes the name command requested by a client, allowing them to change their username
     * @param messageClient the message containing the name command
     */
    public void executeNameCommand(String messageClient) {
        String newUsername = messageClient.split(" ", 3)[2];
        broadcastMessage(username + " has changed their username to " + newUsername);
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
