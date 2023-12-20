package org.example;

public class Main {
    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("unequal argument");
        }
        else{
            if(args[0].equals("decode")){
                Byte[] byteArray = args[1]
                        .chars()
                        .mapToObj(value -> (byte) value)
                        .toArray(Byte[]::new);
                Object decodedValue = Bencoding_decoder.decode(byteArray,new int[]{0});
                System.out.println("object type is " + decodedValue.getClass() + "object value is " + decodedValue);
            }
            else if(args[0].equals("parse")){
                TorrentMetaData torrentMetaData = TorrentFileParser.parseTorrentFile(args[1]);
                if(null!=torrentMetaData){
                    System.out.println(torrentMetaData);
                }
                else{
                    System.out.println("Unable to parse Torrent File");
                }
            }
        }
    }
}