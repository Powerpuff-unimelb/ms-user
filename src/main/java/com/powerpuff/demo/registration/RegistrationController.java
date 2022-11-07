package com.powerpuff.demo.registration;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("sign-up")
@AllArgsConstructor
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    public String register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping("confirm")
    public String confirm(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        return registrationService.confirmToken(token, response);
    }
}
