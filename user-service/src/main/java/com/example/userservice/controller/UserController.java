package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/")

@RequiredArgsConstructor
public class UserController {

    private final Greeting greeting;
    private final Environment env;
    private final UserService userService;
    @GetMapping("/health_check")
    public String status(){
        return "It's working in user-service " + env.getProperty("local.server.port")
            + ", server port: "+ env.getProperty("server.port")
            + ", token secret: "+ env.getProperty("token.secret")
            + ", token expire: "+ env.getProperty("token.expiration_time")
            + ", token expire: "+ env.getProperty("spring.datasource.username")
         ;
    }
//    spring:
//  datasource:
//    driver-class-name: org.h2.Driver
//    url: jdbc:h2:mem:testdb
//    username: sa
    @GetMapping("/welcome")
    public String welcome(HttpServletRequest request){
        log.info("Server port={} ",request.getServerPort());
        return String.format("hi there. server port is %s", greeting.getMessage());
    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user){
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers(){
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        });


        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId){
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);

    }

}
