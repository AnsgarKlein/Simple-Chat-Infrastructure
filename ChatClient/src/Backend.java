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
import ChatProtocol.*;

public class Backend {
    private static final int ConnectionRetryCount = 3;

    private final String serverIPNumber;
    private final int serverPortNumber;
    private UI gui;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket serverSocket;

    public Backend(String serverIP, int serverPort) {
        this.serverIPNumber = serverIP;
        this.serverPortNumber = serverPort;

        //Create appropriate gui
        gui = new SwingUI(this);
        //gui = new CliUI(this)

        //Connect to server
        gui.displaySystemMessage("Connecting to: "+serverIP+":"+serverPort+" ... ");
        boolean isConnected = connect(serverIP, serverPort);
        if (isConnected == false) {
            //Retry a few times
            for (int i = 0; i < ConnectionRetryCount; i++) {
                if (connect(serverIP, serverPort) == true) {
                    isConnected = true;
                    break;
                }
            }

            if (!isConnected) {
                gui.displaySystemMessage("error connecting to server... exit\n");
                //System.exit(1);
            }

        }

        //thread for listening for incoming messages
        Thread t1 = new Thread() {
                @Override public void run() {
                    String message = "";

                    try {
                        while ((message = reader.readLine()) != null) {

                            if (ChatProtocol.Protocol.getMessageType(message) == ChatProtocol.MessageType.CHAT) {
                                gui.displayChatMessage(ChatProtocol.Protocol.getMessageContent(message));
                            }
                            else {
                                //gui.displaySystemMessage(message);
                                gui.displaySystemMessage(ChatProtocol.Protocol.getMessageContent(message));
                            }

                        }
                    }
                    catch (Exception exc) {
                        System.err.println("\n#################################################");
                        System.err.println("reading incoming message - Exception - Stack Trace:");
                        exc.printStackTrace();
                        System.err.println("#################################################\n");
                    }

                    gui.displaySystemMessage("Server Disconnected");
                }
            };
        t1.setName("IncomingMessageListenerThread");
        t1.start();
    }

    /**
     * This function will format a given text as a valid chat message
     * and send it to the server.
     * 
     * @param text the text to format and send
     */
    public void sendChatMessage(String text) {
        String rawMessage = ChatProtocol.Protocol.formatAsType(text, ChatProtocol.MessageType.CHAT);
        sendMessage(rawMessage);
    }
    
    public void sendNickChangeRequest(String newNick) {
        String rawMessage = ChatProtocol.Protocol.formatAsType(newNick, ChatProtocol.MessageType.CHANGENAME);
        sendMessage(rawMessage);
    }

    private void sendMessage(String rawMessage) {
        try {
            writer.println(rawMessage);
            writer.flush();
        }
        catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("sending message to server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("Message:");
            System.err.println(rawMessage);
            System.err.println("#################################################\n");
        }
    }

    /**
     * This function connects to a given server
     * 
     * @param ip   the ip address of the server to connect o
     * @param port the port the server is running on
     */
    private boolean connect(String ip, int port) {
        try {
            serverSocket = new Socket(ip, port);

            InputStreamReader streamReader = new InputStreamReader(serverSocket.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(serverSocket.getOutputStream());

            return true;
        }
        catch (ConnectException exc) {
            //The server simply is not reachable
            return false;
        }
        catch (IOException exc) {
            System.err.println("\n#################################################");
            System.err.println("Connecting to server - IOException - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");

            return false;
        }
    }

}
