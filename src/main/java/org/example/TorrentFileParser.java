package org.example;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TorrentFileParser {
    public static TorrentMetaData parseTorrentFile(String filePath){
        try(BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(filePath))){
            byte[] reader = bufferedInputStream.readAllBytes();
            Byte[] bytes = ArrayUtils.toObject(reader);

            Map<Object,Object> torrentData = (Map<Object, Object>) Bencoding_decoder.decode(bytes, new int[]{0});

            String announceURL = StringUtils.toEncodedString((byte[])ArrayUtils.toPrimitive(torrentData.get(ByteBuffer.wrap("announce".getBytes()))), Charset.defaultCharset());
            Map<Object,Object> infoDict = (Map<Object, Object>) torrentData.get(ByteBuffer.wrap("info".getBytes()));
            int pieceLength = (int) infoDict.get(ByteBuffer.wrap("piece length".getBytes()));
            Byte[] pieces = (Byte[]) infoDict.get(ByteBuffer.wrap("pieces".getBytes()));
            String name = StringUtils.toEncodedString((byte[])ArrayUtils.toPrimitive(infoDict.get(ByteBuffer.wrap("name".getBytes()))), Charset.defaultCharset());

            List<TorrentFile> files = parseFiles(infoDict);

            return new TorrentMetaData(announceURL,pieceLength,pieces,name,files);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<TorrentFile> parseFiles(Map<Object,Object> infoDict){
        List<TorrentFile> files = new ArrayList<>();
        if(infoDict.containsKey(ByteBuffer.wrap("files".getBytes()))){
            List<Map<Object,Object>> fileList = (List<Map<Object, Object>>) infoDict.get(ByteBuffer.wrap("files".getBytes()));
            for(Map<Object,Object> fileMap: fileList){
                String filePath = buildFilePath(fileMap);
                int fileLength = (int) fileMap.get(ByteBuffer.wrap("Length".getBytes()));
                files.add(new TorrentFile(filePath,fileLength));
            }
        }else{
            String filePath = StringUtils.toEncodedString((byte[])ArrayUtils.toPrimitive(infoDict.get(ByteBuffer.wrap("name".getBytes()))), Charset.defaultCharset());
            int fileLength = (int) infoDict.get(ByteBuffer.wrap("length".getBytes()));
            files.add(new TorrentFile(filePath,fileLength));
        }
        return files;
    }

    private static String buildFilePath(Map<Object,Object> fileMap){
        List<String> pathSegment = (List<String>) fileMap.get(ByteBuffer.wrap("path".getBytes()));
        return String.join(File.separator, pathSegment);
    }
}
