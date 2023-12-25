package org.example;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class FileDownloader {
    public final int THREAD_COUNT = 8;
    private final byte[] peerIp;
    private final List<PeerInfo> peers;
    private final TorrentMetaData torrentMetaData;
    private byte[] finalFile;

    FileDownloader(List<PeerInfo> peers, TorrentMetaData torrentMetaData, byte[] peerIp) {
        this.peers = peers;
        this.torrentMetaData = torrentMetaData;
        this.peerIp = peerIp;
        finalFile = new byte[ArrayUtils.toPrimitive(this.torrentMetaData.getPieces()).length];
    }

    public void downloadFile() {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        int peerSize = peers.size();
        int pieceSize = ArrayUtils.toPrimitive(this.torrentMetaData.getPieces()).length / this.torrentMetaData.getPieceLength();
        List<Future<Boolean>> futureList = new ArrayList<>();
        for (int index = 0; index < pieceSize; index++) {
            int peerIndex = index % peerSize;
            futureList.add(executorService.submit(new ThreadRunner(peers.get(peerIndex).getIp(), peers.get(peerIndex).getPort(), peerIp, torrentMetaData, index, finalFile)));
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (Future<Boolean> bool : futureList) {
            try {
                Boolean value = bool.get();
                if (!value) {
                    System.out.println("Unable to download the file");
                    return;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Unexpected Error");
                throw new RuntimeException(e);
            }
        }
        executorService.shutdown();
        System.out.println("File Downloaded Successfully");
    }

    public byte[] getFinalFile() {
        return finalFile;
    }

}

class ThreadRunner implements Callable<Boolean> {

    private final String peerIp;
    private final int peerPort;
    private final byte[] peerId;

    private final TorrentMetaData torrentMetaData;
    private final int index;
    private byte[] finalFile;

    ThreadRunner(String peerIp, int peerPort, byte[] peerId, TorrentMetaData torrentMetaData, int index, byte[] finalFile) {
        this.peerIp = peerIp;
        this.peerPort = peerPort;
        this.peerId = peerId;
        this.torrentMetaData = torrentMetaData;
        this.index = index;
        this.finalFile = finalFile;
    }

    @Override
    public Boolean call() {
        int triesLeft = 5;
        PieceDownloader pieceDownloader = new PieceDownloader(torrentMetaData, index, peerIp, peerPort, peerId, finalFile);
        while (triesLeft > 0 && !pieceDownloader.isDownloadSuccessful()) {
            triesLeft--;
            pieceDownloader.downloadPiece();
        }
        return pieceDownloader.isDownloadSuccessful();
    }
}
