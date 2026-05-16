package by.bsuir.electroshop.server.handler;

import by.bsuir.electroshop.common.dto.Request;
import by.bsuir.electroshop.common.dto.Response;

@FunctionalInterface
public interface RequestHandler {
    Response handle(Request request);
}
