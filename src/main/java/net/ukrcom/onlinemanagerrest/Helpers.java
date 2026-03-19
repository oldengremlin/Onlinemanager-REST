/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author olden
 */
public class Helpers {

    final public static Logger logger = LoggerFactory.getLogger(OnlinemanagerREST.class);

    public static void sendResponse(HttpExchange exchange, int statusCode, String response) throws
            IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static void sendUnifiedResponse(HttpExchange exchange, int statusCode, String status, Object data, String error) throws
            IOException {

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", status);
        responseMap.put("data", data);
        responseMap.put("error", error);

        localLog(exchange, status + (error != null ? " / " + error : ""));

        String json = new ObjectMapper().writeValueAsString(responseMap);

        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Authorization, Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, json.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }

    public static boolean checkAuth(HttpExchange exchange) throws IOException {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            sendUnifiedResponse(exchange, 401, "error", null, "No authorization");
            return false;
        }

        Helpers.logger.debug("checkAuth: " + auth);

        String token = extractToken(auth);
        if (!TokenManager.isValidToken(token)) {
            sendUnifiedResponse(exchange, 403, "error", null, "Invalid or expired token");
            return false;
        }
        return true;
    }

    public static String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String[] parts = authHeader.substring(7)
                .replaceAll("[{}\"]", "")
                .split(":");
        return parts.length > 1 ? parts[1] : parts[0]; // підтримує і UUID напряму, і {"token":"..."}
    }

    public static void localLog(HttpExchange exchange, String msg) {
        logger.info(
                new java.util.Date()
                + " • " + exchange.getRemoteAddress().toString().replaceAll("/", "")
                + " → " + exchange.getProtocol() + " " + exchange.getRequestMethod() + " " + exchange.getRequestURI()
                + " → " + exchange.getLocalAddress().toString().replaceAll("/", "")
                + " → " + msg
        );
    }

}
