package main.java.client;

/**
 * This enum defines a set of commands for a chat application along with their descriptions
 */
public enum Commands {
    QUIT ("User to exit the chat by typing /quit."),
    LIST ("List all the connected clients by typing /list."),
    WHISPER ("Send a message to a specific user by typing /whisper <username> <message>."),
    NAME ("Change your displayed name by typing /name <new-username>."),
    HELP ("List all the available commands by typing /help.");

    private String description;

    Commands(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
