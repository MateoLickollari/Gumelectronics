import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


public class OnlineShop extends JFrame {
    // Constants
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color HOVER_COLOR = new Color(51, 101, 138);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(51, 51, 51);
    private static final Color TEXT_SECONDARY = new Color(102, 102, 102);
    private static final Color SUCCESS_COLOR = new Color(46, 125, 50);
    private static final Color WARNING_COLOR = new Color(198, 40, 40);
    private static final Color PAYMENT_COLOR = new Color(46, 125, 50);  // Green color
    private static final Color PAYMENT_HOVER_COLOR = new Color(39, 105, 43);  // Darker green

    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/electronics_shop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Instance variables
    private int userId;
    private String username;
    private JPanel mainPanel;
    private JPanel productsPanel;
    private JPanel cartPanel;
    private ArrayList<CartItem> cartItems;
    private double total;
    private Map<String, Integer> productQuantities;


    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CARD_BACKGROUND);
        topPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Left side - Welcome message
        JPanel welcomePanel = createWelcomePanel();
        topPanel.add(welcomePanel, BorderLayout.WEST);

        // Right side - Search
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);

        // Welcome label
        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        // User icon (you can add this if you have an icon)
         ImageIcon userIcon = new ImageIcon(getClass().getResource("/icons/user.png"));
         welcomeLabel.setIcon(userIcon);

        welcomePanel.add(welcomeLabel);
        return welcomePanel;
    }
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);

        // Create a compound panel for search field and icon
        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchFieldPanel.setBackground(Color.WHITE);
        searchFieldPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        // Search field with placeholder
        JTextField searchField = createStyledSearchField();
        searchFieldPanel.add(searchField);

        // Search button
        JButton searchButton = createStyledButton("Search");
        searchButton.setPreferredSize(new Dimension(100, 135));

        // Add search functionality
        searchButton.addActionListener(e -> performSearch(searchField.getText()));

        // Add enter key listener to search field
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch(searchField.getText());
                }
            }
        });

        searchPanel.add(searchFieldPanel);
        searchPanel.add(searchButton);
        return searchPanel;
    }

    private JTextField createStyledSearchField() {
        JTextField searchField = new JTextField(25);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Style the search field
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBackground(Color.WHITE);

        // Add placeholder text
        searchField.setForeground(Color.GRAY);
        searchField.setText("Search products...");

        // Handle placeholder text
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search products...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setForeground(Color.GRAY);
                    searchField.setText("Search products...");
                }
            }
        });

        return searchField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void performSearch(String searchTerm) {
        searchTerm = searchTerm.trim();
        if (searchTerm.equals("Search products...") || searchTerm.isEmpty()) {
            loadProducts(); // Load all products if search is empty
            return;
        }

        productsPanel.removeAll();
        String sql = "SELECT * FROM products WHERE name LIKE ? AND stock_quantity > 0";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                createProductCard(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_path")
                );
            }

            if (!found) {
                showNoResultsMessage();
            }

            productsPanel.revalidate();
            productsPanel.repaint();

        } catch (SQLException e) {
            showError("Error searching products: " + e.getMessage());
        }
    }


    private void createProductsPanel() {
        // Initialize products panel with grid layout
        productsPanel = new JPanel(new GridLayout(0, 2, 20, 20));  // 3 columns, dynamic rows, 20px gaps
        productsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        productsPanel.setBackground(BACKGROUND_COLOR);

        // Create scroll pane for products
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        styleScrollPane(scrollPane);

        // Add to main panel
        mainPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = PRIMARY_COLOR;
                this.trackColor = BACKGROUND_COLOR;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    private void createProductCard(String id, String name, double price,
                                       String description, int stockQuantity, String imagePath) {
        // Create card panel
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(CARD_BACKGROUND);
        card.setPreferredSize(new Dimension(300, 450));
        card.setMaximumSize(new Dimension(300, 450));

        // Product image
        addProductImage(card, id, imagePath);

        // Product name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f", price));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        priceLabel.setForeground(PRIMARY_COLOR);
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Description
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descArea.setForeground(TEXT_SECONDARY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setBackground(CARD_BACKGROUND);
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descArea.setMaximumSize(new Dimension(250, 60));
        card.add(descArea);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Stock status
        JLabel stockLabel = new JLabel("In Stock: " + stockQuantity);
        stockLabel.setFont(new Font("Arial", Font.BOLD, 12));
        stockLabel.setForeground(stockQuantity > 5 ? SUCCESS_COLOR : WARNING_COLOR);
        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(stockLabel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Quantity selector
        JPanel quantityPanel = createQuantitySelector(stockQuantity);
        card.add(quantityPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));

        // Add to cart button
        JButton addToCartButton = createAddToCartButton(id, name, price, stockQuantity, imagePath);
        card.add(addToCartButton);

        // Add card to products panel
        productsPanel.add(card);
    }

    private void addProductImage(JPanel card, String id, String imagePath) {
        try {
            // Use default images based on product ID
            String imageUrl = switch (id) {
                case "P001" -> "https://images.unsplash.com/photo-1531297484001-80022131f5a1?w=800"; // Laptop
                case "P002" -> "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800"; // Laptop 2
                case "P003" -> "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800"; // Tablet
                case "P004" -> "https://images.unsplash.com/photo-1593640495253-23196b27a87f?w=800"; // Desktop PC
                case "P005" -> "https://images.unsplash.com/photo-1527864550417-7fd91fc51a46?w=800"; // Mouse
                case "P006" -> "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800"; // Keyboard
                case "P007" -> "https://images.unsplash.com/photo-1586210579191-33b45e38fa2c?w=800"; // Monitor
                case "P008" -> "https://images.unsplash.com/photo-1625842268584-8f3296236761?w=800"; // Docking Station
                case "P009" -> "https://images.unsplash.com/photo-1600490722773-35753aea6332?w=800"; // External SSD
                case "P010" -> "https://images.unsplash.com/photo-1618366712010-f4ae9c647dcb?w=800";// Headphones
                case "P012" -> "https://images.unsplash.com/photo-1592899677977-9c10ca588bbd?w=800";
                default -> "https://images.unsplash.com/photo-1601944177325-f8867652837f?w=800"; // Default electronics
            };

            ImageIcon imageIcon = new ImageIcon(new URL(imageUrl));

            // Scale image
            Image image = imageIcon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(imageLabel);
            card.add(Box.createRigidArea(new Dimension(0, 15)));

        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Image not available");
            errorLabel.setForeground(TEXT_SECONDARY);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(errorLabel);
            card.add(Box.createRigidArea(new Dimension(0, 15)));
        }
    }
    private JPanel createQuantitySelector(int maxQuantity) {
        JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        quantityPanel.setOpaque(false);

        JLabel qtyLabel = new JLabel("Quantity: ");
        qtyLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, maxQuantity, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(60, 25));

        quantityPanel.add(qtyLabel);
        quantityPanel.add(quantitySpinner);

        return quantityPanel;
    }

    private JButton createAddToCartButton(String id, String name, double price,
                                          int stockQuantity, String imagePath) {
        JButton addToCartButton = new JButton("Add to Cart");
        addToCartButton.setFont(new Font("Arial", Font.BOLD, 14));
        addToCartButton.setForeground(Color.BLUE);
        addToCartButton.setBackground(PRIMARY_COLOR);
        addToCartButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        addToCartButton.setFocusPainted(false);
        addToCartButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addToCartButton.setMaximumSize(new Dimension(150, 35));

        // Add hover effect
        addToCartButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                addToCartButton.setBackground(HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addToCartButton.setBackground(PRIMARY_COLOR);
            }
        });

        // Add action listener
        addToCartButton.addActionListener(e -> {
            // Find the quantity spinner by traversing the component hierarchy
            Container parent = addToCartButton.getParent();
            JSpinner spinner = null;

            // Look for the quantity panel which contains the spinner
            for (Component comp : parent.getComponents()) {
                if (comp instanceof JPanel && ((JPanel) comp).getLayout() instanceof FlowLayout) {
                    // This should be our quantity panel
                    for (Component innerComp : ((JPanel) comp).getComponents()) {
                        if (innerComp instanceof JSpinner) {
                            spinner = (JSpinner) innerComp;
                            break;
                        }
                    }
                    if (spinner != null) break;
                }
            }

            if (spinner != null) {
                int quantity = (Integer) spinner.getValue();
                if (quantity > 0 && quantity <= stockQuantity) {
                    addToCart(id, name, price, quantity, imagePath);
                } else {
                    showError("Invalid quantity selected");
                }
            } else {
                showError("Could not find quantity selector");
            }
        });

        return addToCartButton;
    }

    private void createCartPanel() {
        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setPreferredSize(new Dimension(300, getHeight()));
        cartPanel.setBackground(CARD_BACKGROUND);
        cartPanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Cart title
        JLabel cartTitle = new JLabel("Shopping Cart");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 18));
        cartTitle.setForeground(TEXT_PRIMARY);
        cartTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(cartTitle);
        cartPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Create empty cart message
        JLabel emptyCartLabel = new JLabel("Your cart is empty");
        emptyCartLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emptyCartLabel.setForeground(TEXT_SECONDARY);
        emptyCartLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cartPanel.add(emptyCartLabel);

        // Add to main panel
        mainPanel.add(cartPanel, BorderLayout.EAST);
    }

    private void addToCart(String productId, String name, double price, int quantity, String imagePath) {
        // Check if item already exists in cart
        boolean itemExists = false;
        for (CartItem item : cartItems) {
            if (item.productId.equals(productId)) {
                item.quantity += quantity;
                itemExists = true;
                break;
            }
        }

        // If item doesn't exist, add new item
        if (!itemExists) {
            cartItems.add(new CartItem(productId, name, price, quantity, imagePath));
        }

        // Update cart display
        updateCart();

        // Show success message
        showAddToCartMessage();
    }

    private void showAddToCartMessage() {
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(SUCCESS_COLOR);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel messageLabel = new JLabel("Added to cart!");
        messageLabel.setForeground(Color.WHITE);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        messagePanel.add(messageLabel);

        JDialog dialog = new JDialog(this);
        dialog.setUndecorated(true);
        dialog.add(messagePanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);

        // Show and fade out
        Timer timer = new Timer(1500, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
        dialog.setVisible(true);
    }


    private Connection getConnection() throws SQLException {
        try {
            // Register JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create connection
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Database connected successfully");
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }

    private void loadProducts() {
        productsPanel.removeAll();

        String sql = "SELECT * FROM products WHERE stock_quantity > 0 ORDER BY name";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                createProductCard(
                        rs.getString("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_path")
                );
            }

            productsPanel.revalidate();
            productsPanel.repaint();

        } catch (SQLException e) {
            showError("Error loading products: " + e.getMessage());
        }
    }

    private void updateProductStock(String productId, int quantity) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantity);
            pstmt.setString(2, productId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            showError("Error updating stock: " + e.getMessage());
        }
    }

    private void processPayment() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Your cart is empty!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Create credit card information dialog
        JDialog paymentDialog = new JDialog(this, "Payment Information", true);
        paymentDialog.setSize(400, 450);
        paymentDialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(CARD_BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Add total amount display
        JLabel totalLabel = new JLabel(String.format("Total Amount: $%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(totalLabel, gbc);

        // Card holder name
        gbc.gridy++;
        panel.add(new JLabel("Card Holder Name:"), gbc);
        gbc.gridy++;
        JTextField nameField = new JTextField(20);
        panel.add(nameField, gbc);

        // Card number
        gbc.gridy++;
        panel.add(new JLabel("Card Number:"), gbc);
        gbc.gridy++;
        JTextField cardNumberField = new JTextField(20);
        panel.add(cardNumberField, gbc);

               // Expiry date (Month and Year)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Expiry Date:"), gbc);

        // Create a panel for expiry date components
        JPanel expiryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        expiryPanel.setBackground(CARD_BACKGROUND);

        // Day combo box
        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i-1] = String.format("%02d", i);  // Pad with zero for single digits
        }
        JComboBox<String> dayCombo = new JComboBox<>(days);
        dayCombo.setPreferredSize(new Dimension(70, 25));
        expiryPanel.add(dayCombo);

        // First separator
        JLabel separator1 = new JLabel(" / ");
        separator1.setFont(new Font("Arial", Font.BOLD, 18));
        expiryPanel.add(separator1);

        // Month combo box
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        JComboBox<String> monthCombo = new JComboBox<>(months);
        monthCombo.setPreferredSize(new Dimension(70, 25));
        expiryPanel.add(monthCombo);

        // Second separator
        JLabel separator2 = new JLabel(" / ");
        separator2.setFont(new Font("Arial", Font.BOLD, 18));
        expiryPanel.add(separator2);

        // Year combo box
        String[] years = {"2024", "2025", "2026", "2027", "2028", "2029", "2030"};
        JComboBox<String> yearCombo = new JComboBox<>(years);
        yearCombo.setPreferredSize(new Dimension(95, 25));
        expiryPanel.add(yearCombo);

        // Add the expiry panel to the main panel
        gbc.gridy++;
        panel.add(expiryPanel, gbc);

        // CVV
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel("CVV:"), gbc);
        gbc.gridy++;
        JPasswordField cvvField = new JPasswordField(3);
        cvvField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                if ((getLength() + str.length()) <= 3 && str.matches("\\d+")) {
                    super.insertString(offs, str, a);
                }
            }
        });
        panel.add(cvvField, gbc);

        // Pay button
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        JButton payButton = new JButton("Process Payment");
        payButton.setBackground(PAYMENT_COLOR);
        payButton.setForeground(Color.blue);
        payButton.setFocusPainted(false);
        payButton.addActionListener(e -> {
            // Validate input fields
            if (nameField.getText().trim().isEmpty() ||
                    cardNumberField.getText().trim().isEmpty() ||
                    new String(cvvField.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(paymentDialog,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate card number (simple check for 16 digits)
            if (!cardNumberField.getText().trim().matches("\\d{16}")) {
                JOptionPane.showMessageDialog(paymentDialog,
                        "Please enter a valid 16-digit card number",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate CVV (3 digits)
            if (!new String(cvvField.getPassword()).matches("\\d{3}")) {
                JOptionPane.showMessageDialog(paymentDialog,
                        "Please enter a valid 3-digit CVV",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If validation passes, process the payment
            paymentDialog.dispose();
            if (saveOrder()) {
                JOptionPane.showMessageDialog(this,
                        "Payment successful! Thank you for your purchase.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                cartItems.clear();
                updateCart();
                loadProducts();
            }
        });
        panel.add(payButton, gbc);

        paymentDialog.add(panel);
        paymentDialog.setVisible(true);
    }

    private boolean saveOrder() {
        if (cartItems.isEmpty()) return false;

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // Start transaction

            try {
                // First check if we have enough stock for all items
                for (CartItem item : cartItems) {
                    String checkStockSql = "SELECT stock_quantity FROM products WHERE product_id = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkStockSql);
                    checkStmt.setString(1, item.productId);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        int currentStock = rs.getInt("stock_quantity");
                        if (currentStock < item.quantity) {
                            throw new SQLException("Not enough stock for product: " + item.name +
                                    " (Available: " + currentStock +
                                    ", Requested: " + item.quantity + ")");
                        }
                    }
                }

                // Insert order into clients_orders table
                String orderSql = "INSERT INTO clients_orders (user_id, total_amount, order_date, status) VALUES (?, ?, NOW(), 'Completed')";
                PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
                orderStmt.setInt(1, userId);
                orderStmt.setDouble(2, total);

                int orderResult = orderStmt.executeUpdate();
                if (orderResult == 0) {
                    throw new SQLException("Creating order failed, no rows affected.");
                }

                // Get generated order ID
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (!rs.next()) {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
                int orderId = rs.getInt(1);

                // Insert payment info
                String paymentSql = "INSERT INTO payment_info (order_id, amount, payment_date, payment_status) VALUES (?, ?, NOW(), 'Completed')";
                PreparedStatement paymentStmt = conn.prepareStatement(paymentSql);
                paymentStmt.setInt(1, orderId);
                paymentStmt.setDouble(2, total);
                paymentStmt.executeUpdate();

                // Insert order items and update stock
                for (CartItem item : cartItems) {
                    // Insert order item
                    String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                    PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                    itemStmt.setInt(1, orderId);
                    itemStmt.setString(2, item.productId);
                    itemStmt.setInt(3, item.quantity);
                    itemStmt.setDouble(4, item.price);
                    itemStmt.executeUpdate();

                    // Update product stock
                    String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
                    PreparedStatement stockStmt = conn.prepareStatement(updateStockSql);
                    stockStmt.setInt(1, item.quantity);
                    stockStmt.setString(2, item.productId);
                    int updatedRows = stockStmt.executeUpdate();

                    if (updatedRows == 0) {
                        throw new SQLException("Failed to update stock for product: " + item.productId);
                    }

                    // Log the stock update for verification
                    System.out.println("Stock updated for " + item.name +
                            " (ID: " + item.productId +
                            ") - Reduced by: " + item.quantity);
                }

                // If everything is OK, commit the transaction
                conn.commit();

                // Refresh the products display immediately
                SwingUtilities.invokeLater(() -> {
                    productsPanel.removeAll();
                    loadProducts();
                    productsPanel.revalidate();
                    productsPanel.repaint();
                });

                // Show success message
                showSuccess("Order completed successfully! Thank you for your purchase.");

                return true;

            } catch (SQLException e) {
                // If there's an error, roll back the transaction
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                e.printStackTrace();
                showError("Error processing order: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database connection error: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addCartItemToPanel(CartItem item) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(CARD_BACKGROUND);
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Product name
        JLabel nameLabel = new JLabel(item.name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemPanel.add(nameLabel);

        // Price and quantity in one row
        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailsPanel.setBackground(CARD_BACKGROUND);

        // Price
        JLabel priceLabel = new JLabel(String.format("$%.2f x ", item.price));
        detailsPanel.add(priceLabel);

        // Quantity spinner
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(item.quantity, 1, 99, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        quantitySpinner.setPreferredSize(new Dimension(60, 25));
        quantitySpinner.addChangeListener(e -> {
            item.quantity = (int) quantitySpinner.getValue();
            updateCart();
        });
        detailsPanel.add(quantitySpinner);

        // Subtotal
        JLabel subtotalLabel = new JLabel(String.format(" = $%.2f", item.price * item.quantity));
        detailsPanel.add(subtotalLabel);

        // Remove button
        JButton removeButton = new JButton("Remove");
        removeButton.setBackground(WARNING_COLOR);
        removeButton.setForeground(Color.red);
        removeButton.setFocusPainted(false);
        removeButton.setBorderPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeButton.addActionListener(e -> {
            cartItems.remove(item);
            updateCart();
        });
        detailsPanel.add(Box.createHorizontalStrut(20));
        detailsPanel.add(removeButton);

        itemPanel.add(detailsPanel);
        cartPanel.add(itemPanel);
    }


    private void updateCart() {
        // Remove all components from cart panel
        cartPanel.removeAll();

        // Recalculate total
        total = 0.0;
        for (CartItem item : cartItems) {
            total += item.price * item.quantity;
            addCartItemToPanel(item);
        }

        // Add total and pay button at the bottom
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(CARD_BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Total label
        JLabel totalLabel = new JLabel(String.format("Total: $%.2f", total));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(totalLabel);

        // Add pay button if cart is not empty
        if (!cartItems.isEmpty()) {
            JButton payButton = new JButton("Pay Now");
            payButton.setBackground(PAYMENT_COLOR);
            payButton.setForeground(Color.blue);
            payButton.setFont(new Font("Arial", Font.BOLD, 14));
            payButton.setFocusPainted(false);
            payButton.setBorderPainted(false);
            payButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            payButton.addActionListener(e -> processPayment());

            // Add hover effect
            payButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    payButton.setBackground(PAYMENT_HOVER_COLOR);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    payButton.setBackground(PAYMENT_COLOR);
                }
            });

            bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            bottomPanel.add(payButton);
        }

        cartPanel.add(bottomPanel);

        // Refresh the panel
        cartPanel.revalidate();
        cartPanel.repaint();
    }
    private void setComponentsEnabled(boolean enabled) {
        // Disable/enable all interactive components during payment processing
        SwingUtilities.invokeLater(() -> {
            // Disable the main components
            productsPanel.setEnabled(enabled);
            cartPanel.setEnabled(enabled);

            // Disable all buttons and interactive components
            Component[] components = cartPanel.getComponents();
            for (Component component : components) {
                if (component instanceof JButton ||
                        component instanceof JSpinner ||
                        component instanceof JTextField) {
                    component.setEnabled(enabled);
                }
            }
        });
    }
    // Utility methods
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    message,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        });
    }

    private void showSuccess(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });
    }


    private void showNoResultsMessage() {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(BACKGROUND_COLOR);

        JLabel noResultsLabel = new JLabel("No products found");
        noResultsLabel.setFont(new Font("Arial", Font.BOLD, 16));
        noResultsLabel.setForeground(TEXT_SECONDARY);
        noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton resetButton = createStyledButton("Show All Products");
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> loadProducts());

        messagePanel.add(Box.createVerticalGlue());
        messagePanel.add(noResultsLabel);
        messagePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        messagePanel.add(resetButton);
        messagePanel.add(Box.createVerticalGlue());

        productsPanel.add(messagePanel);
    }

    // CartItem inner class
    private static class CartItem {
        String productId;
        String name;
        double price;
        int quantity;
        String imagePath;

        CartItem(String productId, String name, double price, int quantity, String imagePath) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.quantity = quantity;
            this.imagePath = imagePath;
        }
    }


    // Constructor
    public OnlineShop(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.cartItems = new ArrayList<>();
        this.total = 0.0;
        this.productQuantities = new HashMap<>();

        setupFrame();
        initializeComponents();
        loadProducts();
    }

    private void setupFrame() {
        setTitle("GemElectronics - Welcome " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setBackground(BACKGROUND_COLOR);

        // Initialize main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(BACKGROUND_COLOR);
        add(mainPanel);
    }

    private void initializeComponents() {
        createTopPanel();
        createProductsPanel();
        createCartPanel();
    }

}



