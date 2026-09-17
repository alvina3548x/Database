package dao;

import java.util.List;

public interface IncidentDAOInterface {
    // Blueprint to submit a new disaster report
    boolean addIncidentReport(int userId, String type, int severity, String description);

    // Blueprint to fetch all incident reports
    List<String[]> getAllIncidents();

    // Blueprint to update the status of an incident
    boolean updateIncidentStatus(int incidentId, String status);
}