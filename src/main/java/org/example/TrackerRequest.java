package org.example;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TrackerRequest {
    public static PeerInfoParser sendRequest(TorrentMetaData torrentMetaData, String clientPeerId) {
        try {
            String trackURL = torrentMetaData.getURL();
            String infoHash = HexStringConvert(torrentMetaData.getInfoHash());
            String peerId = clientPeerId;
            int port = 6969;
            long uploaded = 0;
            long downloaded = 0;
            long left = ArrayUtils.toPrimitive(torrentMetaData.getPieces()).length;

            String url = buildURL(trackURL, infoHash, peerId, port, uploaded, downloaded, left);
            System.out.println("tracker url is " + trackURL);
            String response = sendGetURL(url);
            return new PeerInfoParser(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static String sendGetURL(String queryUrl) throws IOException {
        URL url = new URL(queryUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();

            return response.toString();
        } else {
            System.out.println("GET request failed");
            return null;
        }

    }

    private static String buildURL(String trackURL, String infoHash, String peerId, int port, long uploaded, long downloaded, long left) {
        String template = "${trackerUrl}?info_hash=${infoHash}&peer_id=${peerId}&port=${port}&uploaded=${uploaded}&downloaded=${downloaded}&left=${left}";
        Map<String, String> valueMap = new HashMap<>() {{
            put("trackURL", trackURL);
            put("infoHash", infoHash);
            put("peerId", peerId);
            put("port", String.valueOf(port));
            put("uploaded", String.valueOf(uploaded));
            put("downloaded", String.valueOf(downloaded));
            put("left", String.valueOf(left));
        }};

        StringSubstitutor substitutor = new StringSubstitutor(valueMap);
        return substitutor.replace(template);

    }

    private static String HexStringConvert(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

}
