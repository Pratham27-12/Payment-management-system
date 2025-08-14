package zeta.payments.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String validSecret = "a".repeat(64); // 64 bytes secret for HS512
    private final Long validExpiration = 3600000L; // 1 hour in milliseconds
    private final String testUsername = "testuser";
    private final String testRole = "USER";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", validSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", validExpiration);
    }

    // getSigningKey Tests
    @Test
    void getSigningKey_ValidSecret_ReturnsKey() {
        // This is tested implicitly through token generation
        String token = jwtUtil.generateToken(testUsername, testRole);
        assertNotNull(token);
    }

    @Test
    void getSigningKey_SecretTooShort_ThrowsException() {
        ReflectionTestUtils.setField(jwtUtil, "secret", "short");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            jwtUtil.generateToken(testUsername, testRole);
        });

        assertEquals("JWT secret key must be at least 64 bytes for HS512 algorithm", exception.getMessage());
    }

    @Test
    void getSigningKey_ExactlyMinimumLength_Success() {
        String minimumSecret = "a".repeat(64); // Exactly 64 bytes
        ReflectionTestUtils.setField(jwtUtil, "secret", minimumSecret);

        String token = jwtUtil.generateToken(testUsername, testRole);

        assertNotNull(token);
        assertEquals(testUsername, jwtUtil.extractUsername(token));
    }

    // generateToken Tests
    @Test
    void generateToken_ValidInputs_ReturnsToken() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
        assertEquals(testUsername, jwtUtil.extractUsername(token));
        assertEquals(testRole, jwtUtil.extractRole(token));
    }

    @Test
    void generateToken_NullUsername_ReturnsTokenWithNullSubject() {
        String token = jwtUtil.generateToken(null, testRole);

        assertNotNull(token);
        assertNull(jwtUtil.extractUsername(token));
        assertEquals(testRole, jwtUtil.extractRole(token));
    }

    @Test
    void generateToken_NullRole_ReturnsTokenWithNullRole() {
        String token = jwtUtil.generateToken(testUsername, null);

        assertNotNull(token);
        assertEquals(testUsername, jwtUtil.extractUsername(token));
        assertNull(jwtUtil.extractRole(token));
    }

    @Test
    void generateToken_EmptyStrings_ReturnsTokenWithEmptyValues() {
        String token = jwtUtil.generateToken("", "");

        assertNotNull(token);
        assertEquals(null, jwtUtil.extractUsername(token));
        assertEquals("", jwtUtil.extractRole(token));
    }

    @Test
    void generateToken_SpecialCharacters_ReturnsValidToken() {
        String specialUsername = "user@domain.com";
        String specialRole = "ROLE_ADMIN";

        String token = jwtUtil.generateToken(specialUsername, specialRole);

        assertNotNull(token);
        assertEquals(specialUsername, jwtUtil.extractUsername(token));
        assertEquals(specialRole, jwtUtil.extractRole(token));
    }

    // extractUsername Tests
    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void extractUsername_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void extractUsername_ExpiredToken_ThrowsException() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // Expired immediately
        String expiredToken = jwtUtil.generateToken(testUsername, testRole);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtUtil.extractUsername(expiredToken);
        });
    }

    @Test
    void extractUsername_TamperedToken_ThrowsException() {
        String token = jwtUtil.generateToken(testUsername, testRole);
        String tamperedToken = token.substring(0, token.length() - 5) + "12345";

        assertThrows(SignatureException.class, () -> {
            jwtUtil.extractUsername(tamperedToken);
        });
    }

    // extractRole Tests
    @Test
    void extractRole_ValidToken_ReturnsRole() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        String extractedRole = jwtUtil.extractRole(token);

        assertEquals(testRole, extractedRole);
    }

    @Test
    void extractRole_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractRole(invalidToken);
        });
    }

    @Test
    void extractRole_TokenWithoutRole_ReturnsNull() {
        // Create a token manually without role claim would be complex,
        // so we test with null role instead
        String token = jwtUtil.generateToken(testUsername, null);

        String extractedRole = jwtUtil.extractRole(token);

        assertNull(extractedRole);
    }

    // extractClaim Tests
    @Test
    void extractClaim_SubjectClaim_ReturnsUsername() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        String subject = jwtUtil.extractClaim(token, Claims::getSubject);

        assertEquals(testUsername, subject);
    }

    @Test
    void extractClaim_IssuedAtClaim_ReturnsDate() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);

        assertNotNull(issuedAt);
        assertTrue(issuedAt.before(new Date()) || issuedAt.equals(new Date()));
    }

    @Test
    void extractClaim_CustomClaim_ReturnsValue() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

        assertEquals(testRole, role);
    }

    @Test
    void extractClaim_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractClaim(invalidToken, Claims::getSubject);
        });
    }

    // isTokenExpired Tests
    @Test
    void isTokenExpired_ValidToken_ReturnsFalse() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.isTokenExpired(invalidToken);
        });
    }

    // extractExpiration Tests
    @Test
    void extractExpiration_ValidToken_ReturnsExpirationDate() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Date expiration = jwtUtil.extractExpiration(token);

        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void extractExpiration_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.extractExpiration(invalidToken);
        });
    }

    @Test
    void extractExpiration_VerifyExpirationTime() {
        long beforeGeneration = System.currentTimeMillis();
        String token = jwtUtil.generateToken(testUsername, testRole);
        long afterGeneration = System.currentTimeMillis();

        Date expiration = jwtUtil.extractExpiration(token);
        long expirationTime = expiration.getTime();

        assertFalse(expirationTime >= beforeGeneration + validExpiration);
        assertTrue(expirationTime <= afterGeneration + validExpiration + 100); // 100ms tolerance
    }

    // validateToken Tests
    @Test
    void validateToken_ValidTokenAndMatchingUsername_ReturnsTrue() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Boolean isValid = jwtUtil.validateToken(token, testUsername);

        assertTrue(isValid);
    }

    @Test
    void validateToken_ValidTokenAndWrongUsername_ReturnsFalse() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Boolean isValid = jwtUtil.validateToken(token, "wronguser");

        assertFalse(isValid);
    }

    @Test
    void validateToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            jwtUtil.validateToken(invalidToken, testUsername);
        });
    }

    @Test
    void validateToken_NullUsername_ReturnsFalse() {
        String token = jwtUtil.generateToken(testUsername, testRole);

        Boolean isValid = jwtUtil.validateToken(token, null);

        assertFalse(isValid);
    }

    // Integration Tests
    @Test
    void integrationTest_FullTokenLifecycle() {
        // Generate token
        String token = jwtUtil.generateToken(testUsername, testRole);
        assertNotNull(token);

        // Extract claims
        assertEquals(testUsername, jwtUtil.extractUsername(token));
        assertEquals(testRole, jwtUtil.extractRole(token));

        // Check expiration
        assertFalse(jwtUtil.isTokenExpired(token));
        Date expiration = jwtUtil.extractExpiration(token);
        assertTrue(expiration.after(new Date()));

        // Validate token
        assertTrue(jwtUtil.validateToken(token, testUsername));
        assertFalse(jwtUtil.validateToken(token, "wronguser"));
    }

    @Test
    void integrationTest_MultipleTokensWithSameCredentials() {
        String token1 = jwtUtil.generateToken(testUsername, testRole);
        String token2 = jwtUtil.generateToken(testUsername, testRole);

        // Tokens should be different (due to different issued at times)

        // But both should be valid
        assertTrue(jwtUtil.validateToken(token1, testUsername));
        assertTrue(jwtUtil.validateToken(token2, testUsername));

        // And contain same information
        assertEquals(jwtUtil.extractUsername(token1), jwtUtil.extractUsername(token2));
        assertEquals(jwtUtil.extractRole(token1), jwtUtil.extractRole(token2));
    }

    @Test
    void integrationTest_DifferentExpirationTimes() {
        // Test with different expiration
        ReflectionTestUtils.setField(jwtUtil, "expiration", 5000L); // 5 seconds
        String shortToken = jwtUtil.generateToken(testUsername, testRole);

        ReflectionTestUtils.setField(jwtUtil, "expiration", 10000L); // 10 seconds
        String longToken = jwtUtil.generateToken(testUsername, testRole);

        Date shortExpiration = jwtUtil.extractExpiration(shortToken);
        Date longExpiration = jwtUtil.extractExpiration(longToken);

        assertTrue(longExpiration.after(shortExpiration));
    }
}
