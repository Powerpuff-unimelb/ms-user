package com.powerpuff.userservices;

import com.powerpuff.userservices.model.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUp(RegistrationRequest request) {
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPassword(),
                UserRole.USER
        );
        boolean userExist = userRepository.findByEmail(user.getEmail()).isPresent();
        if (userExist) {
            throw new IllegalStateException("email already taken");
        }

        String encoded = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encoded);

        userRepository.save(user);

        return "success";

        // TODO: send email
    }

    public String enableUser(String email){
        userRepository.enableUser(email);
        return "confirmed";
    }

}
