package com.ai.qa.user.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ai.qa.user.api.dto.request.UpdateNickRequest;
import com.ai.qa.user.api.dto.response.Response;
import com.ai.qa.user.api.exception.ErrCode;
import com.ai.qa.user.application.service.UserCaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/nick")
@RequiredArgsConstructor
public class NickController {
    private final UserCaseService userService;

    @PostMapping("/update/{username}")
    @Operation(summary = "更新昵称请求", description = "根据用户名更改昵称")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新登录")
    })
    public ResponseEntity<Response<String>> updateNick(@PathVariable("username") String username,
             @RequestBody UpdateNickRequest updateNickRequest) {
        boolean result = userService.updateNick(username, updateNickRequest.getUsername());
        if (result) {
            Response<String> message = Response.success(ErrCode.REGISTER_SUCCESS);
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(400, ErrCode.UPDATE_NICK_FAILED));
    }

    @GetMapping("/get/{userid}")
    @Operation(summary = "获取新昵称请求", description = "根据用户ID获取昵称")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获得昵称")
    })
    public ResponseEntity<Response<String>> getUserName(@PathVariable("userid") Long userId) {
        String result = userService.getUserName(userId);
        return ResponseEntity.ok(Response.success(result));
    }
}
