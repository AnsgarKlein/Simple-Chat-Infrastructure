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