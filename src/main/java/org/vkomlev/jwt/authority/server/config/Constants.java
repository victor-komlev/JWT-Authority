package org.vkomlev.jwt.authority.server.config;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class Constants {
    public static final String BASE_URL = "/api";
    public static final String PROFILE_URL = BASE_URL + "/profile";

    public static final String JWT_TOKEN_HEADER_PARAM = "Authorization";
    public static final String FORM_BASED_LOGIN_ENTRY_POINT = BASE_URL + "/auth/login";
    public static final String TOKEN_BASED_AUTH_ENTRY_POINT = BASE_URL + "/**";
    public static final String TOKEN_REFRESH_ENTRY_POINT = BASE_URL + "/auth/token";
}
