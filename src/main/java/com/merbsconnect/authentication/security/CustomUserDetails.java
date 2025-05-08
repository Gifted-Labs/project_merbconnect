package com.merbsconnect.authentication.security;

import com.merbsconnect.authentication.domain.User;
import com.merbsconnect.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
@Getter
@Setter
public class CustomUserDetails implements UserDetails {

    private User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(User user){
        return new CustomUserDetails(
                user,
                mapRolesToAuthorities(user.getRole()) // Map roles to authorities
        );
    }

    /**
     * Maps the user's role to Spring Security's GrantedAuthority format.
     * @param role The role of the user.
     * @return A collection of GrantedAuthority representing the user's role.
     */
    private static Collection<? extends GrantedAuthority> mapRolesToAuthorities(UserRole role){
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
