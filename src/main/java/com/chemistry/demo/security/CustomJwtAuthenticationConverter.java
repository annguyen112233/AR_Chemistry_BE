package com.chemistry.demo.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter
                implements Converter<Jwt, AbstractAuthenticationToken> {

        private final UserAuthoritiesProvider authoritiesProvider;

        @Override
        public AbstractAuthenticationToken convert(Jwt jwt) {

                var authorities = authoritiesProvider.getAuthorities(jwt.getSubject());

                return new JwtAuthenticationToken(jwt, authorities);
        }
}