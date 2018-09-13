package org.vkomlev.jwt.authority.server.providers;

import org.vkomlev.jwt.authority.server.data.UniversalDAO;
import org.vkomlev.jwt.authority.server.dto.User;
import org.vkomlev.jwt.authority.server.exceptions.JwtValidationException;
import org.vkomlev.jwt.authority.server.filters.JwtAuthenticationToken;
import org.vkomlev.jwt.authority.server.jwt.AccessJwtToken;
import org.vkomlev.jwt.authority.server.jwt.JwtTokenUtils;
import org.vkomlev.jwt.authority.server.jwt.RawAccessJwtToken;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private static Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationProvider.class);

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private UniversalDAO universalDAO;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        LOGGER.info("authenticate({})", authentication);
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();

        AccessJwtToken accessJwtToken;
        try {
            accessJwtToken = jwtTokenUtils.parseToken(rawAccessToken.getToken());
        } catch (Exception eje) {
            LOGGER.error(eje.getMessage(), eje);
            throw new JwtValidationException(rawAccessToken, "JWT Authentication Failed. Unable to parse token.", eje);
        }

        Claims jwsClaims = accessJwtToken.getClaims();
        String subject = jwsClaims.getSubject();
        Date notBefore = jwsClaims.getNotBefore();


        User user = universalDAO.getUserByName(subject);
        if (user != null) {
            return new JwtAuthenticationToken(user, user.getAuthorities());
        }else {
            return new JwtAuthenticationToken(rawAccessToken);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        LOGGER.info("supports({})", authentication);
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
