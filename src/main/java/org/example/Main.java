package org.example;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.IntStream;

public class Main {

    private static byte[] peerId;
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("unequal argument");
        } else {
            if (args[0].equals("decode")) {
                Byte[] byteArray = args[1]
                        .chars()
                        .mapToObj(value -> (byte) value)
                        .toArray(Byte[]::new);
                BencodingDecoder bencodingDecoder = new BencodingDecoder();
                Object decodedValue = bencodingDecoder.decode(byteArray, new int[]{0});
                System.out.println("object type is " + decodedValue.getClass() + "object value is " + decodedValue);
            } else if (args[0].equals("parse")) {
                TorrentMetaData[] torrentMetaData = new TorrentMetaData[args.length - 1];
                IntStream.rangeClosed(1, args.length - 1).parallel().forEach(i -> {
                    torrentMetaData[i - 1] = TorrentFileParser.parseTorrentFile(args[i]);
                    if (null != torrentMetaData[i - 1]) {
                        System.out.println(i + " - torrent file is " + torrentMetaData[i - 1]);
                        System.out.println("infoHash is " + HexStringConvert(torrentMetaData[i - 1].getInfoHash()));
                    } else {
                        System.out.println("Unable to parse file - " + i);
                    }
                });
            } else if (args[0].equals("download")) {
                TorrentMetaData[] torrentMetaData = new TorrentMetaData[args.length - 1];
                IntStream.rangeClosed(1, args.length - 1).parallel().forEach(i -> {
                    torrentMetaData[i - 1] = TorrentFileParser.parseTorrentFile(args[i]);
                    if (null != torrentMetaData[i - 1]) {
                        PeerInfoParser peerInfoParser = TrackerRequest.sendRequest(torrentMetaData[i-1],HexStringConvert(peerId));
                        List<PeerInfo> peers = peerInfoParser.getPeers();
                        FileDownloader fileDownloader = new FileDownloader(peers,torrentMetaData[i-1],peerId);
                    } else {
                        System.out.println("Unable to download file " + args[i]);
                    }
                });
            }
        }
    }

    private static byte[] generateId(){
        int arrayLength = 20;
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomByte = new byte[arrayLength];
        secureRandom.nextBytes(randomByte);
        return randomByte;
    }
    private static String HexStringConvert(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}