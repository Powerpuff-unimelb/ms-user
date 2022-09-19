package com.powerpuff.demo.security.handler;

import com.google.gson.Gson;
import com.powerpuff.demo.security.message.Message;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//login off-site
@Component
public class SessionInformationExpired implements org.springframework.security.web.session.SessionInformationExpiredStrategy {

    final
    Gson gson;

    public SessionInformationExpired(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        Message result = Message.fail("Your account is logged in off-site");
        HttpServletResponse response = event.getResponse();
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(gson.toJson(result));
    }
}