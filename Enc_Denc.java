/************************************************************************
                        Encryption and Decryption
                        
                                                Jay Bhatt
************************************************************************/
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Enc_Denc
{
    public String sha128(String in)
    {
        MessageDigest sha;
        byte[] digest;
        try
        {
            sha = MessageDigest.getInstance("SHA-1");
            digest = sha.digest(in.getBytes("UTF-16"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        String encoded = Base64.getEncoder().encodeToString(digest);
        return encoded;
    }
    public String encrypt(String strToEncrypt, String secret)
    {
        try
        {
            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(sha128(secret).getBytes(), 16), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes()));
        }
        catch (Exception e)
        {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    public String decrypt(String strToDecrypt, String secret)
    {
        try
        {
            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(sha128(secret).getBytes(), 16), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e)
        {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
