package com.powerpuff.demo.user;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.powerpuff.demo.security.jwt.JwtUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {

    @GetMapping("/getUsername")
    public String getUsername(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                JwtUtils jwtUtils = new JwtUtils();
                JWTVerifier jwtVerifier = jwtUtils.buildVerifier();
                DecodedJWT decodedJWT = jwtVerifier.verify(authorizationHeader.substring("Bearer ".length()));
                return decodedJWT.getSubject();
            }catch (Exception e){return e.getMessage();}
        } else {
            throw new UsernameNotFoundException("User Not Found");
        }
    }

}
