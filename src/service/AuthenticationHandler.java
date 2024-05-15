package service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import utils.Utils;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.io.BufferedWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;

public class AuthenticationHandler {
    private String chaveSimetricaEncriptada;
    private static SecretKey key;
    private static byte[] msgEncriptada;
    private Utils utils;


    public AuthenticationHandler(BufferedWriter bufferedWriter) {
        try {
            
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            this.key = generator.generateKey();
            this.utils = new Utils(bufferedWriter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setSimetricKey(String publicKey64) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey64);
            System.out.println("publicKeyBytes: " + publicKeyBytes);
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            Cipher cipher =  Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedAesKey = cipher.doFinal(this.key.getEncoded());
            
            this.chaveSimetricaEncriptada = Base64.getEncoder().encodeToString(encryptedAesKey);
            System.out.println("Chave Simétrica Encriptada: " + this.chaveSimetricaEncriptada);
            byte[] chave = this.key.getEncoded();
            System.out.println("thiskey: " +chave);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendSimetricKeyToServer(){
        try {
            // String encryptedKeyMessageBase64 = Base64.getEncoder().encodeToString(this.chaveSimetricaEncriptada.getBytes());

            String encryptedKeyMessage = "CHAVE_SIMETRICA " + this.chaveSimetricaEncriptada;
            System.out.println("encryptedKeyMessage: " + encryptedKeyMessage);
            utils.sendMessageToServer(encryptedKeyMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void encryptedMessage(String Message){
        try {
            Cipher cif = Cipher.getInstance("AES");
            cif.init(Cipher.ENCRYPT_MODE, this.key);
    
            byte[] buffer = cif.doFinal(Message.getBytes());
            String messageToSend = Base64.getEncoder().encodeToString(buffer);
            utils.sendMessageToServer(messageToSend);
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }
    public String decryptMessageFromClient(String message) throws Exception {
        try {
            byte[] messageBytes = Base64.getDecoder().decode(message);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, this.key); // aesKey é a chave AES criada pelo servidor
            byte[] decryptedMessageBytes = cipher.doFinal(messageBytes);
            String decryptedMessage = new String(decryptedMessageBytes);
            return decryptedMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Failed to decrypt message";

        }
    } 
} 
