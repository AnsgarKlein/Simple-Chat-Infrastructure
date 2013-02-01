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

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

public class SwingUI implements UI {
    private Backend clientBackend;

    private JTextField sendingTextArea;
    private JTextPane incomingTextArea;

    private Style chatStyle;
    private Style systemStyle;

    public SwingUI() {
        buildGui();

        this.clientBackend = new Backend(this, "127.0.0.1", 5000);
    }

    private void buildGui() {
        //Create Frame
        final JFrame frame = new JFrame("Chat Client");
        frame.setMinimumSize(new Dimension(200, 200));
        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create MainPanel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        //Create Toolbar menu
        JMenuItem menuItem = new JMenuItem("Change Nickname");
        menuItem.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    //open dialog to change nickname

                    String newNick = (String)JOptionPane.showInputDialog(frame,
                            "Enter new nickname:",          //message
                            "Change Nick",                  //window title
                            JOptionPane.PLAIN_MESSAGE,      //or JOptionPane.QUESTION_MESSAGE
                            null,                           //Icon
                            null,                           //possibilities (String[])
                            "");                            //default value

                    if ( (newNick != null) && (newNick.length() > 0) ) {
                        clientBackend.sendNickChangeRequest(newNick);
                    }
                    //actionPerformed(null);
                }
            });

        JMenu menu = new JMenu("Chat");
        menu.add(menuItem);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        frame.setJMenuBar(menuBar);

        //Create "Incoming Part"
        incomingTextArea = new JTextPane();
        incomingTextArea.setEditable(false);

        chatStyle = incomingTextArea.addStyle("chatStyle", null);
        StyleConstants.setForeground(chatStyle, Color.BLACK);

        systemStyle = incomingTextArea.addStyle("systemStyle", null);
        StyleConstants.setForeground(systemStyle, Color.GRAY);
        StyleConstants.setItalic(systemStyle, true);

        JScrollPane fScroller = new JScrollPane(incomingTextArea);
        fScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        //Create "Send Part"
        JPanel outgoingPanel = new JPanel();
        outgoingPanel.setLayout(new BoxLayout(outgoingPanel, BoxLayout.LINE_AXIS));

        sendingTextArea = new JTextField(20);
        sendingTextArea.setMaximumSize(new Dimension(sendingTextArea.getMaximumSize().width, sendingTextArea.getMinimumSize().height));
        sendingTextArea.addActionListener(new ActionListener()
            {
                @Override public void actionPerformed(ActionEvent ev) {
                    clientBackend.sendChatMessage(sendingTextArea.getText());
                    sendingTextArea.setText("");
                    sendingTextArea.requestFocus();
                }
            });

        JButton sendButton = new JButton("Send");
        sendButton.setMaximumSize(new Dimension(sendButton.getMaximumSize().width, sendButton.getMinimumSize().height));
        sendButton.addActionListener(new ActionListener()
            {
                @Override public void actionPerformed(ActionEvent ev) {
                    clientBackend.sendChatMessage(sendingTextArea.getText());
                    sendingTextArea.setText("");
                    sendingTextArea.requestFocus();
                }
            });

        outgoingPanel.add(Box.createRigidArea(new Dimension(5,0)));
        outgoingPanel.add(sendingTextArea);
        outgoingPanel.add(Box.createRigidArea(new Dimension(15,0)));
        outgoingPanel.add(sendButton);
        outgoingPanel.add(Box.createRigidArea(new Dimension(5,0)));

        //Put everything together & show
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(fScroller);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        mainPanel.add(outgoingPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0,5)));
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);
        
        //set focus of input field
        sendingTextArea.requestFocus();
    }

    public void displayChatMessage(String message) {
        try {
            StyledDocument doc = incomingTextArea.getStyledDocument();
            doc.insertString(doc.getLength(), message+"\n", chatStyle);
        } catch (BadLocationException exc) {}
    }

    public void displaySystemMessage(String message) {
        try {
            StyledDocument doc = incomingTextArea.getStyledDocument();
            doc.insertString(doc.getLength(), message+"\n", systemStyle);
        } catch (BadLocationException exc) {}
    }
}
