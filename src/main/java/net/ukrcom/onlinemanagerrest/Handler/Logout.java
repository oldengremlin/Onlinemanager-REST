/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import net.ukrcom.onlinemanagerrest.Helpers;
import net.ukrcom.onlinemanagerrest.TokenManager;

/**
 *
 * @author olden
 */
public class Logout implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if (!Helpers.checkAuth(exchange)) {
                return;
            }

            String auth = exchange.getRequestHeaders().getFirst("Authorization");
            String token = Helpers.extractToken(auth);

            TokenManager.invalidateToken(token);
            Helpers.sendUnifiedResponse(exchange, 200, "success", "Logout", null);
        }
    }

}
