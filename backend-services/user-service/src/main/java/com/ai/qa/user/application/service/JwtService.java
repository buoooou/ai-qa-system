package com.ai.qa.user.application.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
	/**
	 * 取得注册JWT的用户名
	 * 
	 * @param String jwt
	 * @return String 用户名
	 */
    String extractUsername(String jwt);

	/**
	 * 验证JWT
	 * 
	 * @param String      jwt
	 * @param UserDetails 用户信息
	 * @return boolean
	 */
	boolean isTokenValid(String jwt, UserDetails userDetails);

	/**
	 * 生成JWT token
	 * 
	 * @param username 用户名
	 * @return String token
	 */
	String generateToken(String username);
}
