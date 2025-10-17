package com.ai.qa.gateway.common;

// @Component
// public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    // private final JwtUtil jwtUtil;

    // public JwtReactiveAuthenticationManager(JwtUtil jwtUtil) {
    //     this.jwtUtil = jwtUtil;
    // }

    // @Override
    // public Mono<Authentication> authenticate(Authentication authentication) {
    //     return Mono.just(authentication)
    //             .map(auth -> {
    //                 String token = auth.getCredentials().toString();
    //                 Claims claims = jwtUtil.parseToken(token);
    //                 String username = claims.getSubject();
    //                 @SuppressWarnings("unchecked")
    //                 List<String> roles = claims.get("roles", List.class);

    //                 List<SimpleGrantedAuthority> authorities = roles.stream()
    //                         .map(SimpleGrantedAuthority::new)
    //                         .collect(Collectors.toList());

    //                 List<SimpleGrantedAuthority> authorities = Collections.emptyList();

    //                 return new UsernamePasswordAuthenticationToken(username, null, authorities);
    //             });
    // }
// }
