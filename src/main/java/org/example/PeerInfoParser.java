package org.example;

import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PeerInfoParser {
    private int interval;
    private List<PeerInfo> peers;
    private final String response;

    public PeerInfoParser(String response) {
        this.response = response;
    }

    public void parseTrackerInfo(){
        JSONObject jsonObject = new JSONObject(response);
        if(jsonObject.has("interval")){
            interval = jsonObject.getInt("interval");
        }
        if(jsonObject.has("peers")){
            String peersBlob = jsonObject.getString("peers");
            peers = parsePeersBlob(peersBlob);
        }
    }

    private List<PeerInfo> parsePeersBlob(String peersBlob) {
        List<PeerInfo> peerList = new ArrayList<>();
        for(int i=0;i<peersBlob.length();i+=6){
            String ipByte = peersBlob.substring(i,i+4);
            String portByte = peersBlob.substring(i,i+6);

            int ip = Integer.parseInt(ipByte,16);
            int port = Integer.parseInt(portByte,16);
            String ipAddress = String.format("%d.%d.%d.%d",(ip>>24)&0xFF,(ip>>16)&0xFF,(ip>>8)&0xFF,ip&0xFF);
            peerList.add(new PeerInfo(ipAddress,port));
        }
        return peerList;
    }

    public List<PeerInfo> getPeers() {
        return peers;
    }

    public int getInterval() {
        return interval;
    }

    public String getResponse() {
        return response;
    }
}
