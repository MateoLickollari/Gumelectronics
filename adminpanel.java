import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.border.TitledBorder;
import java.text.SimpleDateFormat;  // Add this import

class adminpanel extends JFrame {
    // Colors
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);  // Steel Blue
     private static final Color DANGER_COLOR = new Color(220, 53, 69);    // Red
     private static final Color SUCCESS_COLOR = new Color(40, 167, 69);   // Green
     private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
     private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    // Components
    private JTextField idField, nameField, priceField, stockField;
    private JTextArea descriptionArea;
    private JTextArea outputArea;
    private Connection connection;
    private JPanel mainPanel;

    // Main Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton retrieveButton;
    private JButton viewStockButton;
    private JButton initializeButton;
     private JButton addEmployeeButton;
     private JButton viewShiftButton;


    public adminpanel(int adminId, String adminName) {
        SwingUtilities.invokeLater(() -> {
            try {
                setupFrame(adminName);
                initializeComponents();
                setupLayout();
                connectToDatabase();
                addEventListeners();
                setVisible(true);
                logMessage("Admin Panel initialized for: " + adminName);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error initializing AdminPanel: " + e.getMessage(),
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
     // Add this inner class at the bottom of your adminpanel class, before the last closing brace
     private static class EmployeeItem {
         private final int id;
         private final String name;
         private final String username;

         public EmployeeItem(int id, String name, String username) {
             this.id = id;
             this.name = name;
             this.username = username;
         }

         public int getId() {
             return id;
         }

         @Override
         public String toString() {
             return name + " (" + username + ")";
         }
     }

    private void setupFrame(String adminName) {
        setTitle("Admin Panel - " + adminName);
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }


    private void initializeComponents() {
        // Initialize text fields
        idField = new JTextField(20);
        nameField = new JTextField(20);
        priceField = new JTextField(20);
        stockField = new JTextField(20);
        descriptionArea = new JTextArea(6, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Initialize buttons with styled look
        addButton = createStyledButton("Add Product", PRIMARY_COLOR);
        updateButton = createStyledButton("Update Product", PRIMARY_COLOR);
        deleteButton = createStyledButton("Delete Product", PRIMARY_COLOR);
        retrieveButton = createStyledButton("Retrieve Deleted", PRIMARY_COLOR);
        viewStockButton = createStyledButton("View Stock", PRIMARY_COLOR);
        initializeButton = createStyledButton("Initialize Products", PRIMARY_COLOR);
        addEmployeeButton = createStyledButton("Add Employee", PRIMARY_COLOR);
        viewShiftButton = createStyledButton("View Shifts", PRIMARY_COLOR);

        // Output area
        outputArea = new JTextArea(10, 40);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
    }


     private JButton createStyledButton(String text, Color bgColor) {
         JButton button = new JButton(text);
         button.setPreferredSize(new Dimension(160, 40));
         button.setFont(new Font("Arial", Font.BOLD, 14));
         button.setBackground(bgColor);
         button.setForeground(BUTTON_TEXT_COLOR);
         button.setFocusPainted(false);
         button.setBorderPainted(false);
         button.setOpaque(true);
         button.setCursor(new Cursor(Cursor.HAND_CURSOR));

         // Add rounded corners and hover effect
         button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

         // Hover effect
         button.addMouseListener(new MouseAdapter() {
             @Override
             public void mouseEntered(MouseEvent e) {
                 button.setBackground(getDarkerColor(bgColor));
             }

             @Override
             public void mouseExited(MouseEvent e) {
                 button.setBackground(bgColor);
             }

             @Override
             public void mousePressed(MouseEvent e) {
                 button.setBackground(getDarkerColor(getDarkerColor(bgColor)));
             }

             @Override
             public void mouseReleased(MouseEvent e) {
                 button.setBackground(getDarkerColor(bgColor));
             }
         });

         return button;
     }
     private Color getDarkerColor(Color color) {
         float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
         return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - 0.1f));
     }


    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        // Create main panel with padding
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Add search field
        JTextField searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(250, 30));

        // Add placeholder text
        searchField.setForeground(Color.GRAY);
        searchField.setText("Search by ID, name, or price...");
        searchPanel.add(searchField);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setPreferredSize(new Dimension(280, 150));  // Reduced height
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Configure output area
        outputArea.setRows(25);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setPreferredSize(new Dimension(800, 500));  // Increased height
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR),
                        "Product List",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        PRIMARY_COLOR
                ),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);


        // Add search functionality
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText();
                if (!searchText.equals("Search by ID, name, or price...")) {
                    performProductSearch(searchText);
                }
            }
        });

        // Handle placeholder text
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search by ID, name, or price...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search by ID, name, or price...");
                }
            }
        });
    }

    private void performProductSearch(String searchTerm) {
        searchTerm = searchTerm.trim();
        if (searchTerm.equals("Search by ID, name, or price...") || searchTerm.isEmpty()) {
            viewProductStock(); // Show all products if search is empty
            return;
        }

        try {
            String sql = "SELECT * FROM products WHERE " +
                    "product_id LIKE ? OR " +
                    "name LIKE ? OR " +
                    "CAST(price AS CHAR) LIKE ? " +
                    "ORDER BY product_id";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            String searchPattern = "%" + searchTerm + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            StringBuilder output = new StringBuilder();
            output.append("========================= SEARCH RESULTS =========================\n\n");
            output.append(String.format("%-6s | %-20s | %-8s | %-6s | %s\n",
                    "ID", "Name", "Price", "Stock", "Description"));
            output.append("----------------------------------------------------------------------\n");

            boolean found = false;
            while (rs.next()) {
                found = true;
                String name = rs.getString("name");
                if (name.length() > 20) {
                    name = name.substring(0, 17) + "...";
                }

                String description = rs.getString("description");
                String initialLine = String.format("%-6s | %-20s | $%-7.2f | %-6d | %s",
                        rs.getString("product_id"),
                        name,
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        description);
                output.append(initialLine).append("\n");
            }

            if (!found) {
                output.append("No products found matching your search criteria.\n");
            }

            output.append("\n======================================================================\n");
            output.append("Search completed at: ").append(new java.util.Date()).append("\n");

            // Update the output area
            outputArea.setText(output.toString());
            outputArea.setCaretPosition(0);

            if (!found) {
                logMessage("No products found for search term: " + searchTerm);
            } else {
                logMessage("Search completed for term: " + searchTerm);
            }

        } catch (SQLException e) {
            logMessage("Error performing search: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error performing search: " + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));  // Reduced gaps further
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));  // Reduced padding

        // Make buttons smaller
        addButton.setPreferredSize(new Dimension(130, 35));  // Reduced from 160,40
        updateButton.setPreferredSize(new Dimension(130, 35));
        deleteButton.setPreferredSize(new Dimension(130, 35));
        retrieveButton.setPreferredSize(new Dimension(130, 35));
        viewStockButton.setPreferredSize(new Dimension(130, 35));
        initializeButton.setPreferredSize(new Dimension(130, 35));
        addEmployeeButton.setPreferredSize(new Dimension(130, 35));
        viewShiftButton.setPreferredSize(new Dimension(130, 15));

        // Add buttons to panel
        panel.add(addButton);
        panel.add(retrieveButton);
        panel.add(viewStockButton);
        panel.add(initializeButton);
        panel.add(addEmployeeButton);
        panel.add(viewShiftButton);

        return panel;
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/electronics_shop";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            logMessage("Database connection error: " + e.getMessage());
        }
    }

    private void addEventListeners() {
        retrieveButton.addActionListener(e -> retrieveProduct());
        viewStockButton.addActionListener(e -> viewProductStock());
        initializeButton.addActionListener(e -> initializeProducts());
        addEmployeeButton.addActionListener(e -> showAddEmployeeDialog());
        viewShiftButton.addActionListener(e -> showShiftHistoryDialog());
        addButton.addActionListener(e -> showAddProductDialog());
        retrieveButton.addActionListener(e -> showDeletedProducts());


        updateButton.addActionListener(e -> {
            String productId = idField.getText().trim();
            if (!productId.isEmpty()) {
                updateProduct(productId);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a product ID to update.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);

            }
        });

        deleteButton.addActionListener(e -> {
            String productId = idField.getText().trim();
            if (!productId.isEmpty()) {
                deleteProduct(productId);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please enter a product ID to delete.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE);
            }
        });


    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Create fields
        JTextField idField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JTextField priceField = createStyledTextField();
        JTextField stockField = createStyledTextField();
        JTextArea descArea = new JTextArea(5, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Add components to panel
        gbc.gridy = 0;
        panel.add(new JLabel("Product ID:"), gbc);
        gbc.gridy = 1;
        panel.add(idField, gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridy = 3;
        panel.add(nameField, gbc);

        gbc.gridy = 4;
        panel.add(new JLabel("Price:"), gbc);
        gbc.gridy = 5;
        panel.add(priceField, gbc);

        gbc.gridy = 6;
        panel.add(new JLabel("Stock:"), gbc);
        gbc.gridy = 7;
        panel.add(stockField, gbc);

        gbc.gridy = 8;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridy = 9;
        panel.add(new JScrollPane(descArea), gbc);

        // Add button
        JButton addButton = createStyledButton("Add Product", SUCCESS_COLOR);
        addButton.addActionListener(e -> {
            if (validateAndAddProduct(
                    idField.getText(),
                    nameField.getText(),
                    priceField.getText(),
                    stockField.getText(),
                    descArea.getText())) {
                dialog.dispose();
                viewProductStock(); // Refresh the view
            }
        });

        gbc.gridy = 10;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(addButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private boolean validateAndAddProduct(String id, String name, String priceStr,
                                          String stockStr, String description) {
        // Validate input fields
        if (id.trim().isEmpty() || name.trim().isEmpty() || priceStr.trim().isEmpty() ||
                description.trim().isEmpty() || stockStr.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }

        // Validate price and stock format
        double price;
        int stock;
        try {
            price = Double.parseDouble(priceStr);
            stock = Integer.parseInt(stockStr);
            if (price < 0 || stock < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid price and stock values.");
            return false;
        }

        // Insert the new product
        String sql = "INSERT INTO products (product_id, name, price, description, stock_quantity) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id.trim());
            pstmt.setString(2, name.trim());
            pstmt.setDouble(3, price);
            pstmt.setString(4, description.trim());
            pstmt.setInt(5, stock);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logMessage("Added new product: " + name);
                JOptionPane.showMessageDialog(this, "Product added successfully!");
                return true;
            }
        } catch (SQLException e) {
            logMessage("Error adding product: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage());
        }
        return false;
    }

    private void showShiftHistoryDialog() {
        JDialog dialog = new JDialog(this, "View Employee Shifts", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Employee selection panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(BACKGROUND_COLOR);

        JLabel employeeLabel = new JLabel("Select Employee:");
        employeeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JComboBox<EmployeeItem> employeeComboBox = new JComboBox<>();

        // Load employees into combo box
        loadEmployees(employeeComboBox);

        selectionPanel.add(employeeLabel);
        selectionPanel.add(employeeComboBox);

        // Shifts table
        String[] columns = {"Date", "Check In", "Check Out", "Duration", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable shiftsTable = new JTable(model);
        shiftsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(shiftsTable);

        // Add listener to combo box
        employeeComboBox.addActionListener(e -> {
            EmployeeItem selected = (EmployeeItem) employeeComboBox.getSelectedItem();
            if (selected != null) {
                loadShiftHistory(model, selected.getId());
            }
        });

        mainPanel.add(selectionPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void loadEmployees(JComboBox<EmployeeItem> comboBox) {
        try {
            // Query to get employees (excluding admins)
            String sql = "SELECT employee_id, username, full_name FROM employees WHERE role != 'admin'";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            comboBox.removeAllItems(); // Clear existing items

            // Add a default "Select Employee" item
            comboBox.addItem(new EmployeeItem(-1, "Select an employee", ""));

            while (rs.next()) {
                int id = rs.getInt("employee_id");
                String fullName = rs.getString("full_name");
                String username = rs.getString("username");

                // Debug output
                System.out.println("Loading: ID=" + id + ", Name=" + fullName + ", Username=" + username);

                EmployeeItem employee = new EmployeeItem(id, fullName, username);
                comboBox.addItem(employee);
            }

            // Select the default item
            comboBox.setSelectedIndex(0);

        } catch (SQLException e) {
            logMessage("Error loading employees: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error loading employees: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadShiftHistory(DefaultTableModel model, int employeeId) {
        model.setRowCount(0); // Clear existing rows

        try {
            String sql = "SELECT es.*, e.full_name " +
                    "FROM employee_shifts es " +
                    "JOIN employees e ON e.employee_id = es.employee_id " +
                    "WHERE es.employee_id = ? " +
                    "ORDER BY es.check_in DESC";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            while (rs.next()) {
                Timestamp checkIn = rs.getTimestamp("check_in");
                Timestamp checkOut = rs.getTimestamp("check_out");
                String duration = rs.getString("shift_duration");

                String status = checkOut != null ? "Completed" : "Active";
                String checkOutTime = checkOut != null ? timeFormat.format(checkOut) : "Active";

                model.addRow(new Object[]{
                        dateFormat.format(checkIn),
                        timeFormat.format(checkIn),
                        checkOutTime,
                        duration != null ? duration : "In Progress",
                        status
                });
            }

            // Add total hours row
            addTotalHoursRow(model, employeeId);

        } catch (SQLException e) {
            logMessage("Error loading shift history: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error loading shift history: " + e.getMessage());
        }
    }

    private void addTotalHoursRow(DefaultTableModel model, int employeeId) {
        try {
            String sql = "SELECT check_in, check_out, shift_duration " +
                    "FROM employee_shifts WHERE employee_id = ? AND check_out IS NOT NULL";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            int totalSeconds = 0;
            while (rs.next()) {
                String duration = rs.getString("shift_duration");
                if (duration != null) {
                    String[] parts = duration.split(":");
                    int hours = Integer.parseInt(parts[0]);
                    int minutes = Integer.parseInt(parts[1]);
                    int seconds = Integer.parseInt(parts[2]);
                    totalSeconds += hours * 3600 + minutes * 60 + seconds;
                }
            }

            // Format total duration
            String totalDuration = String.format("%02d:%02d:%02d",
                    totalSeconds / 3600,           // hours
                    (totalSeconds % 3600) / 60,    // minutes
                    totalSeconds % 60              // seconds
            );

            // Add empty row for spacing
            model.addRow(new Object[]{"", "", "", "", ""});

            // Add total row
            model.addRow(new Object[]{
                    "TOTAL TIME",
                    "",
                    "",
                    totalDuration,
                    ""
            });
        } catch (SQLException e) {
            logMessage("Error calculating total time: " + e.getMessage());
        }
    }


     private void showAddEmployeeDialog() {
         JDialog dialog = new JDialog(this, "Add New Employee", true);
         dialog.setSize(400, 600);
         dialog.setLocationRelativeTo(this);
         dialog.setResizable(false);

         JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
         panel.setBackground(BACKGROUND_COLOR);

         // Create fields
         JTextField usernameField = createStyledTextField();
         JPasswordField passwordField = createStyledPasswordField();
         JTextField emailField = createStyledTextField();
         JTextField phoneField = createStyledTextField();
         JTextField fullNameField = createStyledTextField();
         JTextField addressField = createStyledTextField();
         JTextField roleField = createStyledTextField();  // New role field

         // Add components with labels
         addEmployeeFormField(panel, "Username:", usernameField);
         addEmployeeFormField(panel, "Password:", passwordField);
         addEmployeeFormField(panel, "Email:", emailField);
         addEmployeeFormField(panel, "Phone:", phoneField);
         addEmployeeFormField(panel, "Full Name:", fullNameField);
         addEmployeeFormField(panel, "Address:", addressField);
         addEmployeeFormField(panel, "Role:", roleField);

         // Add tooltip for role field
         roleField.setToolTipText("Enter role (e.g., admin, employee, manager, cashier)");

         // Add Button
         JButton addButton = createStyledButton("Add Employee", SUCCESS_COLOR);
         addButton.addActionListener(e -> {
             if (validateAndAddEmployee(
                     usernameField.getText(),
                     new String(passwordField.getPassword()),
                     emailField.getText(),
                     phoneField.getText(),
                     fullNameField.getText(),
                     addressField.getText(),
                     roleField.getText())) {
                 dialog.dispose();
             }
         });

         panel.add(Box.createRigidArea(new Dimension(0, 20)));

         // Center the button
         JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         buttonPanel.setBackground(BACKGROUND_COLOR);
         buttonPanel.add(addButton);
         panel.add(buttonPanel);

         dialog.add(panel);
         dialog.setVisible(true);
     }

     private boolean validateAndAddEmployee(String username, String password, String email,
                                            String phone, String fullName, String address, String role) {
         // Validate all fields are filled
         if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                 phone.isEmpty() || fullName.isEmpty() || address.isEmpty() || role.isEmpty()) {
             JOptionPane.showMessageDialog(this,
                     "Please fill in all fields",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Username validation (3-20 characters, letters, numbers, and underscores only)
         if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
             JOptionPane.showMessageDialog(this,
                     "Username must be 3-20 characters long and contain only letters, numbers, and underscores",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Password validation (minimum 8 characters, at least one number and one letter)
         if (!password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")) {
             JOptionPane.showMessageDialog(this,
                     "Password must be at least 8 characters long and contain at least one letter and one number",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Email validation
         if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
             JOptionPane.showMessageDialog(this,
                     "Please enter a valid email address",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Phone validation (10 digits)
         if (!phone.matches("^\\d{10}$")) {
             JOptionPane.showMessageDialog(this,
                     "Phone number must be 10 digits",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Full name validation (2-50 characters, letters and spaces only)
         if (!fullName.matches("^[a-zA-Z\\s]{2,50}$")) {
             JOptionPane.showMessageDialog(this,
                     "Full name must be 2-50 characters long and contain only letters and spaces",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Address validation (minimum 5 characters)
         if (address.length() < 5) {
             JOptionPane.showMessageDialog(this,
                     "Please enter a valid address (minimum 5 characters)",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         // Role validation (letters and spaces only)
         if (!role.matches("^[a-zA-Z\\s]{2,20}$")) {
             JOptionPane.showMessageDialog(this,
                     "Role must be 2-20 characters long and contain only letters and spaces",
                     "Validation Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }

         try {
             // Check if username already exists
             String checkSql = "SELECT COUNT(*) FROM employees WHERE username = ?";
             PreparedStatement checkStmt = connection.prepareStatement(checkSql);
             checkStmt.setString(1, username);
             ResultSet rs = checkStmt.executeQuery();
             rs.next();
             if (rs.getInt(1) > 0) {
                 JOptionPane.showMessageDialog(this,
                         "Username already exists",
                         "Error",
                         JOptionPane.ERROR_MESSAGE);
                 return false;
             }

             // Check if email already exists
             checkSql = "SELECT COUNT(*) FROM employees WHERE email = ?";
             checkStmt = connection.prepareStatement(checkSql);
             checkStmt.setString(1, email);
             rs = checkStmt.executeQuery();
             rs.next();
             if (rs.getInt(1) > 0) {
                 JOptionPane.showMessageDialog(this,
                         "Email already exists",
                         "Error",
                         JOptionPane.ERROR_MESSAGE);
                 return false;
             }

             // Insert new employee
             String sql = "INSERT INTO employees (username, password, email, phone, full_name, address, role) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
             PreparedStatement pstmt = connection.prepareStatement(sql);
             pstmt.setString(1, username);
             pstmt.setString(2, password);
             pstmt.setString(3, email);
             pstmt.setString(4, phone);
             pstmt.setString(5, fullName);
             pstmt.setString(6, address);
             pstmt.setString(7, role.toLowerCase());

             int result = pstmt.executeUpdate();
             if (result > 0) {
                 JOptionPane.showMessageDialog(this,
                         "Employee added successfully!",
                         "Success",
                         JOptionPane.INFORMATION_MESSAGE);
                 logMessage("New employee added: " + username + " (" + fullName + ") - Role: " + role);
                 return true;
             } else {
                 throw new SQLException("Failed to add employee");
             }
         } catch (SQLException ex) {
             logMessage("Error adding employee: " + ex.getMessage());
             JOptionPane.showMessageDialog(this,
                     "Error adding employee: " + ex.getMessage(),
                     "Database Error",
                     JOptionPane.ERROR_MESSAGE);
             return false;
         }
     }
     private void addEmployeeFormField(JPanel panel, String labelText, JComponent field) {
         JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
         fieldPanel.setBackground(BACKGROUND_COLOR);

         JLabel label = new JLabel(labelText);
         label.setPreferredSize(new Dimension(100, 25));
         label.setFont(new Font("Arial", Font.BOLD, 12));

         fieldPanel.add(label);
         fieldPanel.add(field);

         panel.add(fieldPanel);
         panel.add(Box.createRigidArea(new Dimension(0, 5)));
     }

     private JTextField createStyledTextField() {
         JTextField field = new JTextField();
         field.setPreferredSize(new Dimension(200, 30));
         field.setFont(new Font("Arial", Font.PLAIN, 14));
         field.setBackground(Color.WHITE);
         field.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(new Color(200, 200, 200)),
                 BorderFactory.createEmptyBorder(5, 10, 5, 10)
         ));
         return field;
     }

     private JPasswordField createStyledPasswordField() {
         JPasswordField field = new JPasswordField();
         field.setPreferredSize(new Dimension(200, 30));
         field.setFont(new Font("Arial", Font.PLAIN, 14));
         field.setBackground(Color.white);
         field.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(new Color(200, 200, 200)),
                 BorderFactory.createEmptyBorder(5, 10, 5, 10)
         ));
         return field;
     }

    private void initializeProducts() {
        try {
            // Check if products already exist
            Statement checkStmt = connection.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM products");
            rs.next();
            if (rs.getInt(1) > 0) {
                logMessage("Products are already initialized in the database.");
                return;
            }

            // Your original product list
            String[][] products = {
                    {"P001", "GemBook Pro 14", "1299.00", "Sleek and powerful, the GemBook Pro 14 features a 14-inch Retina display, Intel i7 processor, 16GB RAM, and 512GB SSD storage.", "20"},
                    {"P002", "GemBook Air 13", "999.00", "Lightweight and efficient, the GemBook Air 13 is perfect for everyday tasks with Intel i5 processor, 8GB RAM, and 256GB SSD.", "25"},
                    {"P003", "GemTab Pro", "799.00", "Premium tablet with 12.9-inch display, M1 chip, and support for GemPencil and Magic Keyboard.", "30"},
                    {"P004", "GemStation", "1999.00", "High-performance desktop computer with Intel i9, 32GB RAM, 1TB SSD, and dedicated graphics.", "15"},
                    {"P005", "GemMouse", "79.00", "Ergonomic wireless mouse with precision tracking and long battery life.", "50"},
                    {"P006", "GemBoard", "159.00", "Premium wireless keyboard with backlit keys and multi-device connectivity.", "40"},
                    {"P007", "GemView 27", "599.00", "27-inch 4K display with True Tone technology and built-in speakers.", "20"},
                    {"P008", "GemDock Pro", "299.00", "Universal docking station with multiple ports and 4K display support.", "35"},
                    {"P009", "GemDrive 1TB", "199.00", "Portable SSD with 1TB storage and USB-C connectivity.", "45"},
                    {"P010", "GemPods Pro", "249.00", "Wireless earbuds with active noise cancellation and premium sound quality.", "30"}
            };

            String sql = "INSERT INTO products (product_id, name, price, description, stock_quantity) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);

            for (String[] product : products) {
                pstmt.setString(1, product[0]);
                pstmt.setString(2, product[1]);
                pstmt.setDouble(3, Double.parseDouble(product[2]));
                pstmt.setString(4, product[3]);
                pstmt.setInt(5, Integer.parseInt(product[4]));
                pstmt.executeUpdate();
                logMessage("Added product: " + product[1]);
            }

            logMessage("All default products have been initialized successfully!");
            JOptionPane.showMessageDialog(this, "Products initialized successfully!");

        } catch (SQLException e) {
            logMessage("Error initializing products: " + e.getMessage());
        }
    }


    private void retrieveProduct() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM deleted_products ORDER BY deleted_at DESC");

            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No deleted products found.");
                return;
            }

            JDialog dialog = new JDialog(this, "Retrieve Deleted Product", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);

            DefaultTableModel model = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            model.addColumn("ID");
            model.addColumn("Name");
            model.addColumn("Price");
            model.addColumn("Stock");
            model.addColumn("Deleted At");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("product_id"),
                        rs.getString("name"),
                        String.format("$%.2f", rs.getDouble("price")),
                        rs.getInt("stock_quantity"),
                        rs.getTimestamp("deleted_at")
                });
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);
            dialog.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            JButton restoreButton = createStyledButton("Restore Product", SUCCESS_COLOR);
            JButton deletePermButton = createStyledButton("Delete Permanently", DANGER_COLOR);

            restoreButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) table.getValueAt(selectedRow, 0);
                    restoreProduct(productId, dialog);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a product to restore.");
                }
            });

            deletePermButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String productId = (String) table.getValueAt(selectedRow, 0);
                    permanentlyDeleteProduct(productId, dialog);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Please select a product to delete.");
                }
            });

            buttonPanel.add(restoreButton);
            buttonPanel.add(deletePermButton);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            dialog.setVisible(true);

        } catch (SQLException e) {
            logMessage("Error retrieving deleted products: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error retrieving deleted products: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void permanentlyDeleteProduct(String productId, JDialog dialog) {
        try {
            String deleteSql = "DELETE FROM deleted_products WHERE product_id = ?";
            PreparedStatement pstmt = connection.prepareStatement(deleteSql);
            pstmt.setString(1, productId);
            int result = pstmt.executeUpdate();

            if (result > 0) {
                logMessage("Permanently deleted product: " + productId);
                dialog.dispose();
                retrieveProduct(); // Refresh the view
            }
        } catch (SQLException e) {
            logMessage("Error permanently deleting product: " + e.getMessage());
            JOptionPane.showMessageDialog(dialog,
                    "Error permanently deleting product: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void clearFields() {
        // Clear all input fields
        idField.setText("");
        nameField.setText("");
        priceField.setText("");
        stockField.setText("");
        descriptionArea.setText("");

        // Optional: Set focus back to ID field
        idField.requestFocus();

        // Optional: Update the output area
        logMessage("Fields cleared.");
    }

    private void updateProduct(String productId) {
        try {
            // Fetch current product data
            String selectSql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement selectStmt = connection.prepareStatement(selectSql);
            selectStmt.setString(1, productId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Product not found.");
                return;
            }

            // Create dialog for updating
            JDialog dialog = new JDialog(this, "Update Product", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 500);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            // Create and populate fields
            JTextField nameField = new JTextField(rs.getString("name"), 20);
            JTextField priceField = new JTextField(String.valueOf(rs.getDouble("price")), 20);
            JTextField stockField = new JTextField(String.valueOf(rs.getInt("stock_quantity")), 20);
            JTextArea descArea = new JTextArea(rs.getString("description"), 5, 20);
            descArea.setLineWrap(true);
            descArea.setWrapStyleWord(true);

            // Add components to panel
            panel.add(new JLabel("Product ID: " + productId), gbc);
            panel.add(Box.createVerticalStrut(10), gbc);

            panel.add(new JLabel("Name:"), gbc);
            panel.add(nameField, gbc);

            panel.add(new JLabel("Price:"), gbc);
            panel.add(priceField, gbc);

            panel.add(new JLabel("Stock:"), gbc);
            panel.add(stockField, gbc);

            panel.add(new JLabel("Description:"), gbc);
            panel.add(new JScrollPane(descArea), gbc);

            // Update button
            JButton updateButton = createStyledButton("Update", SUCCESS_COLOR);
            updateButton.addActionListener(e -> {
                try {
                    // Validate inputs
                    double price = Double.parseDouble(priceField.getText().trim());
                    int stock = Integer.parseInt(stockField.getText().trim());

                    if (nameField.getText().trim().isEmpty() || descArea.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please fill in all fields.");
                        return;
                    }

                    // Update the product
                    String updateSql = "UPDATE products SET name = ?, price = ?, description = ?, stock_quantity = ? WHERE product_id = ?";
                    PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                    updateStmt.setString(1, nameField.getText().trim());
                    updateStmt.setDouble(2, price);
                    updateStmt.setString(3, descArea.getText().trim());
                    updateStmt.setInt(4, stock);
                    updateStmt.setString(5, productId);

                    int result = updateStmt.executeUpdate();
                    if (result > 0) {
                        JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
                        dialog.dispose();
                        viewProductStock(); // Refresh the view
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numbers for price and stock.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error updating product: " + ex.getMessage());
                }
            });

            // Cancel button
            JButton cancelButton = createStyledButton("Cancel", DANGER_COLOR);
            cancelButton.addActionListener(e -> dialog.dispose());

            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.add(updateButton);
            buttonPanel.add(cancelButton);

            dialog.add(panel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);

        } catch (SQLException e) {
            logMessage("Error updating product: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error updating product: " + e.getMessage());
        }
    }

    private void deleteProduct(String productId) {
        try {
            // First check if the product exists and get its name
            String checkSql = "SELECT name FROM products WHERE product_id = ? AND active = TRUE";
            PreparedStatement checkStmt = connection.prepareStatement(checkSql);
            checkStmt.setString(1, productId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(this, "Product not found or already deleted.");
                return;
            }

            String productName = rs.getString("name");

            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to deactivate product: " + productName + " (ID: " + productId + ")?",
                    "Confirm Deactivation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // Instead of DELETE, update the active status
                String updateSql = "UPDATE products SET active = FALSE WHERE product_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setString(1, productId);

                int result = updateStmt.executeUpdate();
                if (result > 0) {
                    logMessage("Deactivated product: " + productName);
                    JOptionPane.showMessageDialog(this, "Product deactivated successfully!");
                    viewProductStock(); // Refresh the view
                }
            }
        } catch (SQLException e) {
            logMessage("Error deactivating product: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Error deactivating product. This product might be referenced in orders.",
                    "Operation Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void viewProductStock() {
        try {
            outputArea.setEditable(true);
            outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

            StringBuilder output = new StringBuilder();
            output.append("========================= CURRENT PRODUCT STOCK =========================\n\n");
            output.append(String.format("%-6s | %-20s | %-8s | %-6s | %-25s | %s\n",
                    "ID", "Name", "Price", "Stock", "Description", "Actions"));
            output.append("----------------------------------------------------------------------\n");

            // Update the SQL to only show active products
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM products WHERE active = TRUE ORDER BY product_id"
            );

            while (rs.next()) {
                String name = rs.getString("name");
                if (name.length() > 20) {
                    name = name.substring(0, 17) + "...";
                }

                String description = rs.getString("description");
                if (description.length() > 25) {
                    description = description.substring(0, 22) + "...";
                }

                output.append(String.format("%-6s | %-20s | $%-7.2f | %-6d | %-25s | %s %s\n",
                        rs.getString("product_id"),
                        name,
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        description,
                        "✏️", "🗑️"));
            }

            output.append("\n======================================================================\n");
            output.append("Total Active Products: ").append(rs.getRow()).append("\n");
            output.append("Last Updated: ").append(new java.util.Date()).append("\n");
            output.append("\nClick ✏️ to edit or 🗑️ to deactivate a product\n");

            outputArea.setText(output.toString());
            outputArea.setCaretPosition(0);

            // In viewProductStock() method, update the mouse listener section:
            outputArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        int offset = outputArea.viewToModel2D(e.getPoint());
                        int line = outputArea.getLineOfOffset(offset);
                        int lineStart = outputArea.getLineStartOffset(line);
                        int lineEnd = outputArea.getLineEndOffset(line);
                        String lineText = outputArea.getText(lineStart, lineEnd - lineStart);

                        // Check if line contains product data (has both emojis)
                        if (lineText.contains("✏️") && lineText.contains("🗑️")) {
                            String[] parts = lineText.split("\\|");
                            if (parts.length >= 1) {
                                String productId = parts[0].trim();

                                // Determine which emoji was clicked
                                int editPos = lineText.indexOf("✏️");
                                int deletePos = lineText.indexOf("🗑️");
                                int clickPosInLine = offset - lineStart;

                                if (clickPosInLine >= editPos && clickPosInLine < deletePos) {
                                    // Edit emoji clicked
                                    updateProduct(productId);
                                } else if (clickPosInLine >= deletePos) {
                                    // Delete emoji clicked
                                    deleteProduct(productId);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        logMessage("Error processing click: " + ex.getMessage());
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    outputArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    outputArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                }
            });

        } catch (SQLException e) {
            logMessage("Error viewing stock: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error viewing stock: " + e.getMessage());
        }
    }


    private void showDeletedProducts() {
        try {
            // Use deletion_date instead of deleted_at
            String sql = "SELECT p.* FROM deleted_products p ORDER BY p.deletion_date DESC";
            System.out.println("DEBUG - Executing query: " + sql);

            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            DefaultListModel<String> listModel = new DefaultListModel<>();

            while (rs.next()) {
                String productInfo = String.format("%s - %s ($%.2f) - Deleted: %s",
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getTimestamp("deletion_date"));
                listModel.addElement(productInfo);
                System.out.println("DEBUG - Found product: " + productInfo);
            }

            if (listModel.isEmpty()) {
                System.out.println("DEBUG - No products found");
                JOptionPane.showMessageDialog(this, "No deleted products found.");
                return;
            }

            // Create dialog
            JDialog dialog = new JDialog(this, "Deleted Products History", true);
            dialog.setLayout(new BorderLayout(10, 10));

            // Create list
            JList<String> productList = new JList<>(listModel);
            productList.setFont(new Font("Monospaced", Font.PLAIN, 12));

            // Add components
            dialog.add(new JLabel("  Deleted Products History:", SwingConstants.LEFT), BorderLayout.NORTH);
            dialog.add(new JScrollPane(productList), BorderLayout.CENTER);

            // Set dialog properties
            dialog.setSize(500, 400);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

        } catch (SQLException e) {
            System.out.println("DEBUG - SQL Error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving deleted products: " + e.getMessage());
        }
    }


    private void restoreProduct(String productId, JDialog dialog) {
        try {
            // Show confirmation dialog
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Are you sure you want to restore this product?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Update product to active
                String updateSql = "UPDATE products SET active = TRUE WHERE product_id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateSql);
                updateStmt.setString(1, productId);
                int result = updateStmt.executeUpdate();

                if (result > 0) {
                    // Refresh the main product view
                    viewProductStock();

                    logMessage("Product restored: " + productId);
                    JOptionPane.showMessageDialog(dialog, "Product restored successfully!");
                }
            }
        } catch (SQLException ex) {
            logMessage("Error restoring product: " + ex.getMessage());
            JOptionPane.showMessageDialog(dialog,
                    "Error restoring product: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void permanentlyDeleteProduct(String productId, String listItem,
                                          DefaultListModel<String> listModel, JDialog dialog) {
        try {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "WARNING: This will permanently delete the product.\n" +
                            "This action cannot be undone. Continue?",
                    "Confirm Permanent Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                connection.setAutoCommit(false);
                try {
                    // First, get the product details
                    String selectSql = "SELECT * FROM products WHERE product_id = ?";
                    PreparedStatement selectStmt = connection.prepareStatement(selectSql);
                    selectStmt.setString(1, productId);
                    ResultSet rs = selectStmt.executeQuery();

                    if (rs.next()) {
                        // Insert into deleted_products with correct column name
                        String insertSql = "INSERT INTO deleted_products " +
                                "(product_id, name, price, description, stock_quantity, deletion_date) " +
                                "VALUES (?, ?, ?, ?, ?, NOW())";
                        PreparedStatement insertStmt = connection.prepareStatement(insertSql);
                        insertStmt.setString(1, rs.getString("product_id"));
                        insertStmt.setString(2, rs.getString("name"));
                        insertStmt.setDouble(3, rs.getDouble("price"));
                        insertStmt.setString(4, rs.getString("description"));
                        insertStmt.setInt(5, rs.getInt("stock_quantity"));
                        insertStmt.executeUpdate();

                        // Then delete from products
                        String deleteSql = "DELETE FROM products WHERE product_id = ?";
                        PreparedStatement deleteStmt = connection.prepareStatement(deleteSql);
                        deleteStmt.setString(1, productId);
                        deleteStmt.executeUpdate();

                        connection.commit();
                        listModel.removeElement(listItem);

                        if (listModel.isEmpty()) {
                            dialog.dispose();
                        }

                        logMessage("Product permanently deleted and moved to deleted_products: " + productId);
                        JOptionPane.showMessageDialog(dialog, "Product permanently deleted!");
                    }
                } catch (SQLException ex) {
                    connection.rollback();
                    throw ex;
                } finally {
                    connection.setAutoCommit(true);
                }
            }
        } catch (SQLException ex) {
            logMessage("Error permanently deleting product: " + ex.getMessage());
            JOptionPane.showMessageDialog(dialog,
                    "Error permanently deleting product: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


     public static void main(String[] args) {
        try {
            // Set the look and feel to the system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the AdminPanel
        SwingUtilities.invokeLater(() -> {
            adminpanel panel = new adminpanel(1, "Admin");
            panel.setVisible(true);
        });
    }

    private void logMessage(String message) {
        if (outputArea != null) {
            outputArea.append(message + "\n");
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        } else {
            System.out.println(message);
        }
    }
}