package com.chemistry.demo.security;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.repository.UserRepository;
import com.chemistry.demo.utils.UserSecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Slf4j
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

                String cognitoSub = jwt.getSubject();

                User user = userRepository.findByCognitoSub(cognitoSub)
                        .orElse(null);



                if (user != null) {

                        for (Role role : user.getRoles()) {



                                // add role
                                authorities.add(
                                        new SimpleGrantedAuthority(
                                                role.getRoleName().name()
                                        )
                                );


                                // add permissions
                                for (Permission permission : role.getPermissions()) {

                                        authorities.add(
                                                new SimpleGrantedAuthority(
                                                        permission.getName().name()
                                                )
                                        );
                                }
                        }
                }

                return new JwtAuthenticationToken(jwt, authorities);
        }
}