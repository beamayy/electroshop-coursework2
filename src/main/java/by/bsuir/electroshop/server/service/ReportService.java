package by.bsuir.electroshop.server.service;

import by.bsuir.electroshop.common.dto.Response;
import by.bsuir.electroshop.server.repository.ReportRepository;

import java.sql.SQLException;
import java.util.ArrayList;

public class ReportService {
    private final ReportRepository reportRepository = new ReportRepository();

    public Response salesByEmployee() {
        try {
            return Response.ok("Продажи по сотрудникам", new ArrayList<>(reportRepository.salesByEmployee()));
        } catch (SQLException e) {
            return Response.error("Ошибка отчета: " + e.getMessage());
        }
    }

    public Response salesByCategory() {
        try {
            return Response.ok("Продажи по категориям", new ArrayList<>(reportRepository.salesByCategory()));
        } catch (SQLException e) {
            return Response.error("Ошибка отчета: " + e.getMessage());
        }
    }

    public Response dashboardStats() {
        try {
            return Response.ok("Сводная статистика", reportRepository.summary());
        } catch (SQLException e) {
            return Response.error("Ошибка статистики: " + e.getMessage());
        }
    }
}
