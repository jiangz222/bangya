package com.bangya.client.auth;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.provider.SyncStateContract.Constants;
import android.util.Base64;


public class DES_NeverUsed {
	public String DesKey = "109357446966059208613235291923319669999136307558522113370064696977565420171125451375935306533320484629236294978872399846701621183585532962817355652572172983286670846426669751888940251520662053909954303306221477171512630686605178756280526683493686021559523991854551835612314162167445042067324349321548274287971";
	public String encrypt(byte[] datasource) {              
        try{  
        SecureRandom random = new SecureRandom();  
        DESKeySpec desKey = new DESKeySpec(DesKey.getBytes());  
        //创建一个密匙工厂，然后用它把DESKeySpec转换成  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        //Cipher对象实际完成加密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        //用密匙初始化Cipher对象  
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);  
        //现在，获取数据并加密  
        //正式执行加密操作  
        return Base64.encodeToString(cipher.doFinal(datasource), Base64.DEFAULT);
        
        }catch(Throwable e){  
                e.printStackTrace();  
        }  
        return null;  
}  
	public byte[] decrypt(byte[] src) throws Exception {  
        // DES算法要求有一个可信任的随机数源  
        SecureRandom random = new SecureRandom();  
        // 创建一个DESKeySpec对象  
        DESKeySpec desKey = new DESKeySpec(DesKey.getBytes());  
        // 创建一个密匙工厂  
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");  
        // 将DESKeySpec对象转换成SecretKey对象  
        SecretKey securekey = keyFactory.generateSecret(desKey);  
        // Cipher对象实际完成解密操作  
        Cipher cipher = Cipher.getInstance("DES");  
        // 用密匙初始化Cipher对象  
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);  
        // 真正开始解密操作  
        return cipher.doFinal(src);  
	}  
}
