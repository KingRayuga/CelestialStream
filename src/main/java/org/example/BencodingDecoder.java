package org.example;

import org.apache.commons.lang3.ArrayUtils;

import java.nio.ByteBuffer;
import java.util.*;

public class BencodingDecoder {
    public static Object decode(Byte[] encodedBytes, int[] index) {
        if (encodedBytes == null || encodedBytes.length == 0) {
            return null;
        }

        if (encodedBytes[index[0]] == 'i') {
            int endIndex = indexOf(encodedBytes, (byte) 'e', index[0]);
            int startIndex = index[0] + 1;
            index[0] = endIndex + 1;
            Byte[] digitBytes = Arrays.copyOfRange(encodedBytes, startIndex, endIndex);
            return bytesToInt(digitBytes);
        } else if (encodedBytes[index[0]] == 'l') {
            List<Object> list = new ArrayList<>();
            index[0] += 1;
            while (encodedBytes[index[0]] != 'e') {
                list.add(decode(encodedBytes, index));
            }
            return list;
        } else if (encodedBytes[index[0]] == 'd') {
            Map<Object, Object> dict = new HashMap<>();
            index[0] += 1;
            while (encodedBytes[index[0]] != 'e') {
                Object key = decode(encodedBytes, index);
                Object value = decode(encodedBytes, index);
                if (key instanceof Byte[]) {
                    dict.put(ByteBuffer.wrap((byte[]) ArrayUtils.toPrimitive(key)), value);
                } else {
                    dict.put(key, value);
                }
            }
            return dict;
        } else if (Character.isDigit((char) encodedBytes[index[0]].byteValue())) {
            int colonIndex = indexOf(encodedBytes, (byte) ':', index[0]);
            int digitValue = bytesToInt(Arrays.copyOfRange(encodedBytes, index[0], colonIndex));
            Byte[] result = Arrays.copyOfRange(encodedBytes, colonIndex + 1, colonIndex + digitValue + 1);
            index[0] = colonIndex + digitValue + 1;
            return result;
        }
        return null;
    }

    private static int indexOf(Byte[] array, byte target, int fromIndex) {
        for (int i = fromIndex; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }

    private static int bytesToInt(Byte[] bytes) {
        int result = 0;
        for (Byte b : bytes) {
            result = result * 10 + (b - '0');
        }
        return result;
    }
}
