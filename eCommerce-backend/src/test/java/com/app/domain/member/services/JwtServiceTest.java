package com.app.domain.member.services;


import com.app.domain.member.entities.Member;
import com.app.utils.domain.member.RandomMemberBuilder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("UnitTest")
public class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    private final Member member = new RandomMemberBuilder().create();
    private String jwtToken;

    @BeforeAll
    void setup() {
        var claims = new HashMap<String, Object>();
        claims.put("username", member.getUsername());
        jwtToken = jwtService.generateToken(claims, member);

        assertNotNull(jwtToken, "Token should be generated successfully in setup.");
    }

    @Test
    void generateToken_ok() {
        assertNotNull(jwtToken);
    }

    @Test
    void extractUsername_ok() {
        String username = jwtService.extractUsername(jwtToken);

        assertEquals(member.getUsername(), username);
    }

    @Test
    void isTokenValid_true() {
        assertTrue(jwtService.isTokenValid(jwtToken, member));
    }

    @Test
    void isTokenValid_false() {
        Member member2 = new RandomMemberBuilder().create();
        var claims = new HashMap<String, Object>();
        claims.put("username", member2.getUsername());
        String otherToken = jwtService.generateToken(claims, member2);

        assertFalse(jwtService.isTokenValid(otherToken, member));
    }
}
