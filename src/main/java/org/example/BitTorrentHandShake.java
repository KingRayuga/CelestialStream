package org.example;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class BitTorrentHandShake {
    private final String peerIp;
    private final int peerPort;
    private final byte[] infoHash;
    private final byte[] peerId;

    public BitTorrentHandShake(String peerIp, int peerPort, byte[] infoHash, byte[] peerId) {
        this.peerId = peerId;
        this.peerPort = peerPort;
        this.infoHash = infoHash;
        this.peerIp = peerIp;
    }

    public void doHandShake() {
        try (Socket socket = new Socket(peerIp, peerPort)) {

            if (doHandShake(socket)) {
                System.out.println("Hand shake successful");
            } else {
                System.out.println("Hand shake failed");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean doHandShake(Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

        String protocolIdentifier = "BitTorrent Protocol";
        byte[] protocolIdentifierBytes = protocolIdentifier.getBytes();

        byte[] reservedBytes = new byte[8];

        byte[] handShakeMessage = new byte[68];

        System.arraycopy(protocolIdentifierBytes, 0, handShakeMessage, 0, 19);
        System.arraycopy(reservedBytes, 0, handShakeMessage, 19, 8);
        System.arraycopy(infoHash, 0, handShakeMessage, 27, 20);
        System.arraycopy(peerId, 0, handShakeMessage, 47, 20);

        dataOutputStream.write(handShakeMessage);

        byte[] response = new byte[68];
        dataInputStream.readFully(response);

        return verifyHandShakeResponse(response);

    }

    private boolean verifyHandShakeResponse(byte[] response) {
        byte[] recievedInfoHash = new byte[20];
        System.arraycopy(response, 28, recievedInfoHash, 0, 20);

        boolean infoHashMatch = Arrays.equals(recievedInfoHash, infoHash);

        if (!infoHashMatch) {
            System.out.println("infoHash mismatch");
            return false;
        }

        return true;

    }
}
