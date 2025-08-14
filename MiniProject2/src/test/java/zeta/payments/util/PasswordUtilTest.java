package zeta.payments.util;

import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    // hashPassword Tests
    @Test
    void hashPassword_ValidPassword_ReturnsHashedPassword() {
        String plainPassword = "password123";

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertEquals(60, hashedPassword.length());
    }

    @Test
    void hashPassword_EmptyPassword_ReturnsHashedEmptyString() {
        String plainPassword = "";

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertEquals(60, hashedPassword.length());
    }

    @Test
    void hashPassword_LongPassword_ReturnsHashedPassword() {
        String plainPassword = "a".repeat(100);

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertEquals(60, hashedPassword.length());
    }

    @Test
    void hashPassword_SpecialCharacters_ReturnsHashedPassword() {
        String plainPassword = "p@ssw0rd!@#$%^&*()";

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertEquals(60, hashedPassword.length());
    }

    @Test
    void hashPassword_UnicodeCharacters_ReturnsHashedPassword() {
        String plainPassword = "пароль密码パスワード";

        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hashedPassword);
        assertNotEquals(plainPassword, hashedPassword);
        assertTrue(hashedPassword.startsWith("$2a$"));
        assertEquals(60, hashedPassword.length());
    }

    @Test
    void hashPassword_SamePasswordTwice_ReturnsDifferentHashes() {
        String plainPassword = "password123";

        String hash1 = PasswordUtil.hashPassword(plainPassword);
        String hash2 = PasswordUtil.hashPassword(plainPassword);

        assertNotNull(hash1);
        assertNotNull(hash2);
        assertNotEquals(hash1, hash2);
        assertTrue(hash1.startsWith("$2a$"));
        assertTrue(hash2.startsWith("$2a$"));
    }

    // checkPassword Tests
    @Test
    void checkPassword_CorrectPassword_ReturnsTrue() {
        String plainPassword = "password123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_IncorrectPassword_ReturnsFalse() {
        String plainPassword = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.checkPassword(wrongPassword, hashedPassword);

        assertFalse(result);
    }

    @Test
    void checkPassword_EmptyPasswordWithEmptyHash_ReturnsTrue() {
        String plainPassword = "";
        String hashedPassword = PasswordUtil.hashPassword("");

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_EmptyPasswordWithNonEmptyHash_ReturnsFalse() {
        String plainPassword = "";
        String hashedPassword = PasswordUtil.hashPassword("nonempty");

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertFalse(result);
    }

    @Test
    void checkPassword_NonEmptyPasswordWithEmptyHash_ReturnsFalse() {
        String plainPassword = "nonempty";
        String hashedPassword = PasswordUtil.hashPassword("");

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertFalse(result);
    }


    @Test
    void checkPassword_InvalidHashFormat_ThrowsException() {
        String plainPassword = "password123";
        String invalidHash = "invalidhashformat";

        assertThrows(IllegalArgumentException.class, () -> {
            PasswordUtil.checkPassword(plainPassword, invalidHash);
        });
    }

    @Test
    void checkPassword_SpecialCharacters_ReturnsTrue() {
        String plainPassword = "p@ssw0rd!@#$%^&*()";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_UnicodeCharacters_ReturnsTrue() {
        String plainPassword = "пароль密码パスワード";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_LongPassword_ReturnsTrue() {
        String plainPassword = "a".repeat(100);
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);

        boolean result = PasswordUtil.checkPassword(plainPassword, hashedPassword);

        assertTrue(result);
    }

    @Test
    void checkPassword_CaseSensitive_ReturnsFalse() {
        String plainPassword = "Password123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        String wrongCasePassword = "password123";

        boolean result = PasswordUtil.checkPassword(wrongCasePassword, hashedPassword);

        assertFalse(result);
    }

    @Test
    void checkPassword_WhitespaceMatters_ReturnsFalse() {
        String plainPassword = "password123";
        String hashedPassword = PasswordUtil.hashPassword(plainPassword);
        String passwordWithSpace = "password123 ";

        boolean result = PasswordUtil.checkPassword(passwordWithSpace, hashedPassword);

        assertFalse(result);
    }

    @Test
    void checkPassword_ExternallyGeneratedHash_ReturnsTrue() {
        String plainPassword = "testpassword";
        // Pre-generated BCrypt hash for "testpassword"
        String externalHash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());

        boolean result = PasswordUtil.checkPassword(plainPassword, externalHash);

        assertTrue(result);
    }

    // Integration tests
    @Test
    void integrationTest_HashAndVerifyMultiplePasswords() {
        String[] passwords = {
                "simple",
                "complex!@#123",
                "",
                "a".repeat(50),
                "пароль密码パスワード"
        };

        for (String password : passwords) {
            String hash = PasswordUtil.hashPassword(password);
            assertTrue(PasswordUtil.checkPassword(password, hash));
            assertFalse(PasswordUtil.checkPassword(password + "wrong", hash));
        }
    }

    @Test
    void integrationTest_VerifyHashUniqueness() {
        String password = "testpassword";
        int hashCount = 5;
        String[] hashes = new String[hashCount];

        // Generate multiple hashes for the same password
        for (int i = 0; i < hashCount; i++) {
            hashes[i] = PasswordUtil.hashPassword(password);
        }

        // Verify all hashes are different
        for (int i = 0; i < hashCount; i++) {
            for (int j = i + 1; j < hashCount; j++) {
                assertNotEquals(hashes[i], hashes[j]);
            }
        }

        // Verify all hashes validate the original password
        for (String hash : hashes) {
            assertTrue(PasswordUtil.checkPassword(password, hash));
        }
    }

    @Test
    void integrationTest_PasswordSecurity() {
        String password = "securePassword123!";
        String hash = PasswordUtil.hashPassword(password);

        // Verify hash properties
        assertNotNull(hash);
        assertTrue(hash.length() > password.length());
        assertFalse(hash.contains(password));
        assertTrue(hash.startsWith("$2a$"));

        // Verify password verification works
        assertTrue(PasswordUtil.checkPassword(password, hash));

        // Verify similar passwords don't match
        assertFalse(PasswordUtil.checkPassword("securePassword123", hash));
        assertFalse(PasswordUtil.checkPassword("SecurePassword123!", hash));
        assertFalse(PasswordUtil.checkPassword("securePassword123!!", hash));
    }
}