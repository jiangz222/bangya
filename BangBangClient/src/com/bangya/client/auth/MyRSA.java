package com.bangya.client.auth;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.joeapp.bangya.R;

import java.io.*;
import java.security.*;


import android.content.Context;
import android.util.Base64;

/**
 * Created by wisp on 3/18/14.
 */
public class MyRSA {
    private Key privateKey;
    private Key publicKey;
    public MyRSA(Context ct)
    {
        try {
          //  ObjectInputStream privateKeyIS = new ObjectInputStream(new FileInputStream("/mnt/privateKey"));
           // privateKey = (Key)privateKeyIS.readObject();
           // privateKeyIS.close();

            //File publicKeyFile = new File("publicKey");
        	ObjectInputStream publicKeyIS = new ObjectInputStream(ct.getResources().getAssets().open("publicKey"));
        	//InputStreamReader publicKeyIS  = new InputStreamReader(ct.getResources().getAssets().open("publicKey"));
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
        //byte[] base64Cipher = Base64.encode(encByte);
        // return new String(base64Cipher);
        return Base64.encodeToString(encByte, Base64.DEFAULT);

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
