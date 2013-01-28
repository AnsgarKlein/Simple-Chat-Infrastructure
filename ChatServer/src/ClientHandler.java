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

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private ChatServer server;

    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    private String clientName;

    public ClientHandler(Socket clientSocket, ChatServer server) {
        this.clientSocket = clientSocket;
        this.server = server;

        //Set default (random) name
        java.util.Random generator = new java.util.Random();
        int random = generator.nextInt(8999)+1000;
        this.clientName = "unknown"+random;

        try {
            InputStreamReader isReader = new InputStreamReader(clientSocket.getInputStream());
            reader = new BufferedReader(isReader);

            writer = new PrintWriter(clientSocket.getOutputStream());
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("Creating ClientHandler - Exception - Stack Trace:");
            System.err.println(exc.getMessage());
            System.err.println("\n#################################################");
        }

    }

    /**
     * First a client handler gets created and then this function will be called
     * inside a new thread.
     * 
     * It only starts the function to listen for incoming messages from the client
     */
    public void run() {
        listenIncomingMessages();
    }

    /**
     * Returns the name of the client which is associated with this ClientHandler
     * 
     * @return the name of the client
     */
    public String getName() {
        return clientName;
    }

    /**
     * Change the name of the client which is associated with this clientHandler
     * to the given value.
     * 
     * This will not check if the name already exists or something like that.
     * 
     * @param newNick the new name to set the name to
     */
    public void setName(String newNick) {
        String rawMessage = ChatProtocol.Protocol.formatAsType(
                this.clientName+" is now known as "+newNick,
                ChatProtocol.MessageType.INFO);

        System.out.println(rawMessage);
        this.server.distributeMessage(rawMessage);

        this.clientName = newNick;
    }
    
    /**
     * This function is called directly (in an own thread) after the ClientHandler
     * has been created. (and a client connected)
     * 
     * It just listenes for incoming messages and when it receives one it passes it
     * to the Server, which decides what to do with it.
     */
    private void listenIncomingMessages() {
        String rawMessage;
        try {
            while ((rawMessage = reader.readLine()) != null) {
                server.receiveMessage(rawMessage, this);

                Thread.currentThread().yield();
            }
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("ClientHandler - Exception - Stack Trace:");
            System.err.println(exc.getMessage());
            System.err.println("\n#################################################");
        }

        //if the while loop breaks it means the reader returned null
        //which means the client has disconnected.
        server.disconnectClient(this);
    }

    /**
     * This function will write a message to its client
     * Note: This function does NOT format the given message in any way
     * 
     * @param rawMessage the message to send
     */
    public void writeToClient(String rawMessage) {
        try {
            writer.println(rawMessage);
            writer.flush();
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("Sending Message to Client "+clientSocket.getPort()+" - Exception - Strack Trace:");
            System.err.println(exc.getMessage());
            System.err.println("\n#################################################");
        }
    }


}
