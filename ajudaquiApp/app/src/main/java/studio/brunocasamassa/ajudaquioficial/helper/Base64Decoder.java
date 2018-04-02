package studio.brunocasamassa.ajudaquioficial.helper;

import android.util.Base64;

/**
 * Created by bruno on 10/03/2017.
 */

public class Base64Decoder {

    public static String encoderBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)","");

    }

    public static String decoderBase64(String textDecoded){
        return new String (Base64.decode(textDecoded, Base64.DEFAULT));

    }

}
