import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SignupForm extends JFrame {
    private JPanel mainPanel;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton signUpButton;
    private JButton loginButton;

    public SignupForm() {
        setTitle("Sign Up Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);  // Increased height
        setLocationRelativeTo(null);
        setResizable(false);  // Prevent resizing

        // Main panel with padding
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Title
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(70, 130, 180));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Form fields
        nameField = createTextField("Full Name");
        emailField = createTextField("Email");
        phoneField = createTextField("Phone Number");
        passwordField = createPasswordField("Password");
        confirmPasswordField = createPasswordField("Confirm Password");

        // Add space before buttons
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));

        // Sign Up Button
        signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(100, 40));
        signUpButton.setBackground(new Color(70, 130, 180));
        signUpButton.setForeground(Color.blue);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Login Button
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(new Color(46, 125, 50));
        loginButton.setForeground(Color.blue);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(signUpButton);
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel);

        // Add hover effects for buttons
        addButtonHoverEffect(signUpButton, new Color(70, 130, 180), new Color(51, 101, 138));
        addButtonHoverEffect(loginButton, new Color(46, 125, 50), new Color(39, 105, 43));

        // Add action listeners
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    if (registerUser()) {
                        JOptionPane.showMessageDialog(null, "Registration successful! Please login.");
                        dispose();
                        new UserLoginGUI();
                    }
                }
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new UserLoginGUI();
            }
        });

        // Add main panel to frame
        add(mainPanel);
        setVisible(true);
    }

    private void addButtonHoverEffect(JButton button, Color defaultColor, Color hoverColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(defaultColor);
            }
        });
    }

    private JTextField createTextField(String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label Panel
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.setBackground(new Color(240, 240, 240));
        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        labelPanel.add(label);
        panel.add(labelPanel);

        // Field Panel
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fieldPanel.setBackground(new Color(240, 240, 240));
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(250, 35));
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        fieldPanel.add(textField);
        panel.add(fieldPanel);

        mainPanel.add(panel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return textField;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label Panel
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.setBackground(new Color(240, 240, 240));
        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(70, 70, 70));
        labelPanel.add(label);
        panel.add(labelPanel);

        // Field Panel
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        fieldPanel.setBackground(new Color(240, 240, 240));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        fieldPanel.add(passwordField);
        panel.add(fieldPanel);

        mainPanel.add(panel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        return passwordField;
    }

    private boolean registerUser() {
        String sql = "INSERT INTO users (username, email, phone, password) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String username = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, password);

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            }

        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                JOptionPane.showMessageDialog(null,
                        "An account with this email already exists.",
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Database error: " + ex.getMessage(),
                        "Registration Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("SQL Error: " + ex.getMessage());
        }
        return false;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/electronics_shop";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    private boolean validateFields() {
        if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                passwordField.getPassword().length == 0 ||
                confirmPasswordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(null, "All fields are required!");
            return false;
        }

        if (!emailField.getText().contains("@")) {
            JOptionPane.showMessageDialog(null, "Please enter a valid email address!");
            return false;
        }

        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null, "Passwords do not match!");
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new SignupForm());
    }
}