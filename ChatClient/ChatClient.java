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

public class ChatClient {
    private static final int ConnectionRetryCount = 5;

    private final int serverPortNumber;
    private ChatClientGui gui;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket serverSocket;

    public ChatClient(String serverIP, int serverPort) {
        this.serverPortNumber = serverPort;

        System.out.print("Connecting to: "+serverIP+":"+serverPort+" ... ");
        if (networkSetup(serverIP, serverPort) == false) {
            System.out.println("failed");

            //Retrying
            boolean isConnected = false;
            for (int i = 0; i < ConnectionRetryCount; i++) {
                System.out.print("retrying ... ");
                if (networkSetup(serverIP, serverPort) == true) {
                    System.out.println("success");
                    isConnected = true;
                    break;
                }
                System.out.println("failed");
            }

            if (!isConnected) {
                System.out.println("error connecting to server... exiting\n");
                System.exit(1);
            }
            
        } else {
            System.out.println("success");
        }

        //Create appropriate gui
        gui = new ChatClientGuiSwing(this);
        //gui = new ChatClientGuiCLI(this);

        //thread for listening for incoming messages
        /**Thread incomingReaderThread = new Thread(new IncomingMessageReader());
        incomingReaderThread.start();**/
        new Thread() {
            @Override
            public void run() {
                String message = "";

                try {
                    while ((message = reader.readLine()) != null) {
                        System.out.println(message); //debug!
                        gui.displayMessage(message+"\n");
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
            if (writer.checkError() == true) {
                System.err.println("something is wrong in 'writer'");
            }
        }
        catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("sending message to server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");
        }
    }

    private boolean networkSetup(String ip, int port) {
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
