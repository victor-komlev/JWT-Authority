package org.vkomlev.jwt.authority.server.endpoint.services;

import org.vkomlev.jwt.authority.server.data.UniversalDAO;
import org.vkomlev.jwt.authority.server.dto.Tenant;
import org.vkomlev.jwt.authority.server.exceptions.CertificateUploadException;
import org.vkomlev.jwt.authority.server.jwt.CertificateUtils;
import io.jsonwebtoken.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Created by vkomlev on 2017-05-28.
 */
@Service
public class CertificateService {
    private static Logger LOGGER = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private UniversalDAO universalDAO;

    @Autowired
    private CertificateUtils certificateUtils;

    public void processCertificate(MultipartFile certificate, String certPassword, String authorityName, String apiKey,
            String jwtValidFor) {
        LOGGER.info("processCertificate({}, {}, {}, {})", certificate, authorityName, apiKey, jwtValidFor);
        try {
            Assert.isTrue(!StringUtils.isEmpty(certPassword) && !StringUtils.isEmpty(authorityName) && !StringUtils
                    .isEmpty(apiKey) && !StringUtils.isEmpty(jwtValidFor), "Empty parameters are not allowed!");
            Assert.notNull(UUID.fromString(apiKey));
            Long validPeriod = Long.valueOf(jwtValidFor);
            Assert.isTrue(validPeriod > 0, "Valid Period should be greater than 0");
            List<Tenant> tenants = universalDAO.getAllTenants();
            tenants.stream().forEach((tenant) -> {
                if (tenant.getApiKey().equalsIgnoreCase(apiKey)) {
                    throw new IllegalArgumentException("Tenant with API KEY " + apiKey + " already exists!");
                }
            });
            tenants.stream().forEach((tenant) -> {
                if (tenant.getName().equalsIgnoreCase(authorityName)) {
                    throw new IllegalArgumentException("Tenant with API KEY " + apiKey + " already exists!");
                }
            });
            universalDAO.saveNewTenant(authorityName, apiKey, validPeriod, certPassword, certificate);
        } catch (Exception e) {
            LOGGER.error("processCertificate({}, {}, {}, {})", certificate, authorityName, apiKey, jwtValidFor, e);
            throw new CertificateUploadException(e);
        }

    }

    public List<Tenant> getListOfUploadedCertificates() {
        return universalDAO.getAllTenants();
    }
}
