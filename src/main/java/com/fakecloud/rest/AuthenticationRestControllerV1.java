package com.fakecloud.rest;

import com.fakecloud.dto.bridge.RegistrationRequestDtoToUser;
import com.fakecloud.dto.bridge.UserToUserDtoBridge;
import com.fakecloud.dto.model.UserDto;
import com.fakecloud.dto.request.AuthenticationRequestDto;
import com.fakecloud.dto.request.RegistrationRequestDto;
import com.fakecloud.exception.BadRequestException;
import com.fakecloud.model.User;
import com.fakecloud.security.jwt.JwtTokenProvider;
import com.fakecloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/auth/")
public class AuthenticationRestControllerV1 {
    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    @Autowired
    public AuthenticationRestControllerV1(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto requestDto) {
        try {
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if (user == null) {
                throw new UsernameNotFoundException("User with username: " + username + " not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getRoles());

            Map<Object, Object> response = new HashMap<>();
            response.put("username", username);
            response.put("token", token);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDto requestDto) throws BadRequestException {
        String username = requestDto.getUsername();
        User user = userService.register(RegistrationRequestDtoToUser.build(requestDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "user/{id}")
    public ResponseEntity<UserDto> getUserById(Authentication authentication, @PathVariable(name = "id") Long id){
        User user = userService.findById(id);

        if(user == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(UserToUserDtoBridge.build(user), HttpStatus.OK);
    }
}
