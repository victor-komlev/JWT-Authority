package org.vkomlev.jwt.authority.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.vkomlev.jwt.authority.server.dto.ErrorCode;
import org.vkomlev.jwt.authority.server.dto.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class MainAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException e) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        if (e instanceof BadCredentialsException) {
            mapper.writeValue(response.getWriter(), ErrorResponseDTO
                    .of("Invalid username or password", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED, null));
        } else if (e != null && e.getCause() != null) {
            mapper.writeValue(response.getWriter(),
                    ErrorResponseDTO.of(e.getMessage(), ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED, e.getCause().getMessage()));
        } else if (e!= null) {
            mapper.writeValue(response.getWriter(),
                    ErrorResponseDTO.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED, e.getMessage()));
        } else {
            mapper.writeValue(response.getWriter(),
                    ErrorResponseDTO.of("Authentication failed", ErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED, null));
        }
    }
}
