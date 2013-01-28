import java.io.*;
import java.net.*;
import ChatProtocol.*;

public class Backend {
    private static final int ConnectionRetryCount = 3;

    private final String serverIPNumber;
    private final int serverPortNumber;
    private UI gui;

    private BufferedReader reader;
    private PrintWriter writer;
    private Socket serverSocket;

    public Backend(String serverIP, int serverPort) {
        this.serverIPNumber = serverIP;
        this.serverPortNumber = serverPort;

        //Create appropriate gui
        gui = new SwingUI(this);
        //gui = new CliUI(this)

        //Connect to server
        gui.displaySystemMessage("Connecting to: "+serverIP+":"+serverPort+" ... ");
        boolean isConnected = connect(serverIP, serverPort);
        if (isConnected == false) {
            //Retry a few times
            for (int i = 0; i < ConnectionRetryCount; i++) {
                if (connect(serverIP, serverPort) == true) {
                    isConnected = true;
                    break;
                }
            }

            if (!isConnected) {
                gui.displaySystemMessage("error connecting to server... exit\n");
                //System.exit(1);
            }

        }

        //thread for listening for incoming messages
        Thread t1 = new Thread() {
                @Override public void run() {
                    String message = "";

                    try {
                        while ((message = reader.readLine()) != null) {
                            
                            if (ChatProtocol.Protocol.getMessageType(message) == ChatProtocol.MessageType.CHAT) {
                                gui.displayChatMessage(ChatProtocol.Protocol.getMessageContent(message));
                            }
                            else {
                                //gui.displaySystemMessage(message);
                                gui.displaySystemMessage(ChatProtocol.Protocol.getMessageContent(message));
                            }
                            
                        }
                    }
                    catch (Exception exc) {
                        System.err.println("\n#################################################");
                        System.err.println("reading incoming message - Exception - Stack Trace:");
                        exc.printStackTrace();
                        System.err.println("#################################################\n");
                    }
                    
                    gui.displaySystemMessage("Server Disconnected");
                }
            };
        t1.setName("IncomingMessageListenerThread");
        t1.start();
    }

    /**
     * This function will format a given text as a valid chat message
     * and send it to the server.
     * 
     * @param text the text to format and send
     */
    public void sendChatMessage(String text) {
        try {
            text = ChatProtocol.Protocol.formatAsType(text, ChatProtocol.MessageType.CHAT);
            writer.println(text);
            writer.flush();
        }
        catch (Exception exc) {
            System.err.println("\n#################################################");
            System.err.println("sending message to server - Exception - Stack Trace:");
            exc.printStackTrace();
            System.err.println("#################################################\n");
        }
    }

    
    /**
     * This function connects to a given server
     * 
     * @param ip   the ip address of the server to connect o
     * @param port the port the server is running on
     */
    private boolean connect(String ip, int port) {
        try {
            serverSocket = new Socket(ip, port);

            InputStreamReader streamReader = new InputStreamReader(serverSocket.getInputStream());
            reader = new BufferedReader(streamReader);

            writer = new PrintWriter(serverSocket.getOutputStream());

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
