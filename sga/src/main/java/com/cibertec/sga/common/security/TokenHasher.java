package com.cibertec.sga.common.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Utilidad estática (no se inyecta) para hashear el valor crudo de un refresh token antes de
 * persistirlo — nunca se guarda el valor crudo en la base de datos.
 */
public final class TokenHasher {

    private TokenHasher() {
    }

    public static String sha256Hex(String rawValue) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawValue.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
