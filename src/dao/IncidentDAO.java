package dao;

import db.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO implements IncidentDAOInterface {

    // 1. CREATE: Inserts a new disaster report safely into MySQL
    @Override
    public boolean addIncidentReport(int userId, String type, int severity, String description) {
        String sql = "INSERT INTO incident_reports (user_id, disaster_type, severity_level, description) VALUES (?, ?, ?, ?)";

        // FIX: Get the connection outside the try block so it stays open for other methods
        Connection conn = DBConnection.getConnection();

        // Now try-with-resources only closes the statement, not your connection
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setInt(3, severity);
            stmt.setString(4, description);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.out.println("Error inserting incident report!");
            e.printStackTrace();
            return false;
        }
    }

    // 2. READ: Fetches data so Christy can display it in her UI table
    @Override
    public List<String[]> getAllIncidents() {
        List<String[]> incidentList = new ArrayList<>();
        String sql = "SELECT * FROM incident_reports";

        // FIX: Connection moved outside
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = String.valueOf(rs.getInt("incident_id"));
                String userId = String.valueOf(rs.getInt("user_id"));
                String type = rs.getString("disaster_type");
                String severity = String.valueOf(rs.getInt("severity_level"));
                String desc = rs.getString("description");
                String status = rs.getString("status");

                // Package the row into a String array for the UI
                incidentList.add(new String[]{id, userId, type, severity, desc, status});
            }
        } catch (SQLException e) {
            System.out.println("Error fetching incident reports!");
            e.printStackTrace();
        }
        return incidentList;
    }

    // 3. UPDATE: Changes the status (e.g., from 'Reported' to 'Resolved')
    @Override
    public boolean updateIncidentStatus(int incidentId, String status) {
        String sql = "UPDATE incident_reports SET status = ? WHERE incident_id = ?";

        // FIX: Connection moved outside
        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, incidentId);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating incident status!");
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // MAIN METHOD: For you to test it instantly
    // ==========================================
    public static void main(String[] args) {
        IncidentDAO dao = new IncidentDAO();

        // Test 1: Add a new report
        boolean isAdded = dao.addIncidentReport(2, "Flood", 9, "Water levels rising near MBCCET main gate.");
        System.out.println("Incident added successfully? " + isAdded);

        // Test 2: Fetch and print all reports to the console
        System.out.println("\n--- All Incident Reports ---");
        List<String[]> allIncidents = dao.getAllIncidents();
        for (String[] row : allIncidents) {
            System.out.println("ID: " + row[0] + " | User: " + row[1] + " | Type: " + row[2] + " | Status: " + row[5]);
        }
    }
}