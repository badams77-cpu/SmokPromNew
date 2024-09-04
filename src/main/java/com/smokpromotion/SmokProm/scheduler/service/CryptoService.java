package com.smokpromotion.SmokProm.scheduler.service;

import org.apache.xerces.impl.dv.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class CryptoService {

    private final String key;
    private final String iv;

    @Autowired
    public CryptoService( @Value("${MPC_SECRET_KEY:sixteen_chars123}") String key , @Value("${MPC_SECRET_IV:sixteen_chars987}") String iv){
        this.key = key;
        this.iv = iv;
    }

    public String encrypt(String in){
        try {
           IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encValue = cipher.doFinal(in.getBytes("UTF-8"));
            return Base64.encode(encValue);
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("UTF-8 Charset not suppoprted, error in java installation",e);
        } catch (NoSuchAlgorithmException| NoSuchPaddingException e){
            throw new RuntimeException("AES/CBC Encryption not found, check java installation",e);
        } catch (InvalidKeyException e){
            throw new RuntimeException("Secret key was invalid, needs 16/24 or 32 characters,e");
        } catch (InvalidAlgorithmParameterException e){
            throw new RuntimeException("Crypto Algorithm has wrong parameters",e);
        } catch (IllegalBlockSizeException e){
            throw new RuntimeException("IlegalBlockSize in encrypt",e);
        } catch (BadPaddingException e){
            throw new RuntimeException("BadPadding in encrypt",e);
        }
    }

    public String decrypt(String in){
        try {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encValue = cipher.doFinal(Base64.decode(in));
            return new String(encValue);
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("UTF-8 Charset not suppoprted, error in java installation",e);
        } catch (NoSuchAlgorithmException| NoSuchPaddingException e){
            throw new RuntimeException("AES/CBC Encryption not found, check java installation",e);
        } catch (InvalidKeyException e){
            throw new RuntimeException("Secret key was invalid, needs 16/24 or 32 characters,e");
        } catch (InvalidAlgorithmParameterException e){
            throw new RuntimeException("Crypto Algorithm has wrong parameters",e);
        } catch (IllegalBlockSizeException e){
            throw new RuntimeException("IlegalBlockSize in encrypt",e);
        } catch (BadPaddingException e){
            throw new RuntimeException("BadPadding in encrypt",e);
        }
    }


}
