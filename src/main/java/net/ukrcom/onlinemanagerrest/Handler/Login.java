/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import net.ukrcom.onlinemanagerrest.Helpers;
import static net.ukrcom.onlinemanagerrest.ParseParams.getFormParams;
import net.ukrcom.onlinemanagerrest.TokenManager;

/**
 *
 * @author olden
 */
public class Login implements HttpHandler {

    /**
     *
     * @param exchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = getFormParams(exchange);

                String token = TokenManager.generateToken(
                        params.getOrDefault("username", ""),
                        params.getOrDefault("password", "")
                );

                String response;
                int status;
                if (token != null) {
                    response = "{\"token\":\"" + token + "\"}";
                    status = 200;
                } else {
                    response = "{\"error\":\"Error credentials\"}";
                    status = 401;
                }

                Helpers.localLog(exchange, status == 200 ? "success" : "error");
                Helpers.sendResponse(exchange, status, response);
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

}
