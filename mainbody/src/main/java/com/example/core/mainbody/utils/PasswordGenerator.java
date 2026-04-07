package com.example.core.mainbody.utils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordGenerator {

    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARACTERS = "#!@?$%*&+-,.";
    private static final String ALL_CHARACTERS = UPPERCASE_LETTERS + LOWERCASE_LETTERS + DIGITS + SPECIAL_CHARACTERS;

    public static String getRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4");
        }

        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one character from each category
        password.append(UPPERCASE_LETTERS.charAt(random.nextInt(UPPERCASE_LETTERS.length())));
        password.append(LOWERCASE_LETTERS.charAt(random.nextInt(LOWERCASE_LETTERS.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARACTERS.charAt(random.nextInt(SPECIAL_CHARACTERS.length())));

        // Fill the rest of the password with random characters from all categories
        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARACTERS.charAt(random.nextInt(ALL_CHARACTERS.length())));
        }

        // Shuffle the characters to ensure randomness
        char[] passwordArray = password.toString().toCharArray();
        for (int i = length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char a = passwordArray[index];
            passwordArray[index] = passwordArray[i];
            passwordArray[i] = a;
        }

        return new String(passwordArray);
    }

    public static void main(String[] args) {
        int passwordLength = 10; // Specify your desired password length here
        String password = getRandomPassword(passwordLength);
        System.out.println("Generated Password: " + password);
    }
}
