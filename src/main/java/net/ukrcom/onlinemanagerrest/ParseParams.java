/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.ukrcom.onlinemanagerrest;

import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author olden
 */
public class ParseParams {

    public static Map<String, String> getQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                    params.put(key, value);
                }
            }
        }
        return params;
    }

    public static Map<String, String> getFormParams(HttpExchange exchange) throws
            IOException {
        Map<String, String> params = new HashMap<>();
        if ("POST".equals(exchange.getRequestMethod())) {
            String body = new BufferedReader(
                    new InputStreamReader(
                            exchange.getRequestBody(),
                            StandardCharsets.UTF_8
                    ))
                    .lines()
                    .collect(
                            Collectors.joining("\n")
                    );

            params = Arrays.stream(body.split("&"))
                    .map(p -> p.split("=", 2))
                    .filter(arr -> !arr[0].isBlank())
                    .collect(
                            Collectors.toMap(
                                    arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                                    arr -> arr.length > 1 ? URLDecoder.decode(arr[1], StandardCharsets.UTF_8) : ""
                            )
                    );
        }
        return params;
    }
}
