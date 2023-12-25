package org.example;

public class Message {
    private MessageType type;
    private byte[] payload;

    public Message(byte[] type, byte[] payload){
        this.type = MessageType.fromValue(type[0]);
        this.payload = payload;
    }

    public MessageType getType() {
        return type;
    }

    public byte[] getPayload() {
        return payload;
    }
}
