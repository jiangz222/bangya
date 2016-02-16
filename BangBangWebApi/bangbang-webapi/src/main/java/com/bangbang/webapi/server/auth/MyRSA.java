package com.bangbang.webapi.server.auth;

import org.springframework.security.crypto.codec.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.*;
/**
 * Created by wisp on 3/18/14.
 */
public class MyRSA {
    private Key privateKey;
    private Key publicKey;
    public MyRSA()
    {
        try {
            ObjectInputStream privateKeyIS = new ObjectInputStream(new FileInputStream("/mnt/privateKey"));
            privateKey = (Key)privateKeyIS.readObject();
            privateKeyIS.close();

            //File publicKeyFile = new File("publicKey");
            ObjectInputStream publicKeyIS = new ObjectInputStream(new FileInputStream("/mnt/publicKey"));
            publicKey = (Key)publicKeyIS.readObject();
            publicKeyIS.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    public String encrypt(byte[] textBytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
    {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Initialize the cipher for encryption. Use the public key.
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //Perform the encryption using doFinal
        byte[] encByte = rsaCipher.doFinal(textBytes);

        // converts to base64 for easier display.
        byte[] base64Cipher = Base64.encode(encByte);
        return new String(base64Cipher);
    }//end encrypt

    public String decrypt(byte[] cipherBytes) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        //Initialize the cipher for encryption. Use the public key.
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
        //Perform the encryption using doFinal
        byte[] decByte = rsaCipher.doFinal(cipherBytes);
        return new String(decByte);

    }//end decrypt
    private void generate()
    {
        KeyPairGenerator kpg = null;
        KeyPair keyPair = null;
        try
        {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024);
            keyPair = kpg.genKeyPair();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        File privateKeyFile = new File("privateKey");
        File publicKeyFile = new File("publicKey");

        try {
            privateKeyFile.createNewFile();
            ObjectOutputStream privateKeyOS = new ObjectOutputStream(new FileOutputStream(privateKeyFile));
            privateKeyOS.writeObject(keyPair.getPrivate());
            privateKeyOS.close();

            publicKeyFile.createNewFile();
            ObjectOutputStream publicKeyOS = new ObjectOutputStream(new FileOutputStream(publicKeyFile));
            publicKeyOS.writeObject(keyPair.getPublic());
            publicKeyOS.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
