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

    public SwingUI(Backend clientBackend) {
        this.clientBackend = clientBackend;

        buildGui();
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
                    //open window to change nickname
                    new SwingNicknameChanger(frame);
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
                public void actionPerformed(ActionEvent ev) {
                    clientBackend.sendChatMessage(sendingTextArea.getText());
                    sendingTextArea.setText("");
                    sendingTextArea.requestFocus();
                }
            });

        JButton sendButton = new JButton("Send");
        sendButton.setMaximumSize(new Dimension(sendButton.getMaximumSize().width, sendButton.getMinimumSize().height));
        sendButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent ev) {
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
    }
}