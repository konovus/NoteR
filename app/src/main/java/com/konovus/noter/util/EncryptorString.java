package com.konovus.noter.util;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

// to use this encryption class you have to import the spring-crypto library
public class EncryptorString {

    private final String password = "konovus";
    private final String salt = "e606bfd5cf9f198e";
    TextEncryptor encryptor;

    public EncryptorString(){
        encryptor = Encryptors.text(password, salt);
    }

    public String encrypt(String msg){
        return encryptor.encrypt(msg);
    }

    public String decrypt(String msg){
        TextEncryptor descriptor = Encryptors.text(password, salt);
        return descriptor.decrypt(msg);
    }
}
