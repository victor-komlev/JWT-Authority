package org.vkomlev.jwt.authority.server.providers;

import org.vkomlev.jwt.authority.server.data.UniversalDAO;
import org.vkomlev.jwt.authority.server.dto.LoginRequestDTO;
import org.vkomlev.jwt.authority.server.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class MainAuthenticationProvider implements AuthenticationProvider {

    private static Logger LOGGER = LoggerFactory.getLogger(MainAuthenticationProvider.class);

    @Autowired
    private UniversalDAO universalDAO;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOGGER.info("authenticate({})", authentication);

        Assert.notNull(authentication, "No authentication data provided");

        String username = (String) authentication.getPrincipal();
        String password = ((LoginRequestDTO) authentication.getCredentials()).getPassword();

        User user = universalDAO.getUserByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        if (!user.getPassword().equals(password)) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }

        if (user.getAuthorities() == null) {
            throw new InsufficientAuthenticationException("User has no roles assigned");
        }
        user.setLoginRequest((LoginRequestDTO) authentication.getCredentials());

        return new UsernamePasswordAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        LOGGER.info("supports({})", authentication);
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
