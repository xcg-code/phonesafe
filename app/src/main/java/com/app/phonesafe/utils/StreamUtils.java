package com.app.phonesafe.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 14501_000 on 2016/8/1.
 */
public class StreamUtils {
    public static String streamToString(InputStream in) throws IOException {
        BufferedReader br=new BufferedReader(new InputStreamReader(in));
        String line;
        StringBuilder sb=new StringBuilder();
        while((line=br.readLine())!=null){
            sb.append(line);
        }
        String respones=sb.toString();
        return respones;
    }
}
