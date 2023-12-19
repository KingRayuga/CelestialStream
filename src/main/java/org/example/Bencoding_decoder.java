package org.example;

import java.util.*;

public class Bencoding_decoder {
    public static Object decode(String encodedString, int[] index){
        if(null == encodedString || encodedString.isEmpty()){
            return null;
        }
        if(encodedString.charAt(index[0])=='i'){
            int endIndex = encodedString.indexOf('e',index[0]);
            int startIndex = index[0]+1;
            index[0] = endIndex+1;
            return Integer.parseInt(encodedString.substring(startIndex,endIndex));
        }
        else if(encodedString.charAt(index[0])=='l'){
            List<Object> list = new ArrayList<>();
            index[0]+=1;
            while(encodedString.charAt(index[0])!='e'){
                list.add(decode(encodedString,index));
            }
            return list;
        }
        else if(encodedString.charAt(index[0])=='d'){
            Map<Object,Object> dict = new TreeMap<>();
            index[0] += 1;
            while(encodedString.charAt(index[0])!='e'){
                Object key = decode(encodedString,index);
                Object value = decode(encodedString,index);
                dict.put(key,value);
            }
            return dict;
        }
        else if(Character.isDigit(encodedString.charAt(index[0]))){
            int colonIndex = encodedString.indexOf(':',index[0]);
            String digit = encodedString.substring(index[0],colonIndex);
            int digitValue = Integer.parseInt(digit);
            String result = encodedString.substring(colonIndex+1,colonIndex+digitValue+1);
            index[0] = colonIndex+digitValue+1;
            return result;
        }
        return null;
    }
}
