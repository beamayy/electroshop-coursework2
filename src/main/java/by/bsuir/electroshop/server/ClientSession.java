package by.bsuir.electroshop.server;

import by.bsuir.electroshop.common.dto.Request;
import by.bsuir.electroshop.common.dto.Response;
import by.bsuir.electroshop.server.handler.RequestHandlerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientSession implements Runnable {
    private final Socket socket;
    private final RequestHandlerFactory handlerFactory;

    public ClientSession(Socket socket, RequestHandlerFactory handlerFactory) {
        this.socket = socket;
        this.handlerFactory = handlerFactory;
    }

    @Override
    public void run() {
        try (socket;
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {
            while (true) {
                Object object = input.readObject();
                if (!(object instanceof Request request)) {
                    output.writeObject(Response.error("Некорректный формат запроса"));
                    output.flush();
                    continue;
                }
                Response response = handlerFactory.dispatch(request);
                output.writeObject(response);
                output.flush();
            }
        } catch (EOFException ignored) {
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка клиентской сессии: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неожиданная ошибка: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getName()));
        }
    }
}