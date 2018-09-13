package org.vkomlev.jwt.authority.server.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.vkomlev.jwt.authority.server.dto.LoginRequestDTO;
import org.vkomlev.jwt.authority.server.exceptions.AuthMethodNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class MainLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {
    private static Logger LOGGER = LoggerFactory.getLogger(MainLoginProcessingFilter.class);

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    private final ObjectMapper objectMapper;

    public MainLoginProcessingFilter(String defaultFilterProcessesUrl, AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler, ObjectMapper mapper) {
        super(defaultFilterProcessesUrl);
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
        this.objectMapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        LOGGER.info("attemptAuthentication({}, {}", httpServletRequest, httpServletResponse);

        if (!HttpMethod.POST.name().equals(httpServletRequest.getMethod())) {
            if (logger.isDebugEnabled()) {
                logger.debug("Authentication method not supported. Request method: " + httpServletRequest.getMethod());
            }
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        LoginRequestDTO
            loginRequest = objectMapper.readValue(httpServletRequest.getReader(), LoginRequestDTO.class);

        if (StringUtils.isEmpty(loginRequest.getUsername()) || StringUtils.isEmpty(loginRequest.getPassword())) {
            throw new AuthenticationServiceException("Username or Password not provided");
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest);

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        LOGGER.info("successfulAuthentication({}, {}, {}, {}", request, response, chain, authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        LOGGER.info("unsuccessfulAuthentication({}, {}, {}", request, response, failed);
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
