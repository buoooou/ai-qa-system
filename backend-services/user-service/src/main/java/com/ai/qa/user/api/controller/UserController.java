package com.ai.qa.user.api.controller;

import com.ai.qa.user.domain.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import com.ai.qa.user.api.dto.ApiResponse;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.UserRequestDto;
import com.ai.qa.user.application.service.JwtService;
import com.ai.qa.user.application.service.UserService;

import lombok.RequiredArgsConstructor;

/**
 * ?????
 *
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;
    
    private final UserService userService;

    /**
     * ??
     * @param user
     * @return
     */
    @GetMapping("/Test")
//    @Operation(summary = "??", description = "??")
    public AuthResponse test(@RequestBody UserRequestDto request){
        System.out.print("??user test ");
        return new AuthResponse("token");
    }


    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") Long userId) {
        System.out.println("??userid");
        return "userid:"+userId;
    }

    /**
     * ??
     * @param user
     * @return
     */
    @PostMapping("/login")
//    @Operation(summary = "??", description = "??")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody UserRequestDto request){
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(ApiResponse.success("????,token??", new AuthResponse(token)));
    }

    /**
     * ??????
     * @param user
     * @return
     */
    @PostMapping("/register")
//    @Operation(summary = "??", description = "??")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody UserRequestDto userRequestDto) {
        // ???????????
        return ResponseEntity.ok(ApiResponse.success("??????", userService.register(userRequestDto)));
    }

    /**
     * ??????
     * @param user
     * @return
     */
    @PostMapping("/updateNickName")
    public ResponseEntity<ApiResponse<?>> updateNickname(@RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("????????", userService.updateNickName(userRequestDto)));
    }

    /**
     * ????
     * @param user
     * @return
     */
    @PostMapping("/updatePassword")
    public ResponseEntity<ApiResponse<?>> updatePassword(@RequestBody UserRequestDto userRequestDto) {
    	userService.updatePassword(userRequestDto);
        return ResponseEntity.ok(ApiResponse.success("??????", null));
    }
}
