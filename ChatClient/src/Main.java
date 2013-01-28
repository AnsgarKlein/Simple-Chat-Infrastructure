import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        setLookAndFeel("System");

        String serverIP = "127.0.0.1";
        int serverPort = 5000;
        new Backend(serverIP, serverPort);
    }

    public static void setLookAndFeel(String value) {
        try {
            //We have to do this because of a bug where UIManager.getSystemLookAndFeelClassName()
            //returns "javax.swing.plaf.metal.MetalLookAndFeel" (the cross platform look)
            if (value == "System") {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                } catch (Exception exc) {
                    try {
                        UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    } catch (Exception exc2) {
                        try {
                            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                        } catch (Exception exc3) {
                            try {
                                UIManager.setLookAndFeel("com.sun.java.swing.plaf.mac.MacLookAndFeel");
                            } catch (Exception exc4) {
                                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                            }
                        }
                    }
                }

            } else if (value == "Java") {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } else {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            }
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("UnsupportedLookAndFeelException");
            System.err.println(e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException");
            System.err.println(e.getMessage());
        } catch (InstantiationException e) {
            System.err.println("InstantiationException");
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("IllegalAccessException");
            System.err.println(e.getMessage());
        }
    }
}