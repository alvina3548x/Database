package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SosDAO implements SosDAOInterface {

    @Override
    public boolean createSosRequest(int userId, double latitude, double longitude) {
        String sosSql = "INSERT INTO sos_requests (user_id, status) VALUES (?, 'Active')";
        String gpsSql = "INSERT INTO gps_locations (sos_id, latitude, longitude) VALUES (?, ?, ?)";

        // FIX: Connection outside the try block
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement sosStmt = conn.prepareStatement(sosSql, Statement.RETURN_GENERATED_KEYS)) {
            sosStmt.setInt(1, userId);
            int affectedRows = sosStmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = sosStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newSosId = generatedKeys.getInt(1);

                        // Insert GPS linked to the new SOS ID
                        try (PreparedStatement gpsStmt = conn.prepareStatement(gpsSql)) {
                            gpsStmt.setInt(1, newSosId);
                            gpsStmt.setDouble(2, latitude);
                            gpsStmt.setDouble(3, longitude);
                            gpsStmt.executeUpdate();
                        }
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating SOS Request!");
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<String[]> getActiveSosRequests() {
        List<String[]> activeSos = new ArrayList<>();
        String sql = "SELECT s.sos_id, s.user_id, s.request_time, g.latitude, g.longitude " +
                "FROM sos_requests s LEFT JOIN gps_locations g ON s.sos_id = g.sos_id " +
                "WHERE s.status = 'Active'";

        // FIX: Connection outside the try block
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String sosId = String.valueOf(rs.getInt("sos_id"));
                String userId = String.valueOf(rs.getInt("user_id"));
                String time = rs.getTimestamp("request_time").toString();
                String lat = String.valueOf(rs.getDouble("latitude"));
                String lon = String.valueOf(rs.getDouble("longitude"));

                activeSos.add(new String[]{sosId, userId, time, lat, lon});
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active SOS requests!");
            e.printStackTrace();
        }
        return activeSos;
    }

    @Override
    public boolean updateRescueStatus(int sosId, String message) {
        String sql = "INSERT INTO rescue_status (sos_id, update_message) VALUES (?, ?)";

        // FIX: Connection outside the try block
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, sosId);
            stmt.setString(2, message);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error updating rescue status!");
            e.printStackTrace();
            return false;
        }
    }

    // Test method
    public static void main(String[] args) {
        SosDAO dao = new SosDAO();
        boolean sosCreated = dao.createSosRequest(1, 9.5786, 76.9746); // Dummy GPS for Kuttikkanam
        System.out.println("SOS created with GPS? " + sosCreated);

        System.out.println("\n--- Active SOS Signals ---");
        for (String[] row : dao.getActiveSosRequests()) {
            System.out.println("SOS ID: " + row[0] + " | User: " + row[1] + " | Lat: " + row[3] + " | Lon: " + row[4]);
        }
    }
}