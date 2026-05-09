package com.chemistry.demo.security;

import com.chemistry.demo.entity.Permission;
import com.chemistry.demo.entity.Role;
import com.chemistry.demo.entity.User;
import com.chemistry.demo.repository.UserRepository;
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

    //chuyen doi Jwt thanh AuthenticationToken, lay thong tin user tu database de gan vao authorities
    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        String cognitoSub = jwt.getSubject();

        User user = userRepository.findByCognitoSub(cognitoSub)
                .orElse(null);

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        if (user != null) {

            for (Role role : user.getRoles()) {

                authorities.add(
                        new SimpleGrantedAuthority(
                                role.getRoleName().name()
                        )
                );

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