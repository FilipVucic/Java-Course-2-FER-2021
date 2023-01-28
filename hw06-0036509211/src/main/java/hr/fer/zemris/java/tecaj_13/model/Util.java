package hr.fer.zemris.java.tecaj_13.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Util {

    /**
     * Hex chars.
     */
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    /**
     * Convert byte array to hex string.
     *
     * @param bytearray Byte array
     * @return Hex string
     */
    public static String bytetohex(byte[] bytearray) {
        char[] hexChars = new char[bytearray.length * 2];
        for (int j = 0; j < bytearray.length; j++) {
            int v = bytearray[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        return new String(hexChars);
    }

    /**
     * Generates SHA-1 hash from given password.
     *
     * @param pass Password
     * @return Hashed password
     * @throws NoSuchAlgorithmException If can't get {@link MessageDigest} instance
     */
    public static byte[] calcHash(String pass) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        messageDigest.update(pass.getBytes());
        return messageDigest.digest();
    }
}
