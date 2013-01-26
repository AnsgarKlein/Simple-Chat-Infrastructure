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
import java.util.*;

public class ChatServer {
    private ArrayList<PrintWriter> clientOutputStreams;

    private final int localPortNumber;

    public class ClientHandler implements Runnable {
        Socket socket;
        BufferedReader reader;

        public ClientHandler(Socket clientSocket) {
            try {
                socket = clientSocket;
                InputStreamReader isReader = new InputStreamReader(socket.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception exc) {
                System.err.println("\n#################################################");
                System.err.println("Creating ClientHandler - Exception - Stack Trace:");
                System.err.println(exc.getMessage());
                System.err.println("\n#################################################");
            }
            
        }
        
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println(socket.getPort()+": "+message);
                    distributeMessage(socket.getPort()+": "+message);
                    
                    //try { Thread.currentThread().destroy(); } catch(InterruptedException exc2) { System.err.println("InterruptedException"); }
                }
            } catch (Exception exc) {
                System.err.println("\n#################################################");
                System.err.println("ClientHandler - Exception - Stack Trace:");
                System.err.println(exc.getMessage());
                System.err.println("\n#################################################");
            }
            
        }
    }

    public ChatServer(int port) {
        this.localPortNumber = port;
        
        clientOutputStreams = new ArrayList<PrintWriter>();

        try {
            ServerSocket serverSocket = new ServerSocket(this.localPortNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
                clientOutputStreams.add(writer);

                new Thread(new ClientHandler(clientSocket)).start();
                //System.out.println("Found Connection - Starting new thread"); //debug
            }
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("Starting Server - Exception - Stack Trace:");
            System.err.println(exc.getMessage());
            System.err.println("\n#################################################");
        }
    }

    private void distributeMessage(String message) {
        for (PrintWriter writer : clientOutputStreams) {
            try {
                writer.println(message);
                writer.flush();
            } catch (Exception exc) {
                System.err.println("\n#################################################");
                System.err.println("Sending Message to Clients - Exception - Strack Trace:");
                System.err.println(exc.getMessage());
                System.err.println("\n#################################################");
            }
        }
    }
}
