package com.flora30.divecore.tools;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
    public static String convertString(List<String> list){
        StringBuilder converted = new StringBuilder();
        // ''で囲む
        converted.append("'");
        for (String str : list){
            // 最初は「 _ 」をつけない
            if (!converted.toString().equals("'")) {
                converted.append("_");
            }
            converted.append(str);
        }
        converted.append("'");

        return converted.toString();
    }

    public static List<String> convertList(String str){
        str = str.replace("'","");
        return Arrays.asList(str.split("_"));
    }
}
