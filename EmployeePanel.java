import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.awt.Window;

public class EmployeePanel extends JFrame {
    private int employeeId;
    private String username;
    private JPanel mainPanel;
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color HOVER_COLOR = new Color(51, 101, 138);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private Timer activeShiftTimer;
    private JTable shiftTable;
    private DefaultTableModel shiftModel;


    public EmployeePanel(int employeeId, String username) {
        this.employeeId = employeeId;  // Already have this
        this.username = username;       // Add this line

        SwingUtilities.invokeLater(() -> {
            try {
                setupFrame(username);
                initializeComponents();
                setupLayout();
                setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error initializing Employee Panel: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void setupLayout() {
    }

    private void setupFrame(String username) {
        setTitle("Employee - " + this.username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        add(mainPanel);
    }

    private void initializeComponents() {
        // Top Panel with Welcome Message and Logout
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center Panel with Main Functionality
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
    }
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Welcome Message
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        // Right-side buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // Change Password Button
        JButton changePasswordBtn = new JButton("Change Password");
        styleButton(changePasswordBtn, PRIMARY_COLOR);
        changePasswordBtn.addActionListener(e -> showChangePasswordDialog());

        // Logout Button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(198, 40, 40));  // Red color
        logoutButton.addActionListener(e -> logout());

        buttonsPanel.add(changePasswordBtn);
        buttonsPanel.add(logoutButton);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        return topPanel;
    }
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;  // Changed from HORIZONTAL to BOTH
        gbc.insets = new Insets(20, 20, 20, 20);  // Increased padding
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;  // Added weight for vertical filling

        // First row - takes up 40% of vertical space
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;  // Reset gridwidth
        addFunctionButton(centerPanel, "Manage Products", e -> manageProducts(), gbc);

        gbc.gridx = 1;
        addFunctionButton(centerPanel, "View Orders", e -> viewOrders(), gbc);

        // Second row - takes up 60% of vertical space
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;  // Span across both columns
        gbc.weighty = 0.6;  // Make the bottom button larger
        addFunctionButton(centerPanel, "View Profile", e -> viewProfile(), gbc);

        return centerPanel;
    }

    private void addFunctionButton(JPanel panel, String text, ActionListener listener, GridBagConstraints gbc) {
        JButton button = new JButton(text);
        styleButton(button, PRIMARY_COLOR);
        button.addActionListener(listener);
        button.setPreferredSize(new Dimension(200, 100));  // Set minimum size
        panel.add(button, gbc);
    }
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

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
    private void manageProducts() {
        JDialog dialog = new JDialog(this, "Manage Products", true);
        dialog.setSize(1000, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Table setup
        String[] columns = {"ID", "Name", "Price", "Stock", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);

        // Enable line wrapping for the description column
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            private final JTextArea textArea = new JTextArea();

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                textArea.setText((String) value);
                textArea.setWrapStyleWord(true);
                textArea.setLineWrap(true);
                textArea.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                textArea.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());

                // Adjust row height based on content
                int preferredHeight = textArea.getPreferredSize().height;
                if (table.getRowHeight(row) != preferredHeight) {
                    table.setRowHeight(row, preferredHeight);
                }

                return textArea;
            }
        });

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150);  // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80);   // Price
        table.getColumnModel().getColumn(3).setPreferredWidth(80);   // Stock
        table.getColumnModel().getColumn(4).setPreferredWidth(400);  // Description

        // Create scroll pane with adjusted preferred size
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 400));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addButton = new JButton("Add Product");
        JButton editButton = new JButton("Edit Product");
        JButton deleteButton = new JButton("Delete Product");

        styleButton(addButton, PRIMARY_COLOR);
        styleButton(editButton, PRIMARY_COLOR);
        styleButton(deleteButton, new Color(198, 40, 40));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Load products
        loadProductsForManagement(model);

        // Add Button Action
        addButton.addActionListener(e -> showProductDialog(model, null));

        // Edit Button Action
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a product to edit");
                return;
            }
            String productId = model.getValueAt(selectedRow, 0).toString();
            showProductDialog(model, productId);
        });

        // Delete Button Action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a product to delete");
                return;
            }

            String productId = model.getValueAt(selectedRow, 0).toString();
            deleteProduct(productId, model, selectedRow);
        });

        mainPanel.add(buttonPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void loadProductsForManagement(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM products";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("description"),

                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
        }
    }

    // Helper method for creating consistent text fields
    private JTextField createStyledTextField(Dimension size) {
        JTextField field = new JTextField();
        field.setMaximumSize(size);
        field.setPreferredSize(size);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        return field;
    }

    // Helper method for adding styled fields
    private void addStyledField(JPanel panel, String labelText, JComponent field, Dimension labelSize) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(labelSize);

        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
    }
    private void showProductDialog(DefaultTableModel model, String productId) {
        JDialog dialog = new JDialog(this, productId == null ? "Add Product" : "Edit Product", true);
        dialog.setSize(600, 800);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(BACKGROUND_COLOR);

        // Create form fields with consistent sizing
        Dimension fieldSize = new Dimension(400, 35);
        Dimension labelSize = new Dimension(400, 25);

        JTextField idField = createStyledTextField(fieldSize);
        JTextField nameField = createStyledTextField(fieldSize);
        JTextField priceField = createStyledTextField(fieldSize);
        JTextField stockField = createStyledTextField(fieldSize);

        // Description Panel
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
        descriptionPanel.setBackground(BACKGROUND_COLOR);
        descriptionPanel.setMaximumSize(new Dimension(400, 200));
        descriptionPanel.setPreferredSize(new Dimension(400, 200));
        descriptionPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setMaximumSize(labelSize);

        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 150));
        descScrollPane.setMaximumSize(new Dimension(400, 150));
        descScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // If editing, load product data
        if (productId != null) {
            loadProductData(productId, idField, nameField, priceField, stockField, descriptionArea);
            idField.setEditable(false);
            idField.setBackground(new Color(240, 240, 240));
        }

        // Add components with consistent spacing
        addStyledField(panel, "Product ID", idField, labelSize);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        addStyledField(panel, "Name", nameField, labelSize);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        addStyledField(panel, "Price", priceField, labelSize);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        addStyledField(panel, "Stock", stockField, labelSize);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        descriptionPanel.add(descLabel);
        descriptionPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        descriptionPanel.add(descScrollPane);
        panel.add(descriptionPanel);

        // Save Button
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        JButton saveButton = new JButton(productId == null ? "Add Product" : "Save Changes");
        styleButton(saveButton, PRIMARY_COLOR);
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.setMaximumSize(new Dimension(200, 40));

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (idField.getText().trim().isEmpty() ||
                        nameField.getText().trim().isEmpty() ||
                        priceField.getText().trim().isEmpty() ||
                        stockField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required!");
                    return;
                }

                double price;
                int stock;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    stock = Integer.parseInt(stockField.getText().trim());

                    if (price < 0 || stock < 0) {
                        JOptionPane.showMessageDialog(dialog, "Price and stock must be positive values!");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid price or stock value!");
                    return;
                }

                // Save to database
                try (Connection conn = getConnection()) {
                    String sql;
                    if (productId == null) {
                        sql = "INSERT INTO products (product_id, name, price, stock_quantity, description) VALUES (?, ?, ?, ?, ?)";
                    } else {
                        sql = "UPDATE products SET name = ?, price = ?, stock_quantity = ?, description = ? WHERE product_id = ?";
                    }

                    PreparedStatement stmt = conn.prepareStatement(sql);
                    if (productId == null) {
                        stmt.setString(1, idField.getText().trim());
                        stmt.setString(2, nameField.getText().trim());
                        stmt.setDouble(3, price);
                        stmt.setInt(4, stock);
                        stmt.setString(5, descriptionArea.getText().trim());
                    } else {
                        stmt.setString(1, nameField.getText().trim());
                        stmt.setDouble(2, price);
                        stmt.setInt(3, stock);
                        stmt.setString(4, descriptionArea.getText().trim());
                        stmt.setString(5, productId);
                    }

                    stmt.executeUpdate();
                    loadProductsForManagement(model);
                    dialog.dispose();
                    JOptionPane.showMessageDialog(dialog, "Product saved successfully!");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving product: " + ex.getMessage());
            }
        });

        panel.add(saveButton);
        dialog.add(new JScrollPane(panel));
        dialog.setVisible(true);
    }

    private void loadProductData(String productId, JTextField idField, JTextField nameField,
                                 JTextField priceField, JTextField stockField, JTextArea descriptionArea) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                idField.setText(rs.getString("product_id"));
                nameField.setText(rs.getString("name"));
                priceField.setText(String.valueOf(rs.getDouble("price")));
                stockField.setText(String.valueOf(rs.getInt("stock_quantity")));
                descriptionArea.setText(rs.getString("description"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading product: " + e.getMessage());
        }
    }

    private boolean saveProduct(String originalId, String id, String name, String price,
                                String stock, String description, String imagePath) {
        try {
            double priceValue = Double.parseDouble(price);
            int stockValue = Integer.parseInt(stock);

            if (priceValue < 0 || stockValue < 0) {
                throw new NumberFormatException();
            }

            Connection conn = getConnection();
            String sql;
            PreparedStatement stmt;

            if (originalId == null) {
                // Insert new product
                sql = "INSERT INTO products (product_id, name, price, stock_quantity, description, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
            } else {
                // Update existing product
                sql = "UPDATE products SET product_id = ?, name = ?, price = ?, stock_quantity = ?, description = ?, image_path = ? WHERE product_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(7, originalId);
            }

            stmt.setString(1, id);
            stmt.setString(2, name);
            stmt.setDouble(3, priceValue);
            stmt.setInt(4, stockValue);
            stmt.setString(5, description);
            stmt.setString(6, imagePath);

            stmt.executeUpdate();
            conn.close();

            JOptionPane.showMessageDialog(this,
                    originalId == null ? "Product added successfully!" : "Product updated successfully!");
            return true;

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price or stock value!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving product: " + e.getMessage());
        }
        return false;
    }

    private void deleteProduct(String productId, DefaultTableModel model, int row) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = getConnection()) {
                String sql = "DELETE FROM products WHERE product_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, productId);
                stmt.executeUpdate();

                model.removeRow(row);
                JOptionPane.showMessageDialog(this, "Product deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting product: " + e.getMessage());
            }
        }
    }
    private void viewOrders() {
        JDialog dialog = new JDialog(this, "View Orders", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Table
        String[] columns = {"Order ID", "User", "Total", "Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Add double-click listener to view order details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String orderId = table.getValueAt(row, 0).toString();
                        showOrderDetails(orderId);
                    }
                }
            }
        });

        // Load orders
        loadOrders(model);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void loadOrders(DefaultTableModel model) {
        model.setRowCount(0);
        try (Connection conn = getConnection()) {
            // Updated SQL to use clients_orders table and join with users table
            String sql = "SELECT co.*, u.username " +
                    "FROM clients_orders co " +
                    "JOIN users u ON co.user_id = u.id " +
                    "ORDER BY co.order_date DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getString("username"),
                        String.format("$%.2f", rs.getDouble("total_amount")),
                        dateFormat.format(rs.getTimestamp("order_date")),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading orders: " + e.getMessage());
        }
    }

    private void showOrderDetails(String orderId) {
        JDialog detailsDialog = new JDialog(this, "Order Details #" + orderId, true);
        detailsDialog.setSize(600, 400);
        detailsDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);

        String[] columns = {"Product", "Quantity", "Price", "Subtotal"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        try (Connection conn = getConnection()) {
            String sql = "SELECT p.name, oi.quantity, oi.price, (oi.quantity * oi.price) as subtotal " +
                    "FROM order_items oi " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE oi.order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderId);
            ResultSet rs = stmt.executeQuery();

            double total = 0;
            while (rs.next()) {
                double subtotal = rs.getDouble("subtotal");
                total += subtotal;
                model.addRow(new Object[]{
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        String.format("$%.2f", rs.getDouble("price")),
                        String.format("$%.2f", subtotal)
                });
            }

            // Add total row
            model.addRow(new Object[]{"", "", "Total:", String.format("$%.2f", total)});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading order details: " + e.getMessage());
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        detailsDialog.add(panel);
        detailsDialog.setVisible(true);
    }
    private void viewProfile() {
        JDialog dialog = new JDialog(this, "Employee Profile", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);

        try (Connection conn = getConnection()) {
            // Fetch employee details
            String sql = "SELECT * FROM employees WHERE employee_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Employee details
                addProfileField(mainPanel, "Employee ID", String.valueOf(rs.getInt("employee_id")));
                addProfileField(mainPanel, "Username", rs.getString("username"));
                addProfileField(mainPanel, "Full Name", rs.getString("full_name"));
                addProfileField(mainPanel, "Email", rs.getString("email"));
                addProfileField(mainPanel, "Phone", rs.getString("phone"));
                addProfileField(mainPanel, "Address", rs.getString("address"));
                addProfileField(mainPanel, "Role", rs.getString("role"));
                addProfileField(mainPanel, "Hire Date", rs.getTimestamp("hire_date").toString());

                // Add shift button panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
                buttonPanel.setBackground(BACKGROUND_COLOR);
                buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Check In/Out Button
                JButton shiftButton = createShiftButton();
                buttonPanel.add(shiftButton);

                mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
                mainPanel.add(buttonPanel);

                // Add shift history
                addShiftHistory(mainPanel);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e.getMessage());
        }

        dialog.add(new JScrollPane(mainPanel));
        dialog.setVisible(true);
    }
    private void addShiftHistory(JPanel panel) {
        // Add title for shift history
        JLabel historyLabel = new JLabel("Recent Shifts");
        historyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        historyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(historyLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Create table for shift history
        String[] columns = {"Date", "Check In", "Check Out", "Duration"};
        shiftModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        shiftTable = new JTable(shiftModel);
        JScrollPane scrollPane = new JScrollPane(shiftTable);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        scrollPane.setMaximumSize(new Dimension(450, 200));
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load initial shift history
        loadShiftHistory();

        // Start timer for active shifts
        startShiftTimer();

        panel.add(scrollPane);
    }

    private JButton createShiftButton() {
        try (Connection conn = getConnection()) {
            // Check if there's an open shift
            String checkSql = "SELECT * FROM employee_shifts WHERE employee_id = ? AND check_out IS NULL";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, employeeId);
            ResultSet rs = checkStmt.executeQuery();

            JButton shiftButton;
            if (rs.next()) {
                // Employee is checked in, create check-out button
                shiftButton = new JButton("Check Out");
                styleButton(shiftButton, new Color(220, 53, 69)); // Red color
                shiftButton.addActionListener(e -> handleCheckOut());
            } else {
                // Employee is checked out, create check-in button
                shiftButton = new JButton("Check In");
                styleButton(shiftButton, new Color(40, 167, 69)); // Green color
                shiftButton.addActionListener(e -> handleCheckIn());
            }
            return shiftButton;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error checking shift status: " + e.getMessage());
            return null;
        }
    }
    private void handleCheckIn() {
        try (Connection conn = getConnection()) {
            // Use server timestamp for consistency
            String sql = "INSERT INTO employee_shifts (employee_id, check_in) " +
                    "VALUES (?, CURRENT_TIMESTAMP(3))";  // Using millisecond precision
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            stmt.executeUpdate();

            System.out.println("Check-in recorded at: " + new Timestamp(System.currentTimeMillis()));
            JOptionPane.showMessageDialog(this, "Checked in successfully!");
            loadShiftHistory();
            startShiftTimer();
            refreshProfileView();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking in: " + e.getMessage());
        }
    }
    private void refreshProfileView() {
        // Find and dispose the current profile dialog
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog && window.isVisible()
                    && ((JDialog) window).getTitle().equals("Employee Profile")) {
                window.dispose();
                viewProfile();  // Open a new profile view
                break;
            }
        }
    }

    private void handleCheckOut() {
        try (Connection conn = getConnection()) {
            // First get the check-in time
            String getCheckInSql = "SELECT check_in FROM employee_shifts " +
                    "WHERE employee_id = ? AND check_out IS NULL";
            PreparedStatement checkInStmt = conn.prepareStatement(getCheckInSql);
            checkInStmt.setInt(1, employeeId);
            ResultSet rs = checkInStmt.executeQuery();

            if (rs.next()) {
                Timestamp checkInTime = rs.getTimestamp("check_in");
                Timestamp checkOutTime = new Timestamp(System.currentTimeMillis());

                // Update with explicit timestamps
                String updateSql = "UPDATE employee_shifts SET " +
                        "check_out = ?, " +
                        "shift_duration = ? " +
                        "WHERE employee_id = ? AND check_out IS NULL";

                // Calculate duration
                long diffMillis = checkOutTime.getTime() - checkInTime.getTime();
                long diffSeconds = diffMillis / 1000;
                String duration = String.format("%02d:%02d:%02d",
                        diffSeconds / 3600,           // hours
                        (diffSeconds % 3600) / 60,    // minutes
                        diffSeconds % 60              // seconds
                );

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setTimestamp(1, checkOutTime);
                updateStmt.setString(2, duration);
                updateStmt.setInt(3, employeeId);
                updateStmt.executeUpdate();

                System.out.println("Check-in time: " + checkInTime);
                System.out.println("Check-out time: " + checkOutTime);
                System.out.println("Duration: " + duration);
            }

            JOptionPane.showMessageDialog(this, "Checked out successfully!");

            if (activeShiftTimer != null) {
                activeShiftTimer.stop();
            }
            loadShiftHistory();
            refreshProfileView();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking out: " + e.getMessage());
        }
    }

    private void loadShiftHistory() {
        shiftModel.setRowCount(0);
        try (Connection conn = getConnection()) {
            String sql = "SELECT check_in, check_out, shift_duration " +
                    "FROM employee_shifts " +
                    "WHERE employee_id = ? " +
                    "ORDER BY check_in DESC LIMIT 10";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

            while (rs.next()) {
                Timestamp checkIn = rs.getTimestamp("check_in");
                Timestamp checkOut = rs.getTimestamp("check_out");
                String duration = rs.getString("shift_duration");

                shiftModel.addRow(new Object[]{
                        dateFormat.format(checkIn),
                        timeFormat.format(checkIn),
                        checkOut != null ? timeFormat.format(checkOut) : "Active",
                        duration != null ? duration : "In Progress"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading shift history: " + e.getMessage());
        }
    }
    private String formatDuration(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        System.out.println(String.format("Formatting duration: %d seconds = %dh %dm %ds",
                seconds, hours, minutes, remainingSeconds));

        return String.format("%02dh %02dm %02ds", hours, minutes, remainingSeconds);
    }

    private void startShiftTimer() {
        if (activeShiftTimer != null) {
            activeShiftTimer.stop();
        }

        activeShiftTimer = new Timer(1000, e -> {
            try (Connection conn = getConnection()) {
                String sql = "SELECT check_in, " +
                        "TIMESTAMPDIFF(SECOND, check_in, NOW()) as seconds_elapsed " +
                        "FROM employee_shifts " +
                        "WHERE employee_id = ? AND check_out IS NULL";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, employeeId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    long secondsElapsed = rs.getLong("seconds_elapsed");
                    String duration = formatDuration(secondsElapsed);

                    if (shiftModel.getRowCount() > 0 &&
                            shiftModel.getValueAt(0, 2).equals("Active")) {
                        shiftModel.setValueAt("In Progress (" + duration + ")", 0, 3);
                        System.out.println("Timer update: " + duration);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                activeShiftTimer.stop();
            }
        });
        activeShiftTimer.start();
    }
    private void addProfileField(JPanel panel, String label, String value) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(BACKGROUND_COLOR);
        fieldPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 14));
        labelComp.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("Arial", Font.PLAIN, 14));
        valueComp.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(labelComp);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        fieldPanel.add(valueComp);
        fieldPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(fieldPanel);
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Change Password", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(BACKGROUND_COLOR);

        JPasswordField currentPass = new JPasswordField(20);
        JPasswordField newPass = new JPasswordField(20);
        JPasswordField confirmPass = new JPasswordField(20);

        addLabeledField(panel, "Current Password", currentPass);
        addLabeledField(panel, "New Password", newPass);
        addLabeledField(panel, "Confirm Password", confirmPass);

        JButton updateButton = new JButton("Update Password");
        styleButton(updateButton, PRIMARY_COLOR);
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        updateButton.addActionListener(e -> {
            if (updateEmployeePassword(
                    new String(currentPass.getPassword()),
                    new String(newPass.getPassword()),
                    new String(confirmPass.getPassword()))) {
                dialog.dispose();
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(updateButton);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addLabeledField(JPanel panel, String label, JComponent field) {
        JLabel labelComp = new JLabel(label);
        labelComp.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setMaximumSize(new Dimension(200, 25));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(labelComp);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private boolean updateEmployeePassword(String currentPass, String newPass, String confirmPass) {
        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "New passwords do not match!");
            return false;
        }

        try (Connection conn = getConnection()) {
            // Verify current password
            String checkSql = "SELECT * FROM employees WHERE employee_id = ? AND password = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, employeeId);
            checkStmt.setString(2, currentPass);

            if (!checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Current password is incorrect!");
                return false;
            }

            // Update password
            String updateSql = "UPDATE employees SET password = ? WHERE employee_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newPass);
            updateStmt.setInt(2, employeeId);
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Password updated successfully!");
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating password: " + e.getMessage());
            return false;
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFormGUI();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/electronics_shop";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }
}