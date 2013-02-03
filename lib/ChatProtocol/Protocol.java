package ChatProtocol;

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
        if (rawMessage.startsWith("<CHANGENAMEREQUEST>") && rawMessage.endsWith("</CHANGENAMEREQUEST>")) {
            return MessageType.CHANGENAMEREQUEST;
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

        if (getMessageType(rawMessage) == MessageType.CHANGENAMEREQUEST) {
            return rawMessage.substring(19, rawMessage.length()-20);
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
            case CHANGENAMEREQUEST:
            return "<CHANGENAMEREQUEST>"+text+"</CHANGENAMEREQUEST>";
            case INFO:
            return "<INFO>"+text+"</INFO>";
            case BAD:
            default:
            return "could not format message in requested format";
        }
    }
}
