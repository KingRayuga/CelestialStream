package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PieceHash {
    public static List<byte[]> getPieceHash(byte[] pieceHash, int pieceLength) {
        List<byte[]> pieceHashList = new ArrayList<>();
        for (int i = 0; i < pieceHash.length; i += pieceLength) {
            pieceHashList.add(Arrays.copyOfRange(pieceHash, i, i + pieceLength));
        }
        return pieceHashList;
    }
}
