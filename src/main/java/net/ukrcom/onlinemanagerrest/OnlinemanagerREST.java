/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package net.ukrcom.onlinemanagerrest;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import net.ukrcom.onlinemanagerrest.Handler.*;

/**
 *
 * @author olden
 */
public class OnlinemanagerREST {

    public static void main(String[] args) {
        try {
            int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
            var server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/hello", new Hello());
            server.createContext("/getdata", new getData());
            server.createContext("/getduplicate", new getDuplicate());
            server.createContext("/getduplicatesessions", new getDuplicateSessions());
            server.createContext("/getacctstoptimecandidate", new getAcctStopTimeCandidate());
            server.createContext("/correctionacctstoptime", new correctionAcctStopTime());
            server.createContext("/login", new Login());
            server.createContext("/logout", new Logout());

            server.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            server.start();
            System.out.println("OnlinemanagerREST server running on http://" + server.getAddress().toString());

        } catch (IOException ex) {
            Helpers.logger.error(ex.toString());
        }

    }
}
