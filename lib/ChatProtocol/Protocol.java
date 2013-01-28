package ChatProtocol; 

public class Protocol {
    /**
     * This function determines which kind of message a given raw message is
     * 
     * For example: if you pass <CHAT>fu</CHAT> to this function it will
     * return MessageType.CHAT because the message is of type CHAT
     */
    public static MessageType getMessageType(String rawMessage) {
        //<CHAT>message</CHAT>
        if (rawMessage.startsWith("<CHAT>") && rawMessage.endsWith("</CHAT>")) {
            return MessageType.CHAT;
        }

        //<CHANGENAME>new name</CHANGENAME>
        if (rawMessage.startsWith("<CHANGENAME>") && rawMessage.endsWith("</CHANGENAME>")) {
            return MessageType.CHANGENAME;
        }

        //<AUTHENTICATE>message</AUTHENTICATE>
        if (rawMessage.startsWith("<AUTHENTICATE>") && rawMessage.endsWith("</AUTHENTICATE>")) {
            return MessageType.AUTHENTICATE;
        }

        //<DISCONNECT>goodbye message</DISCONNECT>
        if (rawMessage.startsWith("<DISCONNECT>") && rawMessage.endsWith("</DISCONNECT>")) {
            return MessageType.DISCONNECT;
        }

        //<INFO>info message</INFO>
        if (rawMessage.startsWith("<INFO>") && rawMessage.endsWith("</INFO>")) {
            return MessageType.INFO;
        }

        //message is not formated correctly / does not follow specification
        return MessageType.BAD;
    }
    
    /**
     * This function extracts the content of a message from a raw message
     * 
     * For example: if you pass <CHAT>Hello, how are you?</CHAT> it will return
     * "Hello, how are you?"
     * Note that it determines the message type by itself, so passing
     * <CHAT>grabbing something to eat</CHAT> will return "grabbing something to eat".
     * 
     */
    public static String getMessageContent(String rawMessage) {
        if (getMessageType(rawMessage) == MessageType.CHAT) {
            return rawMessage.substring(6, rawMessage.length()-7);
        }

        if (getMessageType(rawMessage) == MessageType.CHANGENAME) {
            return rawMessage.substring(12, rawMessage.length()-13);
        }

        if (getMessageType(rawMessage) == MessageType.AUTHENTICATE) {
            return rawMessage.substring(14, rawMessage.length()-15);
        }

        if (getMessageType(rawMessage) == MessageType.DISCONNECT) {
            return rawMessage.substring(12, rawMessage.length()-13);
        }

        if (getMessageType(rawMessage) == MessageType.INFO) {
            return rawMessage.substring(6, rawMessage.length()-7);
        }

        return "could not read format of message properly";
    }

    
    /**
     * This function will format a raw text as a valid message of given
     * type.
     * 
     * For example: passing "user XY changed name" and MessageType.INFO
     * it will format the message as <INFO>"user XY changed name"</INFO>
     * 
     */
    public static String formatAsType(String text, MessageType type) {
        switch (type) {
            case CHAT:
            return "<CHAT>"+text+"</CHAT>";
            case CHANGENAME:
            return "<CHANGENAME>"+text+"</CHANGENAME>";
            case AUTHENTICATE:
            return "<AUTHENTICATE>"+text+"</AUTHENTICATE>";
            case DISCONNECT:
            return "<DISCONNECT>"+text+"</DISCONNECT>";
            case INFO:
            return "<INFO>"+text+"</INFO>";
            case BAD:
            default:
            return "could not format message in requested format";
        }
    }
}
