package org.vkomlev.jwt.authority.server.data;

import org.vkomlev.jwt.authority.server.dto.Tenant;
import org.vkomlev.jwt.authority.server.dto.User;
import org.vkomlev.jwt.authority.server.jwt.CertificateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class UniversalDAO {
    private static Logger LOGGER = LoggerFactory.getLogger(UniversalDAO.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CertificateUtils certificateUtils;

    public User getUserByName(String userName) {
        String sql = new StringBuilder()
                .append("SELECT u.id AS user_id, u.name AS user_name, u.password AS user_password, ")
                .append("GROUP_CONCAT(ur.role SEPARATOR ',') AS user_roles, t.name as tenant_name ")
                .append("FROM users u ").append("JOIN user_roles ur ON u.id = ur.user_id ")
                .append("JOIN tenants t ON u.tenant_id = t.id ").append("WHERE u.name=?").toString();
        User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> mapUser(rs), userName);
        return user;
    }

    public Tenant findTenantForUserByUserName(String userName) {
        String sql = new StringBuilder().append("SELECT t.id as tenant_id, t.name AS tenant_name, ")
                .append("t.api_key as tenant_api_key, t.default_valid_time_minutes as tenant_default_valid_time_minutes, ")
                .append("t.key_pair_blob as tenant_key_pair_blob FROM tenants t ")
                .append("JOIN users u ON u.tenant_id = t.id ").append("WHERE u.name=?").toString();
        Tenant tenant = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Tenant foundTenant = new Tenant();
            foundTenant.setId(rs.getLong("tenant_id"));
            foundTenant.setName(rs.getString("tenant_name"));
            foundTenant.setApiKey(rs.getString("tenant_api_key"));
            foundTenant.setDefaultValidTimeMinutes(rs.getLong("tenant_default_valid_time_minutes"));
            byte[] keyPair = rs.getBytes("tenant_key_pair_blob");
            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(keyPair))){
                Object object = ois.readObject();
                if (object instanceof KeyPair) {
                    foundTenant.setKeyPair((KeyPair) object);
                } else {
                    throw new RuntimeException("Unable to recover a key pair from blob");
                }
            } catch (Exception e) {
                LOGGER.error("findTenantForUserByUserName()", e);
                throw new RuntimeException(e);
            }
            return foundTenant;
        }, userName);
        return tenant;
    }

    public List<Tenant> getAllTenants() {
        String sql = new StringBuilder().append("SELECT t.id as tenant_id, t.name AS tenant_name, ")
                .append("t.api_key as tenant_api_key, t.default_valid_time_minutes AS tenant_default_valid_time_minutes ")
                .append("FROM tenants t").toString();
        List<Tenant> tenants = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Tenant foundTenant = new Tenant();
            foundTenant.setId(rs.getLong("tenant_id"));
            foundTenant.setName(rs.getString("tenant_name"));
            foundTenant.setApiKey(rs.getString("tenant_api_key"));
            foundTenant.setDefaultValidTimeMinutes(rs.getLong("tenant_default_valid_time_minutes"));
            return foundTenant;
        });
        return tenants;
    }

    public void saveNewTenant(String authorityName, String apiKey, Long validPeriod, String certPassword,
            MultipartFile certificate)
            throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException,
            KeyStoreException, SQLException {
        KeyPair certKeyPair = certificateUtils.getKeyPair(certificate.getInputStream(), authorityName, certPassword);

        String sql = new StringBuilder()
                .append("INSERT INTO tenants (name, api_key, default_valid_time_minutes, key_pair_blob, cert_password) ")
                .append("VALUES (?, ?, ?, ?, ?)").toString();
        PreparedStatement statement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
        statement.setString(1, authorityName);
        statement.setString(2, apiKey);
        statement.setLong(3, validPeriod);
        statement.setObject(4, certKeyPair);
        statement.setString(5, certPassword);
        statement.executeUpdate();
    }

    public List<User> getAllUsers() {
        String sql = new StringBuilder()
                .append("SELECT u.id AS user_id, u.name AS user_name, u.password AS user_password, ")
                .append("GROUP_CONCAT(ur.role SEPARATOR ',') AS user_roles, t.name as tenant_name ")
                .append("FROM users u ").append("JOIN user_roles ur ON u.id = ur.user_id ")
                .append("JOIN tenants t ON u.tenant_id = t.id ").append("GROUP BY u.id").toString();
        List<User> user = jdbcTemplate.query(sql, (rs, rowNum) -> mapUser(rs));
        return user;
    }

    public void saveNewUser(String userName, String userPassword, List<String> roleStrings, Long tenantId)
            throws SQLException {
        String sql = new StringBuilder().append("INSERT INTO users (name, password, tenant_id) ")
                .append("VALUES (?, ?, ?)").toString();
        PreparedStatement statement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
        statement.setString(1, userName);
        statement.setString(2, userPassword);
        statement.setLong(3, tenantId);
        statement.executeUpdate();
        Long userId = this.getUserByName(userName).getId();
        String sqlRoles = new StringBuilder().append("INSERT INTO user_roles (user_id, role) ").append("VALUES (?, ?)")
                .toString();
        roleStrings.stream().forEach(role -> {
            jdbcTemplate.update(sqlRoles, userId, role);
        });
    }

    private User mapUser(ResultSet rs) throws SQLException {
        Long userId = rs.getLong("user_id");
        if (userId < 1)
            return null;
        User userFound = new User();
        userFound.setId(userId);
        userFound.setUsername(rs.getString("user_name"));
        userFound.setPassword(rs.getString("user_password"));
        String userRoles = rs.getString("user_roles");
        if (!StringUtils.isEmpty(userRoles)) {
            List<GrantedAuthority> roles = Arrays.asList(userRoles.split(",")).
                    stream().map((roleString) -> new SimpleGrantedAuthority(roleString)).collect(Collectors.toList());
            userFound.setAuthorities(roles);
        }
        userFound.setTenantName(rs.getString("tenant_name"));
        return userFound;
    }

}
