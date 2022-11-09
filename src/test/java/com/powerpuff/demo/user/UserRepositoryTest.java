package com.powerpuff.demo.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@Disabled
class UserRepositoryTest {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private UserRepository underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void shouldPassIfUserEmailExist() {
        String email = "test@gmail.com";
        User user = new User(
                "mi",
                "ke",
                email,
                "123",
                UserRole.USER
        );
        underTest.save(user);

        User expected = underTest.findByEmail(email).orElse(null);

        assertThat(expected).isEqualTo(user);
    }

    @Test
    void shouldPassIfUserEmailDoesNotExist() {

        String email = "test@gmail.com";

        User expected = underTest.findByEmail(email).orElse(null);

        assertThat(expected).isEqualTo(null);
    }

    @Test
    void shouldPassIfUserIsEnable() {
        String email = "test@gmail.com";
        User user = new User(
                "mi",
                "ke",
                email,
                "123",
                UserRole.USER
        );
        underTest.save(user);
        underTest.enableUser(email);

        entityManager.clear();

        User expected = underTest.findByEmail(email).orElse(null);

        assert expected != null;
        assertThat(expected.isEnabled()).isTrue();
    }
}