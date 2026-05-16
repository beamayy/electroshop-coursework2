package by.bsuir.electroshop.client;

import by.bsuir.electroshop.common.dto.Request;
import by.bsuir.electroshop.common.dto.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient implements AutoCloseable {
    private final Socket socket;
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public SocketClient(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.input = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Response send(Request request) throws IOException, ClassNotFoundException {
        output.writeObject(request);
        output.flush();
        return (Response) input.readObject();
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}
