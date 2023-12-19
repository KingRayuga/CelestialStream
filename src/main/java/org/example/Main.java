package org.example;

public class Main {
    public static void main(String[] args) {
        if(args.length!=2){
            System.out.println("unequal argument");
        }
        else{
            if(args[0].equals("decode")){
                Object decodedValue = Bencoding_decoder.decode(args[1],new int[]{0});
                System.out.println("object type is " + decodedValue.getClass() + "object value is " + decodedValue);
            }
        }
    }
}