/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Проста система токенів для авторизації (in-memory, для тесту)
 */
public class TokenManager {

    private static final Map<String, Long> tokens = new ConcurrentHashMap<>();  // токен -> час створення
    private static final long TOKEN_EXPIRY_MS = TimeUnit.MINUTES.toMillis(30);  // 30 хвилин

    /**
     * Генерує новий токен для користувача (після перевірки логін/пароль)
     *
     * @param username
     * @param password
     * @return
     */
    public static String generateToken(String username, String password) {
        try {
            HtpasswdVerifier verifier = new HtpasswdVerifier();
            // Тут перевірка логін/пароль (з конфігу або бази)
            if (verifier.authenticate(username, password)) {
                String token = UUID.randomUUID().toString();
                tokens.put(token, System.currentTimeMillis());
                return token;
            }
        } catch (IOException ex) {
            Helpers.logger.error("Error reading htpasswd: {}", ex.getMessage());
        }

        return null;
    }

    /**
     * Перевіряє, чи токен валідний
     *
     * @param token
     * @return
     */
    public static boolean isValidToken(String token) {
        Long time = tokens.get(token);
        if (time == null) {
            return false;
        }

        // Перевірка терміну дії
        if (System.currentTimeMillis() - time > TOKEN_EXPIRY_MS) {
            tokens.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Видаляє токен (для logout)
     *
     * @param token
     */
    public static void invalidateToken(String token) {
        tokens.remove(token);
    }
}
