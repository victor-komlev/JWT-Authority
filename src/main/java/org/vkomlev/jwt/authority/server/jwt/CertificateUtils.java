package org.vkomlev.jwt.authority.server.jwt;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.sql.Blob;

/**
 * Created by vkomlev on 2017-05-28.
 */
@Component
public class CertificateUtils {

    public KeyPair getKeyPair(InputStream certIo, String authorityName, String certPassword)
            throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
            UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(certIo, certPassword.toCharArray());
        Key key = keyStore.getKey(authorityName, certPassword.toCharArray());
        if (key != null && key instanceof PrivateKey) {
            Certificate certificate = keyStore.getCertificate(authorityName);
            if (certificate == null) {
                throw new IllegalArgumentException("No certificate for " + authorityName + " in provided keystore!");
            }
            PublicKey publicKey = certificate.getPublicKey();
            return new KeyPair(publicKey, (PrivateKey)key);
        } else {
            throw new IllegalArgumentException("No private key for " + authorityName + " in provided keystore!");
        }
    }

    public PrivateKey getCertificatePrivateKey(Blob certBlob, String certPassword) {
        return null;
    }

    public PublicKey getCertPublicKey(Blob certBlob, String certPassword) {
        return null;
    }
}
