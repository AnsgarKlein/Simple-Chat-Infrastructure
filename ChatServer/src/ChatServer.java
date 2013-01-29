/**
 * Copyright (c) 2013, Ansgar Klein
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies, 
 * either expressed or implied, of the FreeBSD Project.
 */

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
            //Extract new nick
            String newNick = ChatProtocol.Protocol.getMessageContent(rawMessage);

            //create info message
            String rawInfoMessage = ChatProtocol.Protocol.formatAsType(
                    sendingClient.getName()+" is now known as "+newNick,
                    ChatProtocol.MessageType.INFO);

            //print simple info message to server terminal
            System.out.println(sendingClient.getName()+" --> "+newNick);

            //actually change the nick
            sendingClient.setName(newNick);

            //distribute the info message (which we created before)
            distributeMessage(rawInfoMessage);
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
