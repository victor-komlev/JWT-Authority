package org.vkomlev.jwt.authority.server.jwt;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class TokenExtractor {
    public static String HEADER_PREFIX = "Bearer ";

    public String extract(String header) {
        if (StringUtils.isEmpty(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (header.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header size.");
        }

        return header.substring(HEADER_PREFIX.length(), header.length());
    }
}
