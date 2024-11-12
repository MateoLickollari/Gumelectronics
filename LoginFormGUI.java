import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFormGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeComboBox;

    // Colors and Fonts
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color HOVER_COLOR = new Color(51, 101, 138);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color FIELD_BACKGROUND = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font LINK_FONT = new Font("Arial", Font.PLAIN, 12);

    public LoginFormGUI() {
        setupFrame();
        initializeComponents();

        setVisible(true);
    }

    private void setupFrame() {
        setTitle("GemElectronics Staff Portal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void initializeComponents() {
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));



        // Logo/Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("GemElectronics");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Staff Portal");
        subtitleLabel.setFont(FIELD_FONT);
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);

        mainPanel.add(titlePanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // User Type Selection
        JLabel typeLabel = new JLabel("Select User Type");
        typeLabel.setFont(LABEL_FONT);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(typeLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        String[] userTypes = {"Employee", "Admin"};
        userTypeComboBox = new JComboBox<>(userTypes);
        styleComboBox(userTypeComboBox);
        addCenteredComponent(mainPanel, userTypeComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Username Field
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(LABEL_FONT);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        usernameField = createStyledTextField();
        addCenteredComponent(mainPanel, usernameField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(LABEL_FONT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        passwordField = createStyledPasswordField();
        addCenteredComponent(mainPanel, passwordField);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));


        // Login Button
        JButton loginButton = createStyledButton("Login");
        loginButton.addActionListener(e -> handleLogin());  // Add listener directly here
        addCenteredComponent(mainPanel, loginButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Links Panel
        JPanel linksPanel = new JPanel();
        linksPanel.setLayout(new BoxLayout(linksPanel, BoxLayout.Y_AXIS));
        linksPanel.setBackground(BACKGROUND_COLOR);
        linksPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton forgotPasswordButton = createLinkButton("Forgot Password?");
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());  // Add listener directly

        JButton changePasswordButton = createLinkButton("Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());  // Add listener directly

        linksPanel.add(forgotPasswordButton);
        linksPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        linksPanel.add(changePasswordButton);

        mainPanel.add(linksPanel);
        add(mainPanel);

        // Remove this line as we're adding listeners directly
        // findAndAddLoginButtonListener(mainPanel);
    }

    private void addCenteredComponent(JPanel panel, JComponent component) {
        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapper.setBackground(BACKGROUND_COLOR);
        wrapper.add(component);
        panel.add(wrapper);
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setPreferredSize(new Dimension(300, 35));
        comboBox.setFont(FIELD_FONT);
        comboBox.setBackground(FIELD_BACKGROUND);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(FIELD_FONT);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(FIELD_FONT);
        field.setBackground(FIELD_BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 40));
        button.setFont(BUTTON_FONT);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setForeground(PRIMARY_COLOR);
        button.setFont(LINK_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setForeground(HOVER_COLOR);
                button.setFont(new Font(LINK_FONT.getName(), Font.BOLD, LINK_FONT.getSize()));
            }
            public void mouseExited(MouseEvent e) {
                button.setForeground(PRIMARY_COLOR);
                button.setFont(LINK_FONT);
            }
        });

        return button;
    }


    private void findAndAddLoginButtonListener(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                findAndAddLoginButtonListener((JPanel) comp);
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                switch (button.getText()) {
                    case "Login":
                        button.addActionListener(e -> handleLogin());
                        break;
                    case "Forgot Password?":
                        button.addActionListener(e -> showForgotPasswordDialog());
                        break;
                    case "Change Password":
                        button.addActionListener(e -> showChangePasswordDialog());
                        break;
                }
            }
        }
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeComboBox.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection conn = getConnection()) {
            boolean loginSuccess = false;
            int userId = -1;

            switch (userType) {
                case "Employee":
                    String empQuery = "SELECT employee_id, password FROM employees WHERE username = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(empQuery)) {
                        pstmt.setString(1, username);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next() && rs.getString("password").equals(password)) {
                            loginSuccess = true;
                            userId = rs.getInt("employee_id");
                        }
                    }
                    break;

                case "Admin":
                    String adminQuery = "SELECT admin_id, password FROM admin WHERE username = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(adminQuery)) {
                        pstmt.setString(1, username);
                        ResultSet rs = pstmt.executeQuery();
                        if (rs.next() && rs.getString("password").equals(password)) {
                            loginSuccess = true;
                            userId = rs.getInt("admin_id");

                        }
                    }
                    break;
            }

            if (loginSuccess) {
                dispose();
                switch (userType) {
                    case "Employee":
                        new EmployeePanel(userId, username);
                        break;
                    case "Admin":
                        new adminpanel(userId, username);
                        break;
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void showAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "Add New Employee", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField newUsername = new JTextField(20);
        JPasswordField newPassword = new JPasswordField(20);
        JTextField newEmail = new JTextField(20);
        JTextField newPhone = new JTextField(20);

        panel.add(new JLabel("Username:"));
        panel.add(newUsername);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Password:"));
        panel.add(newPassword);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Email:"));
        panel.add(newEmail);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Phone:"));
        panel.add(newPhone);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton addButton = createStyledButton("Add Employee");
        addButton.addActionListener(e -> {
            if (addNewEmployee(
                    newUsername.getText(),
                    new String(newPassword.getPassword()),
                    newEmail.getText(),
                    newPhone.getText()
            )) {
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Employee added successfully!");
            }
        });

        panel.add(addButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private boolean addNewEmployee(String username, String password, String email, String phone) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return false;
        }

        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO employees (username, password, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage());
            return false;
        }
    }

    private void showForgotPasswordDialog() {
        JDialog dialog = new JDialog(this, "Password Recovery", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] userTypes = {"Employee", "Admin"};
        JComboBox<String> typeCombo = new JComboBox<>(userTypes);
        JTextField usernameField = new JTextField(20);
        JTextField emailField = new JTextField(20);

        panel.add(new JLabel("User Type:"));
        panel.add(typeCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton resetButton = createStyledButton("Reset Password");
        resetButton.addActionListener(e -> {
            handlePasswordReset(
                    (String) typeCombo.getSelectedItem(),
                    usernameField.getText(),
                    emailField.getText(),
                    dialog
            );
        });

        panel.add(resetButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String[] userTypes = {"Employee", "Admin"};
        JComboBox<String> typeCombo = new JComboBox<>(userTypes);
        JTextField usernameField = new JTextField(20);
        JPasswordField currentPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        panel.add(new JLabel("User Type:"));
        panel.add(typeCombo);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Current Password:"));
        panel.add(currentPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(new JLabel("Confirm New Password:"));
        panel.add(confirmPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton changeButton = createStyledButton("Change Password");
        changeButton.addActionListener(e -> {
            handlePasswordChange(
                    (String) typeCombo.getSelectedItem(),
                    usernameField.getText(),
                    new String(currentPasswordField.getPassword()),
                    new String(newPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword()),
                    dialog
            );
        });

        panel.add(changeButton);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void handlePasswordReset(String userType, String username, String email, JDialog dialog) {
        if (username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        try (Connection conn = getConnection()) {
            String table = userType.equals("Admin") ? "admins" : "employees";
            String idColumn = userType.equals("Admin") ? "admin_id" : "employee_id";

            String sql = "SELECT " + idColumn + " FROM " + table + " WHERE username = ? AND email = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Generate a random temporary password
                String tempPassword = generateTempPassword();

                // Update the password in database
                String updateSql = "UPDATE " + table + " SET password = ? WHERE " + idColumn + " = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, tempPassword);
                updateStmt.setInt(2, rs.getInt(1));
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Your temporary password is: " + tempPassword + "\nPlease change it after logging in.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No matching user found");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage());
        }
    }

    private void handlePasswordChange(String userType, String username, String currentPassword,
                                      String newPassword, String confirmPassword, JDialog dialog) {
        if (username.isEmpty() || currentPassword.isEmpty() ||
                newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match");
            return;
        }

        try (Connection conn = getConnection()) {
            String table = userType.equals("Admin") ? "admins" : "employees";
            String idColumn = userType.equals("Admin") ? "admin_id" : "employee_id";

            // Verify current password
            String verifySql = "SELECT " + idColumn + " FROM " + table +
                    " WHERE username = ? AND password = ?";
            PreparedStatement verifyStmt = conn.prepareStatement(verifySql);
            verifyStmt.setString(1, username);
            verifyStmt.setString(2, currentPassword);

            ResultSet rs = verifyStmt.executeQuery();
            if (rs.next()) {
                // Update password
                String updateSql = "UPDATE " + table + " SET password = ? WHERE " + idColumn + " = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, newPassword);
                updateStmt.setInt(2, rs.getInt(1));
                updateStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Password changed successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error changing password: " + e.getMessage());
        }
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int) (chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/electronics_shop";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

}