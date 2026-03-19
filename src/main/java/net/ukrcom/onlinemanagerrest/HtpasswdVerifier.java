/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.UnixCrypt;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author olden
 */
public class HtpasswdVerifier {

    private final Path htpasswdPath;

    public HtpasswdVerifier() throws IOException {
        this.htpasswdPath = new jConfigSerializing().resolveHtpasswdPath();
    }

    public boolean authenticate(String username, String password)
            throws IOException {

        try (var lines = Files.lines(this.htpasswdPath)) {
            return lines
                    .filter(line -> !line.isBlank() && !line.startsWith("#"))
                    .map(line -> line.split(":", 2))
                    .filter(parts -> parts.length == 2)
                    .filter(parts -> parts[0].equals(username))
                    .findFirst()
                    .map(parts -> verifyPassword(password, parts[1].trim()))
                    .orElse(false);
        }
    }

    private boolean verifyPassword(String plainPassword, String hash) {
        if (hash.startsWith("$2y$") || hash.startsWith("$2a$") || hash.startsWith("$2b$")) {
            // bcrypt — найпоширеніший у сучасних htpasswd
            // Apache генерує $2y$, але jBCrypt розуміє лише $2a$
            return BCrypt.checkpw(plainPassword, hash.replace("$2y$", "$2a$"));
        }

        if (hash.startsWith("{SHA}")) {
            // SHA-1 (застарілий, але зустрічається)
            return verifySha1(plainPassword, hash.substring(5));
        }

        if (hash.startsWith("$apr1$") || hash.startsWith("$1$")) {
            // Apache MD5 — складний власний алгоритм,
            // потребує окремої бібліотеки (commons-codec або shiro)
            throw new UnsupportedOperationException(
                    "MD5-crypt ($apr1$) не підтримується без додаткової бібліотеки"
            );
        }

        if (hash.length() == 13) {
            // Unix DES crypt — саме те, що генерує твій Python-скрипт.
            // Перші 2 символи хешу — це і є salt.
            String salt = hash.substring(0, 2);
            return UnixCrypt.crypt(plainPassword, salt).equals(hash);
        }

        // Plaintext (тільки для тестів, ніколи не використовуйте в prod!)
        return hash.equals(plainPassword);
    }

    private static boolean verifySha1(String plain, String encodedHash) {
        try {
            return Base64.encodeBase64String(
                    MessageDigest.getInstance("SHA-1")
                            .digest(
                                    plain.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                            )
            ).equals(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 недоступний", e);
        }
    }

}
