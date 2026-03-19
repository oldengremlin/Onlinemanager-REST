/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import net.ukrcom.onlinemanagerrest.ParseParams;
import net.ukrcom.onlinemanagerrest.Helpers;
import net.ukrcom.onlinemanagerrest.RadiusData;

/**
 *
 * @author olden
 */
public class correctionAcctStopTime implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {
                if (!Helpers.checkAuth(exchange)) {
                    return;
                }

                Map<String, String> params = ParseParams.getFormParams(exchange);
                String stopTime = params.getOrDefault("stoptime", "");
                long id = Long.parseLong(params.getOrDefault("id", "0"));
                if (id == 0 || stopTime == null) {
                    Helpers.sendUnifiedResponse(exchange, 400, "error", null, "Undefined id or stoptime");
                    return;
                }

                try {
                    boolean success = RadiusData.getInstance().correctionAcctStopTime(id, stopTime);
                    Helpers.sendUnifiedResponse(exchange, 200, "success", Map.of("corrected", success), null);
                } catch (SQLException e) {
                    Helpers.sendUnifiedResponse(exchange, 500, "error", null, "Database error: " + e.getMessage());
                }
            } else {
                Helpers.sendUnifiedResponse(exchange, 405, "error", null, "Method Not Allowed");
            }
        }
    }

}
