package org.example;

import org.apache.commons.lang3.ArrayUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class PieceDownloader {

    private final String peerIp;
    private final int peerPort;
    private final byte[] peerId;

    private final TorrentMetaData torrentMetaData;
    private final int index;
    private volatile boolean downloadSuccessful;
    private byte[] finalFile;

    PieceDownloader(TorrentMetaData torrentMetaData, int index, String peerIp, int peerPort, byte[] peerId, byte[] finalFile) {
        this.torrentMetaData = torrentMetaData;
        this.index = index;
        this.downloadSuccessful = false;
        this.peerIp = peerIp;
        this.peerPort = peerPort;
        this.peerId = peerId;
        this.finalFile = finalFile;
    }

    public boolean isDownloadSuccessful() {
        return downloadSuccessful;
    }

    public void downloadPiece() {
        try (Socket socket = new Socket(peerIp, peerPort)) {
            BitTorrentHandShake bitTorrentHandShake = new BitTorrentHandShake(torrentMetaData.getInfoHash(), peerId, socket);
            bitTorrentHandShake.doHandShake();

            if (!waitForUnchoke(socket)) {
                return;
            }

            byte[] pieceData = startDownload(socket);

            if (null != pieceData) {
                processPayload(pieceData);
                this.downloadSuccessful = true;
            }

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean waitForUnchoke(Socket socket) throws InterruptedException {
        long startTimeMillis = System.currentTimeMillis();
        long timeOutMillis = 100000;

        while (System.currentTimeMillis() - startTimeMillis < timeOutMillis) {
            Message recievedMessage;
            try {
                recievedMessage = recieveMessage(socket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (recievedMessage.getType() == MessageType.UNCHOKE) {
                return true;
            }
            Thread.sleep(200);
        }
        return false;
    }

    private Message recieveMessage(Socket socket) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        int length = dataInputStream.readInt();
        if (length == 0) {
            return new Message(MessageType.KEEP_ALIVE, null);
        }
        byte typeByte = dataInputStream.readByte();
        byte[] payload = new byte[length - 1];
        dataInputStream.readFully(payload);
        return new Message(typeByte, payload);
    }

    private byte[] startDownload(Socket socket) throws IOException {
        int pieceIndex = this.index;
        int pieceSize = ArrayUtils.toPrimitive(this.torrentMetaData.getPieces()).length / this.torrentMetaData.getPieceLength();
        int blockSize = 2 ^ 14;
        int numBlocks = (pieceSize + blockSize - 1) / blockSize;
        int blockOffset = 0;

        byte[] fullPiece = new byte[pieceSize];

        for (int blockIndex = 0; blockIndex < numBlocks; blockIndex++) {
            int blockLength = (blockIndex == numBlocks - 1) ? (pieceSize % blockSize) : blockSize;
            byte[] requestPayload = createRequestPayload(pieceIndex, blockOffset, blockLength);
            sendMessage(MessageType.REQUEST, requestPayload, socket);

            Message pieceMessage = recieveMessage(socket);

            if (pieceMessage.getType() == MessageType.PIECE) {
                byte[] blockData = pieceMessage.getPayload();
                System.arraycopy(blockData, 0, fullPiece, blockOffset, blockData.length);
                blockOffset += blockData.length;
            } else {
                return null;
            }

        }

        return fullPiece;
    }

    private void sendMessage(MessageType messageType, byte[] payload, Socket socket) throws IOException {
        Message message = new Message(messageType, payload);
        byte[] newPayload = new byte[payload.length + 2];
        newPayload[0] = (byte) newPayload.length;
        newPayload[1] = messageType.getValue();
        System.arraycopy(payload, 0, newPayload, 2, payload.length);
        socket.getOutputStream().write(newPayload);
    }

    private byte[] createRequestPayload(int pieceIndex, int pieceOffset, int pieceSize) {
        byte[] payload = new byte[3];
        payload[0] = (byte) pieceIndex;
        payload[1] = (byte) pieceOffset;
        payload[2] = (byte) pieceSize;

        return payload;
    }

    private void processPayload(byte[] response) {
        int pieceIndex = this.index;
        int pieceSize = ArrayUtils.toPrimitive(this.torrentMetaData.getPieces()).length / this.torrentMetaData.getPieceLength();
        int newIndex = (pieceIndex + 1) * pieceSize;
        System.arraycopy(response, 0, this.finalFile, newIndex, response.length);
    }

}
