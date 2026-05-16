package by.bsuir.electroshop.server;

import by.bsuir.electroshop.server.config.ApplicationProperties;
import by.bsuir.electroshop.server.db.DatabaseManager;
import by.bsuir.electroshop.server.handler.RequestHandlerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElectroShopServer {
    public static void main(String[] args) {
        int port = ApplicationProperties.getInt("server.port");
        DatabaseManager.getInstance().initializeDatabase();
        RequestHandlerFactory factory = new RequestHandlerFactory();
        ExecutorService executor = Executors.newFixedThreadPool(20);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("ElectroShop server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                executor.submit(new ClientSession(socket, factory));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка запуска сервера", e);
        }
    }
}
