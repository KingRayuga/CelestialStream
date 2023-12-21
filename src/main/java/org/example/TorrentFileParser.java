package org.example;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TorrentFileParser {
    public static TorrentMetaData parseTorrentFile(String filePath) {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] reader = bufferedInputStream.readAllBytes();
            Byte[] bytes = ArrayUtils.toObject(reader);

            BencodingDecoder bencodingDecoder = new BencodingDecoder();
            Map<Object, Object> torrentData = (Map<Object, Object>) bencodingDecoder.decode(bytes, new int[]{0});

            String announceURL = getEncodedString(torrentData, "announce");
            Map<Object, Object> infoDict = getInfoDictionary(torrentData);
            int pieceLength = getPieceLength(infoDict);
            Byte[] pieces = getPieces(infoDict);
            String name = getEncodedString(infoDict, "name");

            List<TorrentFile> files = parseFiles(infoDict);

            TorrentMetaData torrentMetaData = new TorrentMetaData(announceURL, pieceLength, pieces, name, files);

            torrentMetaData.setInfoHash(InfoHash.getInfoHash(bencodingDecoder.getInfo()));
            torrentMetaData.setPieceHash(PieceHash.getPieceHash(bencodingDecoder.getPiece(), pieceLength));

            return torrentMetaData;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getEncodedString(Map<Object, Object> data, String key) {
        byte[] bytes = (byte[]) ArrayUtils.toPrimitive(data.get(ByteBuffer.wrap(key.getBytes())));
        return StringUtils.toEncodedString(bytes, Charset.defaultCharset());
    }

    private static Map<Object, Object> getInfoDictionary(Map<Object, Object> torrentData) {
        return (Map<Object, Object>) torrentData.get(ByteBuffer.wrap("info".getBytes()));
    }

    private static int getPieceLength(Map<Object, Object> infoDict) {
        return (int) infoDict.get(ByteBuffer.wrap("piece length".getBytes()));
    }

    private static Byte[] getPieces(Map<Object, Object> infoDict) {
        return (Byte[]) infoDict.get(ByteBuffer.wrap("pieces".getBytes()));
    }

    private static List<TorrentFile> parseFiles(Map<Object, Object> infoDict) {
        List<TorrentFile> files = new ArrayList<>();
        if (infoDict.containsKey(ByteBuffer.wrap("files".getBytes()))) {
            List<Map<Object, Object>> fileList = (List<Map<Object, Object>>) infoDict.get(ByteBuffer.wrap("files".getBytes()));
            for (Map<Object, Object> fileMap : fileList) {
                String filePath = buildFilePath(fileMap);
                int fileLength = (int) fileMap.get(ByteBuffer.wrap("Length".getBytes()));
                files.add(new TorrentFile(filePath, fileLength));
            }
        } else {
            String filePath = getEncodedString(infoDict, "name");
            int fileLength = (int) infoDict.get(ByteBuffer.wrap("length".getBytes()));
            files.add(new TorrentFile(filePath, fileLength));
        }
        return files;
    }

    private static String buildFilePath(Map<Object, Object> fileMap) {
        List<String> pathSegment = (List<String>) fileMap.get(ByteBuffer.wrap("path".getBytes()));
        return String.join(File.separator, pathSegment);
    }
}
