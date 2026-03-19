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
import net.ukrcom.onlinemanagerrest.Helpers;
import net.ukrcom.onlinemanagerrest.RadiusData;
import net.ukrcom.onlinemanagerrest.RadiusData.DuplicateDataRow;

/**
 *
 * @author olden
 */
public class getDuplicate implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            if ("POST".equals(exchange.getRequestMethod())) {
                if (!Helpers.checkAuth(exchange)) {
                    return;
                }

                try {
                    List<DuplicateDataRow> data = RadiusData.getInstance().getDuplicateData();
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
