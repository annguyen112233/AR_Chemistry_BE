package com.chemistry.demo.security;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public interface UserAuthoritiesProvider {
    Collection<? extends GrantedAuthority> getAuthorities(String cognitoSub);
}
