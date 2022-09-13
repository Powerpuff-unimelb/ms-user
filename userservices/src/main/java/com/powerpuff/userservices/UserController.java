package com.powerpuff.userservices;

import com.powerpuff.userservices.model.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("save")
    public String signUp(@RequestBody RegistrationRequest request){
        return userService.signUp(request);
    }

    @GetMapping("enable/{email}")
    public String enableUser(@PathVariable String email) {
        return userService.enableUser(email);
    }

    @GetMapping("{email}")
    public User getUser(@PathVariable String email){
        return userService.findUserByEmail(email);
    }
}
