package org.example;

public class TorrentFile {
    private String path;
    private long length;

    public TorrentFile(String path, long length){
        this.path = path;
        this.length = length;
    }
    public String getPath(){
        return path;
    }
    public long getLength(){
        return length;
    }
}
