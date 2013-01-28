import java.io.*;

public class CliUI implements 	UI {

    private Backend clientBackend;

    public CliUI(Backend clientBackend_) {
        this.clientBackend = clientBackend_;

        //System.out.println("exiting...");
        //System.exit(0);
        new Thread(new InputListener()).start();
    }

    public void displayChatMessage(String msg) {
        System.out.println("\tMsg: "+msg+"\n");
        System.out.flush();
    }

    public void displaySystemMessage(String msg) {
        System.out.println("\tMsg: "+msg+"\n");
        System.out.flush();
    }

    public class InputListener implements Runnable {
        public void run() {
            String currentLine = "";
            InputStreamReader inputStreamReader = new InputStreamReader(System.in);
            BufferedReader inStream = new BufferedReader(inputStreamReader);

            while ( currentLine.equals("quit") == false ) {
                try {
                    currentLine = inStream.readLine();
                    clientBackend.sendChatMessage(currentLine);
                }
                catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        }
    }
}