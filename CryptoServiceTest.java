package com.tanmay.vault;
import com.tanmay.vault.crypto.CryptoService;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class CryptoServiceTest {
 @Test void encryptsAndDecrypts()throws Exception{CryptoService c=new CryptoService();byte[] k=c.deriveKey("correct horse battery staple".toCharArray(),c.newSalt());String e=c.encrypt("secret",k);assertEquals("secret",c.decrypt(e,k));}
 @Test void rejectsWrongKey()throws Exception{CryptoService c=new CryptoService();byte[] salt=c.newSalt();String e=c.encrypt("secret",c.deriveKey("one".toCharArray(),salt));byte[] wrong=c.deriveKey("two".toCharArray(),salt);assertThrows(SecurityException.class,()->c.decrypt(e,wrong));}
 @Test void rejectsTampering()throws Exception{CryptoService c=new CryptoService();byte[] k=c.deriveKey("password".toCharArray(),c.newSalt());String e=c.encrypt("secret",k);byte[] b=java.util.Base64.getDecoder().decode(e);b[b.length-1]^=1;String tampered=java.util.Base64.getEncoder().encodeToString(b);assertThrows(SecurityException.class,()->c.decrypt(tampered,k));}
}
