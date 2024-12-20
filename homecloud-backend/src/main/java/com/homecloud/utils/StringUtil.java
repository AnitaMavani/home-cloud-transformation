package com.homecloud.utils;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

public class StringUtil {

    public static String encodeByMD5(String originString) {
        return StringUtil.isEmpty(originString) ? null : DigestUtils.md5Hex(originString);
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.trim().isEmpty() || "null".equalsIgnoreCase(str) || "\u0000".equals(str)) {
            return true;
        }
        return false;
    }

    public static String getFileSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index == -1 ? "" : fileName.substring(index);
    }

    public static String getFileNameNoSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index == -1 ? fileName : fileName.substring(0, index);
    }


    public static String rename(String fileName, int count) {
        String fileNameReal = getFileNameNoSuffix(fileName);
        String suffix = getFileSuffix(fileName);
        return fileNameReal + "(" + count + ")"+ suffix;
    }

    public static final String getRandomString(Integer count) {
        return RandomStringUtils.random(count, true, true);
    }

    public static final String getRandomNumber(Integer count) {
        return RandomStringUtils.random(count, false, true);
    }


    public static boolean pathCorrect(String path) {
        if (StringUtil.isEmpty(path)) {
            return true;
        }
        if (path.contains("../") || path.contains("..\\")) {
            return false;
        }
        return true;
    }
}
