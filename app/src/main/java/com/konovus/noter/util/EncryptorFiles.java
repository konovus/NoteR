package com.konovus.noter.util;

import android.app.Activity;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptorFiles {

    private final static int READ_WRITE_BLOCK_BUFFER = 1024;
    public static final String keyStr = "QJJzExZHO9tsHlW6";
    public static final String specStr = "ajx0m6wK1wSuBK2j";
    private final static String algo_secretKey = "AES";
    private final static String algorithm = "AES/CBC/PKCS5Padding";

        @SuppressWarnings("resource")
        public static String encryptFile(Activity activity, String imagePath){
//                here we create the folder which is going to contain all our encrypted images
            File folder = new File(activity.getExternalFilesDir("/").getAbsolutePath() + "/images/encrypted/");
            if (!folder.exists())
                folder.mkdirs();
//                here we create the actual encrypted file for our image
            File encryptedFile = new File(folder, imagePath.substring(imagePath.lastIndexOf("/") + 1));
            if (encryptedFile.exists())
                encryptedFile.delete();

            File file = new File(imagePath);
            OutputStream out = null;
            if(file.exists())
            try {

//                here we're getting our image in the inputStream and then our encryptedFile in the outputStream
                InputStream in = new FileInputStream(file);
                out = new FileOutputStream(encryptedFile);

                IvParameterSpec iv = new IvParameterSpec(specStr.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes(StandardCharsets.UTF_8), algo_secretKey);
                Cipher c = Cipher.getInstance(algorithm);
                c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
                out = new CipherOutputStream(out, c);
                int count = 0;
                byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
                while((count = in.read(buffer)) > 0)
                    out.write(buffer, 0, count);

                return Uri.fromFile(encryptedFile).toString();
            } catch (NoSuchAlgorithmException | InvalidKeyException
                    | InvalidAlgorithmParameterException | NoSuchPaddingException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @SuppressWarnings("resource")
        public static String decryptFile(Activity activity, String imagePath){
            File decryptedFolder = new File(activity.getExternalFilesDir("/").getAbsolutePath()+"/images");
            if(!decryptedFolder.exists())
                decryptedFolder.mkdirs();
            File decryptedFile = new File(decryptedFolder, imagePath.substring(imagePath.lastIndexOf("/") + 1));
            if (decryptedFile.exists ())
                decryptedFile.delete ();
            OutputStream out = null;
            try {
                InputStream in = activity.getContentResolver().openInputStream(Uri.parse(imagePath));
                out = new FileOutputStream(decryptedFile);

                IvParameterSpec iv = new IvParameterSpec(specStr.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec keySpec = new SecretKeySpec(keyStr.getBytes(StandardCharsets.UTF_8), algo_secretKey);
                Cipher c = Cipher.getInstance(algorithm);
                c.init(Cipher.DECRYPT_MODE, keySpec, iv);
                out = new CipherOutputStream(out, c);
                int count = 0;
                byte[] buffer = new byte[READ_WRITE_BLOCK_BUFFER];
                while((count = in.read(buffer)) > 0)
                    out.write(buffer, 0, count);

                return Uri.fromFile(decryptedFile).toString();

            } catch (IOException | NoSuchPaddingException
                    | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
}
