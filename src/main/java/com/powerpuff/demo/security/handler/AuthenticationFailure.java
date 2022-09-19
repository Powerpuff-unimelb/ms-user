package com.powerpuff.demo.security.handler;

import com.google.gson.Gson;
import com.powerpuff.demo.security.message.Message;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationFailure implements AuthenticationFailureHandler {

    final
    Gson gson;

    public AuthenticationFailure(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        Message msg;
        if (e instanceof UsernameNotFoundException) {
            msg = Message.fail(e.getMessage());
        } else if (e instanceof BadCredentialsException) {
            msg = Message.fail("Incorrect password!");
        } else {
            msg = Message.fail(e.getMessage());
        }
        response.setContentType("text/json;charset=utf-8");
        response.getWriter().write(gson.toJson(msg));
    }
}
