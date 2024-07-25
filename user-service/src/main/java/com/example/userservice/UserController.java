package com.example.userservice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

    private final Environment environment;

    @GetMapping("/welcome")
    public String test(){
        return "welcome userController";
    }
    @GetMapping("/check")
    public String check(HttpServletRequest request){
        log.info("Server port={} ",request.getServerPort());
        return String.format("hi there. server port is %s", environment.getProperty("local.server.port"));
    }
}
