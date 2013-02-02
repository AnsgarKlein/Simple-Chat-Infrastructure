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
import java.awt.GridLayout;
//import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.Inet4Address;

public class SwingConnectDialog extends JFrame implements ActionListener {
    JTextField ipField;
    JTextField portField;

    public SwingConnectDialog(String ip, int port) {
        if (ip != null && port != 0 && isValidIP(ip)) {
            new SwingUI(ip, port);
            return;
        }

        buildGui();
    }

    private boolean isValidIP(String str) {
        String[] parts = str.split("\\.");
        for (String s : parts) {
            int i = Integer.parseInt(s);
            if (i < 0 || i > 255) {
                return false;
            }
        }
        return true;
    }

    private void buildGui() {
        //setup
        this.setSize(300, 110);
        this.setResizable(false);

        //setup main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(2, 2));
        mainPanel.add(gridPanel);

        //setup components
        JLabel ipLabel = new JLabel("IP:");
        gridPanel.add(ipLabel);

        ipField = new JTextField();
        ipField.setText("127.0.0.1");
        gridPanel.add(ipField);

        JLabel portLabel = new JLabel("PORT:");
        gridPanel.add(portLabel);

        portField = new JTextField();
        portField.setText("5000");
        gridPanel.add(portField);

        //add button
        JButton applyButton = new JButton("Start");
        applyButton.addActionListener(this);
        mainPanel.add(applyButton);

        //Put everything together & show
        this.getContentPane().add(mainPanel);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent ev) {
        String ip = ipField.getText();
        String portString = portField.getText();

        if (!portString.isEmpty() && !ip.isEmpty()) {
            int port = Integer.parseInt(portString);

            new SwingUI(ip, port);

            this.dispose();
        }
    }
}
