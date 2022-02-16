package com.softcrypt.deepkeysmusic.tools.encryptionTools;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.softcrypt.deepkeysmusic.common.Common;
import com.softcrypt.deepkeysmusic.model.DecEnc;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptA {

    private static final String PROVIDER = "BC";
    private static final int SALT_LENGTH = 20;
    private static final int IV_LENGTH = 16;
    private static final int PBE_ITERATION_COUNT = 100;

    private static final String RANDOM_ALGORITHM = "SHA1PRNG";
    private static final String HASH_ALGORITHM = "SHA-512";
    private static final String PBE_ALGORITHM = "PBEWithSHA256And256BitAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    public static final String SECRET_KEY_ALGORITHM = "AES";
    private static final String TAG = "EncryptionPassword";
    private static final Charset charSet = StandardCharsets.UTF_8;

    private static String decryptedPassword = "";

    public static String decData(DecEnc decEnc) {
        return decryptPassword(decEnc);
    }

    public static DecEnc encData(String password) {
        return encryptPassword(password);
    }

    private static String decryptPassword(DecEnc decEnc) {
        if(decEnc.token != null && !decEnc.token.isEmpty()) {
            try {
                byte[] encoded = hexStringToByteArray(decEnc.key);
                SecretKey aesKey = new SecretKeySpec(encoded, SECRET_KEY_ALGORITHM);
                decryptedPassword = decrypt(aesKey, decEnc.token);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decryptedPassword;
    }

    private static DecEnc encryptPassword(String password) {
        DecEnc data = new DecEnc();
        if(password != null && !password.isEmpty()) {
            SecretKey secretKey = null;
            try {
                secretKey = getSecretKey(password, generateSalt());

                byte[] encoded = secretKey.getEncoded();
                String input = byteArrayToHexString(encoded);
                String encryptedPassword = encrypt(secretKey, password);

                data.setToken(encryptedPassword);
                data.setKey(input);
                data.setVerified(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    private static String encrypt(SecretKey secret, String clearText) throws Exception {

        try {
            byte[] iv = generateIv();
            String ivHex = byteArrayToHexString(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            //Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
            Cipher encryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secret, ivSpec);
            byte[] encryptedText = encryptionCipher.doFinal(clearText.getBytes(charSet));
            String encryptedHex = byteArrayToHexString(encryptedText);

            return ivHex + encryptedHex;
        } catch (Exception e) {
            throw  new Exception(e);
        }
    }

    private static String decrypt(SecretKey secret, String encrypted) throws Exception {
        try {

            //Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM, PROVIDER);
            Cipher decryptionCipher = Cipher.getInstance(CIPHER_ALGORITHM);
            String ivHex = encrypted.substring(0, IV_LENGTH * 2);
            String encryptedHex = encrypted.substring(IV_LENGTH * 2);
            IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(ivHex));
            decryptionCipher.init(Cipher.DECRYPT_MODE, secret, ivSpec);
            byte[] decryptedText = decryptionCipher.doFinal(hexStringToByteArray(encryptedHex));
            return new String(decryptedText, charSet);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    private static String byteArrayToHexString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }

    private static String generateSalt() throws Exception {
        try {
            SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            return byteArrayToHexString(salt);
        } catch (Exception e) {
            throw  new Exception(e);
        }
    }

    private static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    private static SecretKey getSecretKey(String password, String salt) throws Exception {
        try {
            PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(),
                    hexStringToByteArray(salt), PBE_ITERATION_COUNT, 256);
            //SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM, PROVIDER);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(PBE_ALGORITHM);
            SecretKey tmp = factory.generateSecret(pbeKeySpec);
            return new SecretKeySpec(tmp.getEncoded(), SECRET_KEY_ALGORITHM);
        } catch (Exception e) {
            throw new Exception("Unable to get secret key", e);
        }
    }

    private static byte[] generateIv() throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom random = SecureRandom.getInstance(RANDOM_ALGORITHM);
        byte[] iv = new byte[IV_LENGTH];
        random.nextBytes(iv);
        return iv;
    }
}
