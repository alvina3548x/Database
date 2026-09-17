package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements UserDAOInterface {

    @Override
    public List<String[]> getAllUsers() {

        List<String[]> userList = new ArrayList<>();

        String sql = "SELECT u.user_id, u.username, u.phone, " +
                "p.full_name, p.blood_group " +
                "FROM users u " +
                "LEFT JOIN user_profiles p ON u.user_id = p.user_id";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                String id = String.valueOf(rs.getInt("user_id"));
                String username = rs.getString("username");
                String phone = rs.getString("phone");
                String fullName = rs.getString("full_name");
                String bloodGroup = rs.getString("blood_group");

                userList.add(new String[]{
                        id,
                        username,
                        phone,
                        fullName,
                        bloodGroup
                });
            }

        } catch (SQLException e) {

            System.out.println("Error fetching users!");
            e.printStackTrace();
        }

        return userList;
    }


    @Override
    public boolean addUser(String username, String phone) {

        String sql = "INSERT INTO users (username, phone) VALUES (?, ?)";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, phone);

            int rowsInserted = stmt.executeUpdate();

            return rowsInserted > 0;

        } catch (SQLException e) {

            System.out.println("Error adding user!");
            e.printStackTrace();

            return false;
        }
    }


    @Override
    public List<String[]> getEmergencyContacts(int userId) {

        List<String[]> contacts = new ArrayList<>();

        String sql = "SELECT contact_name, contact_phone " +
                "FROM emergency_contacts " +
                "WHERE user_id = ?";

        Connection conn = DBConnection.getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    String name = rs.getString("contact_name");
                    String phone = rs.getString("contact_phone");

                    contacts.add(new String[]{
                            name,
                            phone
                    });
                }
            }

        } catch (SQLException e) {

            System.out.println("Error fetching emergency contacts!");
            e.printStackTrace();
        }

        return contacts;
    }


    // Test method
    public static void main(String[] args) {

        UserDAO dao = new UserDAO();

        // Test adding a user
        boolean userAdded =
                dao.addUser("new_volunteer", "1122334455");

        System.out.println("User added? " + userAdded);


        // Test getting all users
        System.out.println("\n--- All Users ---");

        List<String[]> users = dao.getAllUsers();

        for (String[] row : users) {

            System.out.println(
                    "ID: " + row[0] +
                            " | Username: " + row[1] +
                            " | Phone: " + row[2] +
                            " | Full Name: " + row[3] +
                            " | Blood Group: " + row[4]
            );
        }


        // Test emergency contacts
        System.out.println("\n--- Emergency Contacts ---");

        int testUserId = 1;

        List<String[]> contacts =
                dao.getEmergencyContacts(testUserId);

        for (String[] contact : contacts) {

            System.out.println(
                    "Name: " + contact[0] +
                            " | Phone: " + contact[1]
            );
        }
    }
}