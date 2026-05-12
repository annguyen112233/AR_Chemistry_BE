package com.chemistry.demo.security;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.utils.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter
                implements Converter<Jwt, AbstractAuthenticationToken> {

        private final UserRepository userRepository;

        // chuyen doi Jwt thanh AuthenticationToken, lay thong tin user tu database de
        // gan vao authorities
        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {

                Set<SimpleGrantedAuthority> authorities = new HashSet<>();

                Object groupsObj = jwt.getClaims().get("cognito:groups");

                if (groupsObj instanceof Iterable<?> groups) {

                        for (Object group : groups) {

                                authorities.add(
                                                new SimpleGrantedAuthority(
                                                                group.toString()));
                        }
                }

                return new JwtAuthenticationToken(jwt, authorities);
        }
}