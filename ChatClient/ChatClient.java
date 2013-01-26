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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatClient {
    private final int serverPortNumber;

    private JTextArea incomingTextArea;
    private JTextField sendingTextArea;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public class SendListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                writer.println(sendingTextArea.getText());
                writer.flush();
            }
            catch (Exception exc) {
                System.err.println("\n#################################################");
                System.err.println("sending message to server - Exception - Stack Trace:");
                exc.printStackTrace();
                System.err.println("#################################################\n");
            }
            sendingTextArea.setText("");
            sendingTextArea.requestFocus();
        }
    }

    public void send(String msg) {
        try {
            writer.println(msg);
            writer.flush();
        }
        catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("sending message to server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");
        }
    }

    public class IncomingMessageReader implements Runnable {
        public void run() {
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    //System.out.println("gelesen "+message); //debug!
                    incomingTextArea.append(message+"\n");
                }
            }
            catch (Exception exc) {
                System.err.println("\n#################################################");
                System.err.println("reading incoming message - Exception - Stack Trace:");
                exc.printStackTrace();
                System.err.println("#################################################\n");
            }
        }
    }

    public ChatClient(String serverIP, int serverPort) {
        this.serverPortNumber = serverPort;

        if (networkSetup(serverIP, serverPort) == false) {
            System.err.println("error connecting to server... exiting\n");
            System.exit(1);
        }

        buildGui();

        Thread incomingReaderThread = new Thread(new IncomingMessageReader());
        incomingReaderThread.start();
    }

    private void buildGui() {
        //Create Frame
        JFrame frame = new JFrame("Chat Client");
        frame.setSize(400, 500);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create MainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        //Create "Incoming Part"
        incomingTextArea = new JTextArea(15, 20);
        incomingTextArea.setLineWrap(true);
        incomingTextArea.setWrapStyleWord(true);
        incomingTextArea.setEditable(false);
        JScrollPane fScroller = new JScrollPane(incomingTextArea);
        fScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //Create "Send Part"
        JPanel outgoingPanel = new JPanel();
        outgoingPanel.setLayout(new BoxLayout(outgoingPanel, BoxLayout.LINE_AXIS));

        sendingTextArea = new JTextField(20);
        sendingTextArea.setMaximumSize(new Dimension(sendingTextArea.getMaximumSize().width, sendingTextArea.getMinimumSize().height));
        sendingTextArea.addActionListener(new SendListener());

        JButton sendButton = new JButton("Send");
        sendButton.setMaximumSize(new Dimension(sendButton.getMaximumSize().width, sendButton.getMinimumSize().height));
        sendButton.addActionListener(new SendListener());

        outgoingPanel.add(Box.createRigidArea(new Dimension(5,0)));
        outgoingPanel.add(sendingTextArea);
        outgoingPanel.add(Box.createRigidArea(new Dimension(15,0)));
        outgoingPanel.add(sendButton);
        outgoingPanel.add(Box.createRigidArea(new Dimension(5,0)));

        //Put everything together
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(fScroller);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(outgoingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        frame.getContentPane().add(mainPanel);
    }

    private boolean networkSetup(String ip, int port) {
        try {
            socket = new Socket(ip, port);

            InputStreamReader streamReader = new InputStreamReader(socket.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(socket.getOutputStream());
            //System.out.println("connected to server successfully"); //debug

            return true;
        }
        catch (IOException exc) {
            System.err.println("\n#################################################");
            System.err.println("Configuring Network - IOException - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");

            return false;
        }
    }

}
