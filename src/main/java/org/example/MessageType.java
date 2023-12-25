package org.example;

public enum MessageType {
    KEEP_ALIVE(0),
    CHOKE(1),
    UNCHOKE(2),
    INTERESTED(3),
    NOT_INTERESTED(4),
    HAVE(5),
    BITFIELD(6),
    REQUEST(7),
    PIECE(8),
    CANCEL(9);

    private final byte value;

    MessageType (int value){
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
    public static MessageType fromValue(byte value) {
        for (MessageType messageType : values()) {
            if (messageType.value == value) {
                return messageType;
            }
        }
        throw new IllegalArgumentException("Invalid MessageType value: " + value);
    }


}
