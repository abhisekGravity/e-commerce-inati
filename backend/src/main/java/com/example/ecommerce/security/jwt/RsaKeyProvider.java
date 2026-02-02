package com.example.ecommerce.security.jwt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
public class RsaKeyProvider {

    public PrivateKey getPrivateKey() throws Exception {
        String key = new String(
                Files.readAllBytes(new ClassPathResource("keys/private.pem").getFile().toPath())
        );

        key = key.replaceAll("-----\\w+ PRIVATE KEY-----", "")
                 .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    public PublicKey getPublicKey() throws Exception {
        String key = new String(
                Files.readAllBytes(new ClassPathResource("keys/public.pem").getFile().toPath())
        );

        key = key.replaceAll("-----\\w+ PUBLIC KEY-----", "")
                 .replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }
}
