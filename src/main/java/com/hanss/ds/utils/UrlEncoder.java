package com.hanss.ds.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UrlEncoder {

    /**
     * Кодирует только непечатаемые символы в строке.
     *
     * @param input Входная строка
     * @return Строка с закодированными непечатаемыми символами
     */
    public static String encodeNonPrintableCharacters(String input) {
        StringBuilder result = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (isNonPrintableCharacter(ch)) {
                // Кодируем непечатаемый символ
                result.append(encodeCharacter(ch));
            } else {
                // Оставляем печатаемый символ без изменений
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * Проверяет, является ли символ непечатаемым.
     *
     * @param ch Символ для проверки
     * @return true, если символ непечатаемый
     */
    private static boolean isNonPrintableCharacter(char ch) {
        // Управляющие символы и пробелы считаются непечатаемыми
        return Character.isISOControl(ch) || Character.isWhitespace(ch);
    }

    /**
     * Кодирует один символ в URL-кодированный формат.
     *
     * @param ch Символ для кодирования
     * @return Закодированный символ
     */
    private static String encodeCharacter(char ch) {
        return URLEncoder.encode(String.valueOf(ch), StandardCharsets.UTF_8).replace("+", "%20");
    }

    public static void main(String[] args) {
        String input = "Hello,\nWorld!\tПривет, мир!";
        String encoded = encodeNonPrintableCharacters(input);
        System.out.println("Исходная строка: " + input);
        System.out.println("Закодированная строка: " + encoded);
    }

    public static String hashContent(String content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
