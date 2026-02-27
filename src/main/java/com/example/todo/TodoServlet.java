package com.example.todo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/tasks")
public class TodoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private TodoDao dao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        dao = new TodoDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        List<TodoItem> tasks = dao.getAll();
        resp.getWriter().write(gson.toJson(tasks));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        JsonObject json = gson.fromJson(sb.toString(), JsonObject.class);
        String description = json.has("description") ? json.get("description").getAsString() : "";
        TodoItem created = dao.add(description);
        resp.getWriter().write(gson.toJson(created));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        resp.setContentType("application/json");
        if (idParam == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"missing id\"}");
            return;
        }
        try {
            int id = Integer.parseInt(idParam);
            boolean deleted = dao.delete(id);
            if (deleted) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"invalid id\"}");
        }
    }
}
