package org.vkomlev.jwt.authority.server.dto;

/**
 * Created by vkomlev on 2017-05-27.
 */
public enum Scopes {
    REFRESH_TOKEN;

    public String authority() {
        return "ROLE_" + this.name();
    }
}
