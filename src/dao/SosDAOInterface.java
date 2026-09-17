package dao;

import java.util.List;

public interface SosDAOInterface {
    // Blueprint to trigger an SOS with GPS coordinates
    boolean createSosRequest(int userId, double latitude, double longitude);

    // Blueprint to see all active SOS requests
    List<String[]> getActiveSosRequests();

    // Blueprint to update rescue teams on the situation
    boolean updateRescueStatus(int sosId, String message);
}