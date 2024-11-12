import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class UserLoginGUI extends JFrame {
    private JPanel mainPanel;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton backToSignupButton;
    private JButton forgotPasswordButton;

    public UserLoginGUI() {
        setTitle("User Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Title
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Email Field
        emailField = createTextField("Email");
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password Field
        passwordField = createPasswordField("Password");
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Forgot Password Link
        forgotPasswordButton = new JButton("Forgot Password?");
        forgotPasswordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        forgotPasswordButton.setBorderPainted(false);
        forgotPasswordButton.setContentAreaFilled(false);
        forgotPasswordButton.setForeground(new Color(70, 130, 180));
        forgotPasswordButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordButton.setFont(new Font("Arial", Font.PLAIN, 12));
        mainPanel.add(forgotPasswordButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Login Button
        loginButton = new JButton("Login");
        styleButton(loginButton, new Color(70, 130, 180));

        // Back to Signup Button
        backToSignupButton = new JButton("Back to Signup");
        styleButton(backToSignupButton, new Color(46, 125, 50));

        buttonPanel.add(loginButton);
        buttonPanel.add(backToSignupButton);
        mainPanel.add(buttonPanel);

        // Add Action Listeners
        loginButton.addActionListener(e -> {
            if (validateFields()) {
                loginUser();
            }
        });

        backToSignupButton.addActionListener(e -> {
            dispose();
            new SignupForm();
        });

        forgotPasswordButton.addActionListener(e -> {
            showChangePasswordDialog();
        });

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private JTextField createTextField(String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        JTextField textField = new JTextField(20);
        textField.setMaximumSize(new Dimension(250, 35));
        textField.setPreferredSize(new Dimension(250, 35));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setHorizontalAlignment(JTextField.CENTER);
        panel.add(textField);

        mainPanel.add(panel);
        return textField;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        JPasswordField passField = new JPasswordField(20);
        passField.setMaximumSize(new Dimension(250, 35));
        passField.setPreferredSize(new Dimension(250, 35));
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setHorizontalAlignment(JPasswordField.CENTER);
        panel.add(passField);

        mainPanel.add(panel);
        return passField;
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(350, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        dialog.getContentPane().setBackground(new Color(240, 240, 240));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Email field
        JTextField emailField = new JTextField(20);
        addFieldToPanel(mainPanel, "Email", emailField);

        // New password field
        JPasswordField newPasswordField = new JPasswordField(20);
        addFieldToPanel(mainPanel, "New Password", newPasswordField);

        // Confirm password field
        JPasswordField confirmPasswordField = new JPasswordField(20);
        addFieldToPanel(mainPanel, "Confirm Password", confirmPasswordField);

        // Change Password Button
        JButton changeButton = new JButton("Change Password");
        styleButton(changeButton, new Color(70, 130, 180));
        changeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(changeButton);

        changeButton.addActionListener(e -> {
            if (updatePassword(emailField.getText(),
                    new String(newPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword()))) {
                dialog.dispose();
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void addFieldToPanel(JPanel panel, String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));

        field.setMaximumSize(new Dimension(250, 35));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
    }

    private boolean updatePassword(String email, String newPassword, String confirmPassword) {
        if (email.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return false;
        }

        try (Connection conn = getConnection()) {
            // First verify if email exists
            String checkSql = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, email);

            if (!checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Email not found!");
                return false;
            }

            // Update password
            String updateSql = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newPassword);
            updateStmt.setString(2, email);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
                return true;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
        return false;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(bgColor);
        button.setForeground(Color.black);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
    }

    private void loginUser() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String username = rs.getString("username");
                dispose();
                SwingUtilities.invokeLater(() -> {
                    OnlineShop shop = new OnlineShop(userId, username);
                    shop.setVisible(true);

                    // Show welcome message
                    JOptionPane.showMessageDialog(shop,
                            "Welcome back, " + username + "!",
                            "Login Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid email or password!",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/electronics_shop";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    private boolean validateFields() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid email address!",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

}