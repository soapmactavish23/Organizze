package com.example.organizze.helper;

import android.util.Base64;

public class Base64Custom {

    public static String condificarBase64(String texto){
        return Base64.encodeToString(texto.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)" , "");
    }

    public static String decodificarBase64(String txtCodificado){
        return new String( Base64.decode(txtCodificado, Base64.DEFAULT));
    }

}
