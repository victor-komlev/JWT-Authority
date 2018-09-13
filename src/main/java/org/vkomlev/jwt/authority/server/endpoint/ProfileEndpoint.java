package org.vkomlev.jwt.authority.server.endpoint;

import org.vkomlev.jwt.authority.server.config.Constants;
import org.vkomlev.jwt.authority.server.dto.User;
import org.vkomlev.jwt.authority.server.filters.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by vkomlev on 2017-05-27.
 */
@RestController
public class ProfileEndpoint {
    @RequestMapping(value = Constants.PROFILE_URL, method = RequestMethod.GET)
    public User get(JwtAuthenticationToken token) {
        return (User) token.getPrincipal();
    }
}
