package com.robi.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CipherUtil {
    // [Class public constants]
    // CipherType
    public static final int AES_CBC_PKCS5 = 0;
    // ...
    
    // [Class private constants]
    private static final Logger logger = LoggerFactory.getLogger(CipherUtil.class);
    private static final byte[] IV_ARY = "ABC0abc1DEF2def3GHI4ghi5JKL6jkl7MNO8mno9PQR+pqr-STU_stu=VWX~vwx/YZ.yz".getBytes();
    // CipherType : AES_CBC_PKCS5
    private static final String TF_AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    private static final IvParameterSpec IV_AES_CBC_PKCS5 = new IvParameterSpec(Arrays.copyOfRange(IV_ARY, 0, 16));
    // CipherType : algo_mode_padd
    // ...
    
    // [Methods]
    /**
     * <p>입력된 바이트 배열을 암호화하여 반환합니다.</p>
     * @param cipherType : 암호화 알고리즘, 모드 및 패딩.
     * <pre>
     * - AES_CBC_PKCS5 : 알고리즘:AES-128, 암호화 모드:CBC, 패딩:PKCS5</pre>
     * @param plainBytes : 평문 바이트 배열.
     * @param secretKeySpec : SecretKeySpec(byte[] key, String algorithm) 생성자로 생성된 암호화 키.
     * @return 암호화된 바이트 배열.
     */
    public static byte[] encrypt(int cipherType, byte[] plainBytes, SecretKeySpec secretKeySpec) {
        // 파라미터 검사
        if (plainBytes == null || plainBytes.length == 0) {
            logger.error("'plainByte' is null or zero length! (plainByte:" + plainBytes + ")");
            return null;
        }
        
        if (secretKeySpec == null) {
            logger.error("'secretKeySpec' is null!");
            return null;
        }
        
        // 암호화 수행
        byte[] cipherBytes = null;
        
        if (cipherType == AES_CBC_PKCS5) {
            cipherBytes = cipherAES(Cipher.ENCRYPT_MODE, plainBytes, secretKeySpec);
        }
        else if (true) {
            // Add new CipherType here...
        }
        
        // 암호화 결과 검사 및 반환
        if (cipherBytes == null) {
            logger.error("The encode reseult 'cipherBytes' is null. encoding failed!");
            return null;
        }

        return cipherBytes;
    }
    
    /**
     * <p>입력된 바이트 배열을 복호화하여 반환합니다.</p>
     * @param cipherType : 복호화 알고리즘, 모드 및 패딩.
     * <pre>
     * - AES_CBC_PKCS5 : 알고리즘:AES-128, 복호화 모드:CBC, 패딩:PKCS5</pre>
     * @param cipherBytes : 암호문 바이트 배열.
     * @param secretKeySpec : SecretKeySpec(byte[] key, String algorithm) 생성자로 생성된 복호화 키.
     * @return 복호화된 바이트 배열.
     */
    public static byte[] decrypt(int cipherType, byte[] cipherBytes, SecretKeySpec secretKeySpec) {
        // 파라미터 검사
        if (cipherBytes == null || cipherBytes.length == 0) {
            logger.error("'cipherBytes' is null or zero length! (cipherBytes:" + cipherBytes + ")");
            return null;
        }
        
        if (secretKeySpec == null) {
            logger.error("'secretKeySpec' is null!");
            return null;
        }
        
        // 복호화 수행
        byte[] plainBytes = null;
        
        if (cipherType == AES_CBC_PKCS5) {
            plainBytes = cipherAES(Cipher.DECRYPT_MODE, cipherBytes, secretKeySpec);
        }
        else if (true) {
            // Add new CipherType here...
        }
        
        // 복호화 결과 검사 및 반환
        if (plainBytes == null) {
            logger.error("The decode reseult 'plainBytes' is null. decoding failed!");
            return null;
        }
        
        return plainBytes;
    }
    
    // AES 암복호화 수행
    private static byte[] cipherAES(int opMode, byte[] inBytes, SecretKeySpec secretKeySpec) {
        // Algorithm    : AES-128 (16byte block)
        // Mode         : CBC
        // Padding      : PKCS5
        byte[] outBytes = null;
        
        try {
            Cipher aesCipher = Cipher.getInstance(TF_AES_CBC_PKCS5);
            aesCipher.init(opMode, secretKeySpec, IV_AES_CBC_PKCS5);
            outBytes = aesCipher.doFinal(inBytes);
        }
        catch (InvalidKeyException | InvalidAlgorithmParameterException | 
                IllegalBlockSizeException | BadPaddingException | 
                NoSuchAlgorithmException | NoSuchPaddingException e) {
            logger.error("Util Exception! ", e);
            return null;
        }
        
        return outBytes;
    }
}
