package com.api.beard_soft.controller;

import com.api.beard_soft.dto.user.Login.LoginRequestDto;
import com.api.beard_soft.dto.user.Login.LoginResponseDto;
import com.api.beard_soft.dto.user.UserCreateDto;
import com.api.beard_soft.dto.user.UserResponseDto;
import com.api.beard_soft.dto.user.UserUpdateDto;
import com.api.beard_soft.service.AuthService;
import com.api.beard_soft.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;
    private final UserService userService;

    public AuthenticationController(AuthService authService, UserService userService){
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDto data){
        String token = authService.loginUser(data);
        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserCreateDto data) {
        try {
            authService.registerUser(data);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity registerAdmin(@RequestBody @Valid UserCreateDto data){
        try {
            authService.registerAdmin(data);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getAuthenticatedUser(){
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userIdentifier = authentication.getName();

        UserResponseDto userDetail = userService.findByEmail(userIdentifier);

        return ResponseEntity.ok(userDetail); //retorna informações do usuário(Nome, email, telefone).
    }

    @PutMapping("/update")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody @Valid UserUpdateDto userUpdateDTO){
        org.springframework.security.core.Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userIdentifier = authentication.getName();

        UserResponseDto updatedUser = userService.updateUser(userIdentifier, userUpdateDTO);

        return ResponseEntity.ok(updatedUser);
    }
}
