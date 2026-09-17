package dao;

import java.util.List;

public interface UserDAOInterface {

    List<String[]> getAllUsers();

    boolean addUser(String username, String phone);

    List<String[]> getEmergencyContacts(int userId);
}