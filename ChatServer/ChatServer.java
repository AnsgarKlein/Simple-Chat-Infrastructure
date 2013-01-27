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

public class ChatServer implements MessageDistributor {
    private ArrayList<ClientHandler> clients;

    private final int localPortNumber;

    public ChatServer(int port) {
        System.out.println("Starting Server on port "+port+" ...");
        this.localPortNumber = port;
        clients = new ArrayList<ClientHandler>();

        try {
            ServerSocket serverSocket = new ServerSocket(this.localPortNumber);

            while (true) {
                Socket clientSocket = serverSocket.accept(); //this blocks until a connection is made

                //Create new client
                ClientHandler newClient = new ClientHandler(clientSocket, this);

                //add new client to list of all clients
                clients.add(newClient);

                //start new client in a thread
                new Thread(newClient).start();

                printServerMessage("Found Connection (socket:"+clientSocket.getPort()+")- Starting new thread"); //debug
            }
        } catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("Starting Server - Exception - Stack Trace:");
            System.err.println(exc.getMessage());
            System.err.println("\n#################################################");
        }
    }
    
    private void printServerMessage(String str) {
        System.out.println("\t\t<SYSTEM>\t"+str);
    }
    
    public void distributeMessage(String message) {
        printServerMessage("distributing message to "+clients.size()+" child ... ");
        System.out.flush();
        for (ClientHandler client : clients) {
            client.writeToClient(message);
        }
        printServerMessage("distribution done");
    }
}
