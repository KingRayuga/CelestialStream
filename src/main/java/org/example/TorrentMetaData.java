package org.example;

import java.util.List;

public class TorrentMetaData {
    private String announceURL;
    private int pieceLength;
    private Byte[] pieces;
    private String name;
    private List<TorrentFile> files;
    private byte[] infoHash;
    private List<byte[]> pieceHash;

    public TorrentMetaData(String announceURL, int pieceLength, Byte[] pieces, String name, List<TorrentFile> files) {
        this.announceURL = announceURL;
        this.pieceLength = pieceLength;
        this.pieces = pieces;
        this.name = name;
        this.files = files;
    }

    public String getURL() {
        return announceURL;
    }

    public int getPieceLength() {
        return pieceLength;
    }

    public Byte[] getPieces() {

        return pieces;
    }

    public List<TorrentFile> getFiles() {

        return files;
    }

    public String getName() {

        return name;
    }

    public byte[] getInfoHash() {
        return infoHash;
    }

    public void setInfoHash(byte[] infoHash) {
        this.infoHash = infoHash;
    }

    public List<byte[]> getPieceHash() {
        return pieceHash;
    }

    public void setPieceHash(List<byte[]> pieceHash) {
        this.pieceHash = pieceHash;
    }
}
