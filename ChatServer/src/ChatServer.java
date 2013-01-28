import java.net.*;
import java.util.*;

import ChatProtocol.*;

public class ChatServer {
    private ArrayList<ClientHandler> clients;

    private final int localPortNumber;

    public ChatServer(int port) {
        printServerMessage("Starting Server on port "+port+" ...");
        this.localPortNumber = port;
        clients = new ArrayList<ClientHandler>();

        try {
            ServerSocket serverSocket = new ServerSocket(this.localPortNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept(); //this blocks until a connection is made

                //Print server message announcing new client
                printServerMessage("New Connection from "+clientSocket.getInetAddress()+":"+clientSocket.getPort());

                //create new client
                ClientHandler newClient = new ClientHandler(clientSocket, this);

                //add new client to list of all clients
                clients.add(newClient);

                //Tell all connected clients about the new client
                String messageContent = newClient.getName()+" connected";
                String rawMessage = ChatProtocol.Protocol.formatAsType(messageContent,
                        ChatProtocol.MessageType.INFO);

                distributeMessage(rawMessage);

                //start new client in a thread
                Thread t = new Thread(newClient);
                t.setName("clientHandlerThread");
                t.start();

            }
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("Starting Server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("\n#################################################");
        }
    }

    /**
     * This function will only print a message to server terminal window.
     * (it will be formatted a little different thant just stdout)
     * It will NOT distribute that message in any way to its clients!
     * 
     * This function is used to announce general things that happened
     * like a client that is sending garbage (which you don't want to
     * send to all clients)
     * 
     * @param text the string to print
     */
    private void printServerMessage(String text) {
        System.out.println("\t\t"+text);
    }

    /**
     * This function will be called by a ClientHandler if it receives a
     * message.
     * 
     * This function determines the type of the passed message and
     * decide what to do with it.
     * 
     * For example:
     *  - chat messages will be distributed to all other clients.
     *  - change-name requests will change the clients name
     *    and clients will be notified of that.
     * 
     * @param rawMessage    the receivedMessage (for example <CHAT>Hi!</Chat>)
     * @param sendingClient the ClientHandler which received the message
     */
    public synchronized void receiveMessage(String rawMessage, ClientHandler sendingClient) {
        System.out.println(rawMessage+"\t(INCOMING)");

        ChatProtocol.MessageType type = ChatProtocol.Protocol.getMessageType(rawMessage);

        if (type == ChatProtocol.MessageType.CHAT) {
            //We need to "unpack" the message content, then add the nick of
            //the sending client and then "repack" (reformat) the message again.

            String messageContent = ChatProtocol.Protocol.getMessageContent(rawMessage);

            String messageToSend = ChatProtocol.Protocol.formatAsType(
                    sendingClient.getName()+": "+messageContent,
                    ChatProtocol.MessageType.CHAT);

            distributeMessage(messageToSend);
        } else if (type == ChatProtocol.MessageType.CHANGENAME) {

            System.out.println("(type == changename) == true");
            sendingClient.setName(ChatProtocol.Protocol.getMessageContent(rawMessage));
        } else {
            printServerMessage("don't know what to do with this message");
        }
    }

    /**
     * This function just sends a given message to all clients.
     * The given message has to be formated correctly!
     * 
     * @param rawMessage The (correctly formated) message to send
     */
    public synchronized void distributeMessage(String rawMessage) {
        System.out.println(rawMessage+"\t(OUTGOING)");

        //Tell every client what happened
        for (ClientHandler client : clients) {
            client.writeToClient(rawMessage);
        }
    }

    /**
     * This function will be called by a ClientHandler if it discovers
     * that its client disconnected.
     * 
     * This function will remove the associated ClientHandler from the
     * list of ClientHandlers and notify all other clients of his exit.
     * 
     * @param disconnectedClient the client to disconnect
     */
    public void disconnectClient(ClientHandler disconnectedClient) {
        String messageContent = disconnectedClient.getName()+" disconnected";
        printServerMessage(messageContent);

        String rawMessage = ChatProtocol.Protocol.formatAsType(messageContent,
                ChatProtocol.MessageType.INFO);
        distributeMessage(rawMessage);

        this.clients.remove(disconnectedClient);
    }

    public void disconnectClientWithReason(ClientHandler client, String reason) {

    }
}