### TCP Concurrent Chat Server
This project implements a concurrent chat server using the TCP protocol. It allows multiple clients to connect simultaneously and engage in real-time text-based communication within a shared chat environment. Each client can send messages, change their username, list available clients, send private messages (whispers), request help, and quit the chat. The server is designed to handle multiple clients concurrently using multithreading, ensuring smooth communication and scalability. The implementation follows the client-server architecture, where the server manages client connections and facilitates message exchange among connected clients.

Key Features:

  - Concurrent handling of multiple client connections.
  
  - Support for various commands, including sending messages, changing username, listing clients, sending whispers, requesting help, and quitting the chat.
  
  - Implementation of multithreading to ensure responsiveness and scalability.
  
  - Proper resource management, including handling of sockets, input/output streams, and client disconnections.
