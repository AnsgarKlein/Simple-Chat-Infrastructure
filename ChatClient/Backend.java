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

public class Backend {
    private static final int ConnectionRetryCount = 5;

    private final int serverPortNumber;
    private ChatGui gui;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket serverSocket;

    public Backend(String serverIP, int serverPort) {
        this.serverPortNumber = serverPort;
        
        //Create appropriate gui
        gui = new ChatGuiSwing(this);
        //gui = new ChatClientGuiCLI(this)

        //Connect to server
        gui.displaySystemMessage("Connecting to: "+serverIP+":"+serverPort+" ... ");
        boolean isConnected = connect(serverIP, serverPort);
        if ( isConnected == false) {
            gui.displaySystemMessage("failed");

            //Retry a few times
            for (int i = 0; i < ConnectionRetryCount; i++) {
                gui.displaySystemMessage("retrying ... ");
                if (connect(serverIP, serverPort) == true) {
                    gui.displaySystemMessage("success");
                    isConnected = true;
                    break;
                }
                System.out.println("failed");
            }

            if (!isConnected) {
                gui.displaySystemMessage("error connecting to server... exit\n");
                //System.exit(1);
            }

        } else {
            gui.displaySystemMessage("success");
        }

        //thread for listening for incoming messages
        new Thread() {
            @Override public void run() {
                String message = "";

                try {
                    while ((message = reader.readLine()) != null) {
                        gui.displayChatMessage(message);
                        //System.out.println(message); //debug!
                    }
                }
                catch (Exception exc) {
                    System.err.println("\n#################################################");
                    System.err.println("reading incoming message - Exception - Stack Trace:");
                    exc.printStackTrace();
                    System.err.println("#################################################\n");
                }
            }
        }.start();
    }

    public void sendMessage(String msg) {
        try {
            writer.println(msg);
            writer.flush();
            
            //REMOVE THIS!
            if (writer.checkError() == true) {
                gui.displaySystemMessage("something is wrong in 'writer' - disconnect ?");
            }
        }
        catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("sending message to server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");
        }
    }

    private boolean connect(String ip, int port) {
        try {
            serverSocket = new Socket(ip, port);

            InputStreamReader streamReader = new InputStreamReader(serverSocket.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(serverSocket.getOutputStream());
            //System.out.println("connected to server successfully"); //debug

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
