package com.powerpuff.demo.user;

import com.google.gson.Gson;
import com.powerpuff.demo.Email.EmailService;
import com.powerpuff.demo.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class UserServiceTest {

    @Mock
    ConfirmationTokenService confirmationTokenService;
    @Mock
    EmailService emailService;
    @Mock
    Gson gson;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private AutoCloseable autoCloseable;
    private UserService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new UserService(userRepository, bCryptPasswordEncoder, confirmationTokenService, emailService, gson);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void canLoadUserByUsername() {
        //given
        String email = "test@gmail.com";
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(new User()));

        //when
        underTest.loadUserByUsername(email);

        //then
        verify(userRepository).findByEmail(email);
    }

    @Test
    void canSignUp() {
        //given
        String email = "test@gmail.com";
        String password = "123";
        User user = new User(
                "mi",
                "ke",
                email,
                password,
                UserRole.USER
        );

        //when
        underTest.signUp(user);

        //then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        ArgumentCaptor<String> passwordArgumentCaptor = ArgumentCaptor.forClass(String.class);

        verify(bCryptPasswordEncoder).encode(passwordArgumentCaptor.capture());
        verify(userRepository).save(userArgumentCaptor.capture());

        User capturedUser = userArgumentCaptor.getValue();
        String capturedPassword = passwordArgumentCaptor.getValue();

        assertThat(capturedUser).isEqualTo(user);
        assertThat(capturedPassword).isEqualTo(password);
    }

    @Test
    void willThrowException() {
        //given
        String email = "test@gmail.com";
        String password = "123";
        User user = new User(
                "mi",
                "ke",
                email,
                password,
                UserRole.USER
        );

        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));

        //when
        //then
        assertThatThrownBy(() -> underTest.signUp(user)).isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("email already taken");

        verify(userRepository, never()).save(any());
    }

    @Test
    void enableUser() {
        //given
        String email = "test@gmail.com";

        //when
        underTest.enableUser(email);

        //then
        verify(userRepository).enableUser(email);
    }
}