import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting application...");

        try {
            testDatabaseConnection();
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Could not set Look and Feel: " + e.getMessage());
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("Creating login form...");
                        LoginFormGUI loginForm = new LoginFormGUI();
                        loginForm.setVisible(true);
                    } catch (Exception e) {
                        System.err.println("Error creating login form:");
                        e.printStackTrace();
                        showErrorDialog("Failed to start application: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            System.err.println("Critical error during startup:");
            e.printStackTrace();
            showErrorDialog("Critical error: " + e.getMessage());
        }
    }

    private static void testDatabaseConnection() {
        System.out.println("Testing database connection...");
        try {
            String url = "jdbc:mysql://localhost:3306/electronics_shop";
            String user = "root";
            String password = "";

            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed:");
            e.printStackTrace();
            showErrorDialog("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private static void showErrorDialog(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        message,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}