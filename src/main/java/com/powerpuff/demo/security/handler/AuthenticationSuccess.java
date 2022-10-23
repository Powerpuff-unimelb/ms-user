package com.powerpuff.demo.security.handler;

import com.google.gson.Gson;
import com.powerpuff.demo.security.jwt.JwtUtils;
import com.powerpuff.demo.security.message.Message;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Component
public class AuthenticationSuccess implements AuthenticationSuccessHandler {

    final Gson gson;

    final JwtUtils jwtUtils;

    public AuthenticationSuccess(Gson gson, JwtUtils jwtUtils) {
        this.gson = gson;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();
        List<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        Message result = Message.success("Login success！");
        String accessToken = jwtUtils.generateAccessToken(username, authorities, request.getRequestURL().toString());
        result.add("accessToken", accessToken);
        result.add("refreshToken", jwtUtils.generateRefreshToken(username, request.getRequestURL().toString()));
        System.out.println("token: " + accessToken);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(result));
    }
}