package com.example.docqa.util;
public class CommonUtils {
    public static String trimToLength(String input, int length) {
        return input.length() <= length ? input : input.substring(0, length);
    }
}