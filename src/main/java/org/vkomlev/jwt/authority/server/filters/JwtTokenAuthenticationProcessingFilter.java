package org.vkomlev.jwt.authority.server.filters;

import org.vkomlev.jwt.authority.server.config.Constants;
import org.vkomlev.jwt.authority.server.jwt.RawAccessJwtToken;
import org.vkomlev.jwt.authority.server.jwt.TokenExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class JwtTokenAuthenticationProcessingFilter  extends AbstractAuthenticationProcessingFilter {
    private static Logger LOGGER = LoggerFactory.getLogger(JwtTokenAuthenticationProcessingFilter.class);

    private final AuthenticationFailureHandler failureHandler;
    private final TokenExtractor tokenExtractor;

    @Autowired
    public JwtTokenAuthenticationProcessingFilter(AuthenticationFailureHandler failureHandler,
            TokenExtractor tokenExtractor, RequestMatcher matcher) {
        super(matcher);
        this.failureHandler = failureHandler;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
            throws AuthenticationException, IOException, ServletException {
        LOGGER.info("attemptAuthentication({}, {})", httpServletRequest, httpServletResponse);
        String tokenPayload = httpServletRequest.getHeader(Constants.JWT_TOKEN_HEADER_PARAM);
        RawAccessJwtToken token = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));
        return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        LOGGER.info("successfulAuthentication({}, {}, {}, {}}", request, response, chain, authResult);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        chain.doFilter(request, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException, ServletException {
        LOGGER.error("unsuccessfulAuthentication({}, {})", request, response, failed);
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
