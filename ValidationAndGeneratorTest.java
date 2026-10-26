package com.tanmay.vault;
import com.tanmay.vault.service.PasswordGenerator;import com.tanmay.vault.service.InputValidator;import org.junit.jupiter.api.Test;import static org.junit.jupiter.api.Assertions.*;
class ValidationAndGeneratorTest {
 @Test void rejectsMissingSite(){assertThrows(IllegalArgumentException.class,()->InputValidator.validate(" ","user","pw",""));}
 @Test void generatesRequestedLengthAndCharacterClasses(){String p=new PasswordGenerator().generate(24);assertEquals(24,p.length());assertTrue(p.matches(".*[a-z].*"));assertTrue(p.matches(".*[A-Z].*"));assertTrue(p.matches(".*[2-9].*"));assertTrue(p.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}?].*"));}
}
