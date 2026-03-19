/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest.Handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import net.ukrcom.onlinemanagerrest.ParseParams;
import net.ukrcom.onlinemanagerrest.Helpers;
import net.ukrcom.onlinemanagerrest.RadiusData;

/**
 *
 * @author olden
 */
public class getData implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {
                if (!Helpers.checkAuth(exchange)) {
                    return;
                }

                Map<String, String> params = ParseParams.getFormParams(exchange);

                // Витягуємо параметри
                int days = Integer.parseInt(params.getOrDefault("days", "1"));
                boolean onlineOnly = Boolean.parseBoolean(params.getOrDefault("onlineonly", "false"));
                int selectedIndex = Integer.parseInt(params.getOrDefault("selectedindex", "0"));
                String customerFilter = params.getOrDefault("customerfilter", "");
                String usernameFilter = params.getOrDefault("usernamefilter", "");

                try {
                    List<RadiusData.SessionRow> data = RadiusData.getInstance().getData(days, onlineOnly, selectedIndex, customerFilter, usernameFilter);
                    Helpers.sendUnifiedResponse(exchange, 200, "success", data, null);
                } catch (SQLException e) {
                    Helpers.sendUnifiedResponse(exchange, 500, "error", null, "Database error: " + e.getMessage());
                }
            } else {
                Helpers.sendUnifiedResponse(exchange, 405, "error", null, "Method Not Allowed");
            }
        }
    }

}
