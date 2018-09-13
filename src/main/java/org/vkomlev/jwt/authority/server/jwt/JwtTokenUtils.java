package org.vkomlev.jwt.authority.server.jwt;

import org.vkomlev.jwt.authority.server.data.UniversalDAO;
import org.vkomlev.jwt.authority.server.dto.LoginRequestDTO;
import org.vkomlev.jwt.authority.server.dto.Tenant;
import org.vkomlev.jwt.authority.server.dto.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by vkomlev on 2017-05-27.
 */
@Component
public class JwtTokenUtils {

    @Autowired
    private UniversalDAO universalDAO;

    private JwtParser parser;

    @PostConstruct
    public void init() {
        this.parser = Jwts.parser().setSigningKeyResolver(new SigningKeyResolver() {

            @Override
            public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, String plaintext) {
                return null;
            }

            @Override
            public Key resolveSigningKey(@SuppressWarnings("rawtypes") JwsHeader header, Claims claims) {
                String issuer = claims.getIssuer();
                String subject = claims.getSubject();
                Date expire = claims.getExpiration();
                Date notBefore = claims.getNotBefore();
                String jti = claims.getId();

                if (StringUtils.isEmpty(issuer))
                    throw new JwtException("'iss' claim is required");
                if (StringUtils.isEmpty(subject))
                    throw new JwtException("'sub' claim is required");
                if (expire == null)
                    throw new JwtException("'exp' claim is required");
                if (notBefore == null)
                    throw new JwtException("'nbf' claim is required");
                if (StringUtils.isEmpty(jti))
                    throw new JwtException("'jti' claim is required");

                Tenant tenant = universalDAO.findTenantForUserByUserName(subject);
                if (tenant == null) {
                    throw new JwtException("No tenant for user [" + subject + "]");
                }
                if (!tenant.getApiKey().equalsIgnoreCase(issuer)) {
                    throw new JwtException(
                            "API Key is invalid! provided[" + issuer + "], provisioned[" + tenant.getApiKey() + "]");
                }
                PublicKey pubKey = tenant.getKeyPair().getPublic();
                if (pubKey == null) {
                    throw new JwtException("No public key found for issuer [" + issuer + "]");
                }
                return pubKey;
            }
        });
    }

    public JwtToken createAccessJwtToken(User userContext) {
        if (StringUtils.isEmpty(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        LocalDateTime currentTime = LocalDateTime.now();

        Tenant tenant = universalDAO.findTenantForUserByUserName(userContext.getUsername());
        PrivateKey privateKey = tenant.getKeyPair().getPrivate();

        LoginRequestDTO credentials = userContext.getLoginRequest();
        List<String> claimsToExclude = credentials.getExcludeClaims();
        Claims jwtClaims = Jwts.claims();
        if (!(claimsToExclude != null && claimsToExclude.contains("sub"))) {
            String sub = StringUtils.isEmpty(credentials.getRequiredSub()) ?
                    userContext.getUsername() :
                    credentials.getRequiredSub();
            jwtClaims.setSubject(sub);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("iss"))) {
            String iss = StringUtils.isEmpty(credentials.getRequiredIss()) ?
                    tenant.getApiKey() :
                    credentials.getRequiredIss();
            jwtClaims.setIssuer(iss);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("iat"))) {
            Date iat = credentials.getRequiredIat() == null ?
                    Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()) :
                    new Date(credentials.getRequiredIat() * 1000);
            jwtClaims.setIssuedAt(iat);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("exp"))) {
            Date exp = credentials.getRequiredExp() == null ?
                    Date.from(
                            currentTime.plusMinutes(tenant.getDefaultValidTimeMinutes()).atZone(ZoneId.systemDefault())
                                    .toInstant()) :
                    new Date(credentials.getRequiredExp() * 1000);
            jwtClaims.setExpiration(exp);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("nbf"))) {
            Date nbf = credentials.getRequiredNbf() == null ?
                    Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()) :
                    new Date(credentials.getRequiredNbf() * 1000);
            jwtClaims.setNotBefore(nbf);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("jti"))) {
            String jti = StringUtils.isEmpty(credentials.getRequiredJti()) ?
                    UUID.randomUUID().toString() :
                    credentials.getRequiredJti();
            jwtClaims.setId(jti);
        }
        if (!(claimsToExclude != null && claimsToExclude.contains("authorities"))) {
            jwtClaims.put("authorities",
                    userContext.getAuthorities().stream().map(s -> s.toString()).collect(Collectors.toList()));
        }

        JwtBuilder builder = Jwts.builder().setClaims(jwtClaims);

        String token = builder.signWith(SignatureAlgorithm.RS256, privateKey).compact();

        return new AccessJwtToken(token, jwtClaims);
    }

    public AccessJwtToken parseToken(String token) {
        Jws<Claims> parsedToken = parser.parseClaimsJws(token);
        return new AccessJwtToken(token, parsedToken.getBody());
    }
}
