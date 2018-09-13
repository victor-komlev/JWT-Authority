package org.vkomlev.jwt.authority.server.dto;

import java.security.KeyPair;

/**
 * Created by vkomlev on 2017-05-27.
 */
public class Tenant {
    private Long id;
    private String name;
    private String apiKey;
    private Long defaultValidTimeMinutes;
    private KeyPair keyPair;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getDefaultValidTimeMinutes() {
        return defaultValidTimeMinutes;
    }

    public void setDefaultValidTimeMinutes(Long defaultValidTimeMinutes) {
        this.defaultValidTimeMinutes = defaultValidTimeMinutes;
    }

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    @Override
    public String toString() {
        return "Tenant{" + "id=" + id + ", name='" + name + '\'' + ", apiKey='" + apiKey + '\''
                + ", defaultValidTimeMinutes=" + defaultValidTimeMinutes + '}';
    }
}
