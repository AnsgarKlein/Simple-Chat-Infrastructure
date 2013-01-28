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
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;

public class SwingNicknameChanger extends JDialog implements ActionListener {
    public SwingNicknameChanger(JFrame parentFrame) {
        //setup
        super(parentFrame, true); //make dialog modal
        this.setSize(300, 80);
        this.setResizable(false);

        //setup main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

        //setup text field
        JTextField nicknameField = new JTextField();
        nicknameField.addActionListener(this);
        nicknameField.setMaximumSize(new Dimension(nicknameField.getMaximumSize().width, nicknameField.getMinimumSize().height));

        //setup apply button
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(this);

        //Put everything together & show
        mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        mainPanel.add(nicknameField);
        mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        mainPanel.add(applyButton);
        mainPanel.add(Box.createRigidArea(new Dimension(5,0)));
        this.getContentPane().add(mainPanel);
        this.setVisible(true);
        
    }

    public void actionPerformed(ActionEvent ev) {
        this.dispose();
    }
}
