package com.example.todo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TodoDao {
    private static final String JDBC_URL = "jdbc:sqlite:todo.db";

    public TodoDao() {
        initDatabase();
    }

    private void initDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT NOT NULL);");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public synchronized List<TodoItem> getAll() {
        List<TodoItem> list = new ArrayList<>();
        String sql = "SELECT id, description FROM tasks ORDER BY id";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new TodoItem(rs.getInt("id"), rs.getString("description")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to query tasks", e);
        }
        return list;
    }

    public synchronized TodoItem add(String description) {
        String sql = "INSERT INTO tasks(description) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, description);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Insert failed, no rows affected");
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new TodoItem(id, description);
                } else {
                    throw new SQLException("Insert succeeded but no ID obtained");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add task", e);
        }
    }

    public synchronized boolean delete(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete task", e);
        }
    }
}
