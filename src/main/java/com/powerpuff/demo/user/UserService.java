package com.powerpuff.demo.user;

import com.google.gson.Gson;
import com.powerpuff.demo.Email.EmailDetails;
import com.powerpuff.demo.Email.EmailService;
import com.powerpuff.demo.registration.token.ConfirmationToken;
import com.powerpuff.demo.registration.token.ConfirmationTokenService;
import com.powerpuff.demo.security.message.Message;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    @Autowired
    private final Gson gson;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
                .orElse(null);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public String signUp(User user) {
        boolean userExist = userRepository.findByEmail(user.getEmail()).isPresent();

        if (userExist) {
            Message result = Message.fail("Email has been taken");
            return gson.toJson(result);
        }

        String encoded = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encoded);

        userRepository.save(user);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String link = "http://localhost:8082/sign-up/confirm?token=" + token;
//        String emailState = emailService.sendHtmlMail(emailService.buildEmail(user.getEmail(), user.getFirstName(), link));

        Message result = Message.success("Sign up successful");
//        result.add("EmailService", emailState);
        result.add("token", token);

        return gson.toJson(result);
    }

    public void enableUser(String email) {
        userRepository.enableUser(email);
    }

}
