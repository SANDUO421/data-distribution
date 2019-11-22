package com.data.distribution.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 三多
 * @Time 2019/11/20
 */
public class Utils {
    public Utils() {
    }

    public static int BytestoInt16(byte[] src, int offset) {
        int value = (src[offset] & 255) << 24 | (src[offset + 1] & 255) << 16 | (src[offset + 2] & 255) << 8 | src[offset + 3] & 255;
        return value;
    }

    public static String filterUnNumber(String str) {
        String regEx = "[^,.\\d]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
