package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Transaction extends javax.swing.JPanel {
    
    private DataChangeListener dataChangeListener;
    
    public Transaction() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme();
        
        // Setup action listeners
        btnSearch.addActionListener(evt -> loadTransactionData(searchField.getText()));
        searchField.addActionListener(evt -> loadTransactionData(searchField.getText()));
        btnRefresh.addActionListener(evt -> {
            searchField.setText("");
            loadTransactionData();
        });
        
        btnViewReceipt.addActionListener(evt -> viewReceipt());
        btnProcessPayment.addActionListener(evt -> processPayment());
        btnAuthorize.addActionListener(evt -> showAuthorizationDialog());
        
        // Double click to view full details
        tblTransactions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showTransactionDetails();
                }
            }
        });
        
        // Load initial data
        loadTransactionData();
    }
    private JPanel createGradientSeparator() {
    JPanel separator = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(255, 102, 0, 200),
                getWidth(), 0, new Color(255, 102, 0, 50)
            );
            g2d.setPaint(gp);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawLine(10, getHeight()/2, getWidth()-10, getHeight()/2);
            
            // Add small diamond accent
            g2d.setColor(new Color(255, 102, 0));
            g2d.fillOval(getWidth()/2 - 3, getHeight()/2 - 3, 6, 6);
            
            g2d.dispose();
        }
    };
    separator.setOpaque(false);
    separator.setPreferredSize(new Dimension(10, 20));
    return separator;
}
    
    private Members getMembersPanel() {
    // Get the parent container (JFrame)
    Container parent = getParent();
    while (parent != null) {
        if (parent instanceof JFrame) {
            JFrame frame = (JFrame) parent;
            // Find the Members panel in the frame's content pane
            Component[] components = frame.getContentPane().getComponents();
            for (Component comp : components) {
                if (comp instanceof Members) {
                    return (Members) comp;
                }
                // Also check nested containers
                if (comp instanceof JPanel) {
                    Members found = findMembersInContainer((JPanel) comp);
                    if (found != null) return found;
                }
            }
        }
        parent = parent.getParent();
    }
    return null;
}

// Helper method to recursively find Members panel in containers
private Members findMembersInContainer(JPanel container) {
    for (Component comp : container.getComponents()) {
        if (comp instanceof Members) {
            return (Members) comp;
        }
        if (comp instanceof JPanel) {
            Members found = findMembersInContainer((JPanel) comp);
            if (found != null) return found;
        }
    }
    return null;
}

private JPanel createMetallicPanel(String title, Color accentColor) {
    JPanel panel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Metallic gradient
            GradientPaint gp = new GradientPaint(
                0, 0, new Color(60, 60, 70),
                0, getHeight(), new Color(30, 30, 40)
            );
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            
            // Border with glow
            g2d.setColor(accentColor);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
            
            g2d.dispose();
        }
    };
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Impact", Font.BOLD, 16));
    titleLabel.setForeground(accentColor);
    panel.add(titleLabel, BorderLayout.WEST);
    
    return panel;
}

private JPanel createInfoPanel(String title, Color accentColor) {
    JPanel panel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Semi-transparent background
            g2d.setColor(new Color(40, 40, 50, 100));
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            
            // Left accent line
            g2d.setColor(accentColor);
            g2d.setStroke(new BasicStroke(3.0f));
            g2d.drawLine(5, 5, 5, getHeight()-5);
            
            g2d.dispose();
        }
    };
    panel.setOpaque(false);
    panel.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    titleLabel.setForeground(accentColor);
    panel.add(titleLabel, BorderLayout.CENTER);
    
    return panel;
}

private void addReceiptRow(JPanel panel, GridBagConstraints gbc, String label, String value, 
                          Color labelColor, Color valueColor) {
    JLabel lblLabel = new JLabel(label);
    lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblLabel.setForeground(labelColor);
    
    JLabel lblValue = new JLabel("<html><b>" + (value != null && !value.isEmpty() ? value : "N/A") + "</b></html>");
    lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    lblValue.setForeground(valueColor);
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0.3;
    panel.add(lblLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 0.7;
    panel.add(lblValue, gbc);
}

private void addAmountRow(JPanel panel, GridBagConstraints gbc, String label, String value, 
                         Color labelColor, Color valueColor) {
    JLabel lblLabel = new JLabel(label);
    lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
    lblLabel.setForeground(labelColor);
    
    // Extract numeric value from amount string (remove ₱ and commas)
    String numericValue = value.replace("₱", "").replace(",", "").trim();
    String formattedAmount = "₱ " + numericValue;
    
    JLabel lblValue = new JLabel(formattedAmount);
    lblValue.setFont(new Font("Impact", Font.BOLD, 24));
    lblValue.setForeground(valueColor);
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.weightx = 0.3;
    panel.add(lblLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 0.7;
    panel.add(lblValue, gbc);
}

private void addDivider(JPanel panel, GridBagConstraints gbc) {
    JSeparator separator = new JSeparator();
    separator.setForeground(new Color(255, 102, 0, 100));
    separator.setBackground(new Color(255, 102, 0, 50));
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.insets = new Insets(10, 10, 10, 10);
    panel.add(separator, gbc);
    
    gbc.gridwidth = 1;
    gbc.insets = new Insets(8, 10, 8, 10);
}

private void addQRCodeSection(JPanel panel, GridBagConstraints gbc, int transactionId) {
    JPanel qrPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    qrPanel.setOpaque(false);
    
    // Create a simulated QR code (just for design)
    JPanel qrCode = new JPanel(new GridLayout(5, 5, 2, 2)) {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(255, 102, 0, 50));
            
            // Draw random squares to simulate QR code
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 5; j++) {
                    if ((i + j) % 3 == 0 || (i * j) % 4 == 0) {
                        g2d.fillRect(i * 12 + 2, j * 12 + 2, 10, 10);
                    }
                }
            }
            g2d.dispose();
        }
    };
    qrCode.setPreferredSize(new Dimension(70, 70));
    qrCode.setBackground(new Color(30, 30, 30));
    qrCode.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 100)));
    
    JLabel qrLabel = new JLabel("SCAN FOR DIGITAL COPY");
    qrLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
    qrLabel.setForeground(new Color(150, 150, 150));
    
    qrPanel.add(qrCode);
    qrPanel.add(qrLabel);
    
    gbc.gridx = 0;
    gbc.gridy++;
    gbc.gridwidth = 2;
    panel.add(qrPanel, gbc);
    gbc.gridwidth = 1;
}

private JButton createStyledButton(String text, Color bgColor) {
    JButton button = new JButton(text) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2d.setColor(bgColor.darker());
            } else if (getModel().isRollover()) {
                g2d.setColor(bgColor.brighter());
            } else {
                g2d.setColor(bgColor);
            }
            
            // Rounded button with gradient
            GradientPaint gp = new GradientPaint(
                0, 0, bgColor,
                0, getHeight(), bgColor.darker()
            );
            g2d.setPaint(gp);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Draw text
            g2d.setColor(Color.WHITE);
            g2d.setFont(getFont());
            FontMetrics fm = g2d.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2d.drawString(getText(), x, y);
            
            g2d.dispose();
        }
    };
    
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setPreferredSize(new Dimension(140, 40));
    
    return button;
}

private String formatDate(String dateStr) {
    if (dateStr == null || dateStr.isEmpty()) return "N/A";
    try {
        // Try to parse and format the date
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy - hh:mm a");
        Date date = (Date) inputFormat.parse(dateStr);
        return outputFormat.format(date);
    } catch (Exception e) {
        return dateStr;
    }
}

private void printReceipt(JPanel panel) {
    try {
        // Create a printable version
        Printable printable = (Graphics graphics, PageFormat pageFormat, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            // Scale to fit page if needed
            double scaleX = pageFormat.getImageableWidth() / panel.getWidth();
            double scaleY = pageFormat.getImageableHeight() / panel.getHeight();
            double scale = Math.min(scaleX, scaleY);
            
            if (scale < 1.0) {
                g2d.scale(scale, scale);
            }
            
            panel.paint(g2d);
            return Printable.PAGE_EXISTS;
        };
        
        // Show print dialog
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(printable);
        
        if (job.printDialog()) {
            job.print();
            JOptionPane.showMessageDialog(this, "Receipt sent to printer!");
        }
        
    } catch (PrinterException e) {
        JOptionPane.showMessageDialog(this, "Print error: " + e.getMessage());
    }
}

// Add this missing method if you don't have it
/**
 * Save receipt as high-quality PNG image
 * @param panel The panel to save
 * @param transactionId The transaction ID for filename
 */
/**
 * Save receipt as high-quality PNG image
 * @param panel The panel to save
 * @param transactionId The transaction ID for filename
 */
    public interface DataChangeListener {
    void onDataChanged();
}
    
    public void setDataChangeListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }
    
    private void applyDashboardTheme() {
        // Style the scroll pane
        scrollTransactions.setBorder(new EmptyBorder(0, 0, 0, 0));
        scrollTransactions.setOpaque(false);
        scrollTransactions.getViewport().setOpaque(false);
        
        // Style the table
        tblTransactions.setRowHeight(35);
        tblTransactions.setShowGrid(false);
        tblTransactions.setIntercellSpacing(new Dimension(0, 0));
        tblTransactions.setBackground(new Color(30, 30, 30));
        tblTransactions.setForeground(new Color(220, 220, 220));
        tblTransactions.setSelectionBackground(new Color(255, 102, 0, 100));
        tblTransactions.setSelectionForeground(Color.WHITE);
        tblTransactions.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Style the table header
        tblTransactions.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblTransactions.getTableHeader().setBackground(new Color(25, 25, 25));
        tblTransactions.getTableHeader().setForeground(new Color(255, 102, 0));
        tblTransactions.getTableHeader().setReorderingAllowed(false);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(true);
        centerRenderer.setBackground(new Color(30, 30, 30));
        centerRenderer.setForeground(new Color(220, 220, 220));
        tblTransactions.setDefaultRenderer(Object.class, centerRenderer);
        
        // Style buttons
        styleButtons();
    }
    
    private void styleButtons() {
        Color buttonBg = new Color(255, 102, 0);
        Color buttonFg = Color.WHITE;
        
        JButton[] buttons = {btnRefresh, btnSearch, btnViewReceipt, btnProcessPayment, btnAuthorize};
        
        for (JButton btn : buttons) {
            if (btn != null) {
                btn.setBackground(buttonBg);
                btn.setForeground(buttonFg);
                btn.setFocusPainted(false);
                btn.setBorderPainted(false);
                btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
        
        // Style search field
        searchField.setBackground(new Color(45, 45, 45));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 102, 0)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        
    }
    
    public void loadTransactionData() {
        loadTransactionData(null);
    }
    
public void loadTransactionData(String searchTerm) {
    // 6 columns (Type column removed)
    String[] columnNames = {
        "Transaction ID", "Amount", "Date", 
        "Member", "Payment ID", "Description"
    };
    
    DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };
    
    String sql = "SELECT t.*, " +
                 "m.Name as MemberName " +
                 "FROM Transactions t " +
                 "LEFT JOIN Members m ON t.Member_ID = m.M_ID ";
    
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        sql += " WHERE t.Transaction_Type LIKE ? OR " +
               "t.Description LIKE ? OR " +
               "m.Name LIKE ?";
    }
    
    sql += " ORDER BY t.Transaction_Date DESC";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql)) {
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            String pattern = "%" + searchTerm + "%";
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            pst.setString(3, pattern);
        }
        
        try (ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String amount = "₱" + String.format("%,.2f", rs.getDouble("Amount"));
                String memberInfo = rs.getInt("Member_ID") > 0 ? 
                    rs.getInt("Member_ID") + " (" + rs.getString("MemberName") + ")" : "-";
                
                // Add exactly 6 values to match column count (Type column removed)
                model.addRow(new Object[]{
                    rs.getInt("Transaction_ID"),      // col 0 - Integer
                    amount,                           // col 1 - String (Amount)
                    rs.getString("Transaction_Date"), // col 2 - String
                    memberInfo,                       // col 3 - String
                    rs.getInt("Payment_ID") > 0 ? rs.getInt("Payment_ID") : "-", // col 4 - Integer or String
                    rs.getString("Description")       // col 5 - String
                });
            }
        }
        
        tblTransactions.setModel(model);
        
        // Set column widths (updated indices)
        tblTransactions.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblTransactions.getColumnModel().getColumn(1).setPreferredWidth(100);
        tblTransactions.getColumnModel().getColumn(2).setPreferredWidth(150);
        tblTransactions.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblTransactions.getColumnModel().getColumn(4).setPreferredWidth(80);
        tblTransactions.getColumnModel().getColumn(5).setPreferredWidth(200);
        
        // Update summary labels
        updateSummary();
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading transactions: " + e.getMessage());
    }
}
private void updateSummary() {
        String sql = "SELECT " +
                     "COUNT(*) as TotalCount, " +
                     "SUM(Amount) as TotalAmount, " +
                     "AVG(Amount) as AvgAmount, " +
                     "MAX(Amount) as MaxAmount " +
                     "FROM Transactions";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            if (rs.next()) {
                lblTotalCount.setText(String.valueOf(rs.getInt("TotalCount")));
                lblTotalAmount.setText("₱" + String.format("%,.2f", rs.getDouble("TotalAmount")));
                lblAverageAmount.setText("₱" + String.format("%,.2f", rs.getDouble("AvgAmount")));
                lblMaxAmount.setText("₱" + String.format("%,.2f", rs.getDouble("MaxAmount")));
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating summary: " + e.getMessage());
        }
    }
        
private void viewReceipt() {
    int selectedRow = tblTransactions.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, "Please select a transaction to view receipt");
        return;
    }
    
    try {
        // SAFELY get values with proper type checking (Type column removed - indices shifted)
        int transactionId = 0;
        Object idObj = tblTransactions.getValueAt(selectedRow, 0);
        if (idObj instanceof Integer) {
            transactionId = (Integer) idObj;
        } else if (idObj instanceof String) {
            try {
                transactionId = Integer.parseInt((String) idObj);
            } catch (NumberFormatException e) {
                transactionId = 0;
            }
        }
        
        // Column 1 is now Amount (was Type before)
        String amount = "";
        Object amountObj = tblTransactions.getValueAt(selectedRow, 1);
        if (amountObj != null) {
            amount = amountObj.toString();
        }
        
        // Column 2 is now Date
        String date = "";
        Object dateObj = tblTransactions.getValueAt(selectedRow, 2);
        if (dateObj != null) {
            date = dateObj.toString();
        }
        
        // Column 3 is now Member
        String memberInfo = "";
        Object memberObj = tblTransactions.getValueAt(selectedRow, 3);
        if (memberObj != null) {
            memberInfo = memberObj.toString();
        }
        
        // Column 4 is now Payment ID
        String paymentId = "";
        Object paymentObj = tblTransactions.getValueAt(selectedRow, 4);
        if (paymentObj != null) {
            paymentId = paymentObj.toString();
        }
        
        // Column 5 is now Description
        String description = "";
        Object descObj = tblTransactions.getValueAt(selectedRow, 5);
        if (descObj != null) {
            description = descObj.toString();
        }
        
        // CHECK IF TRANSACTION IS PAID - Only show receipt for paid transactions
        boolean isPaid = false;
        
        // Query the database to check payment status
        if (!paymentId.equals("-") && !paymentId.isEmpty()) {
            String checkSql = "SELECT Payment_Status FROM Payments WHERE Payment_ID = ?";
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(checkSql)) {
                
                // Parse payment ID safely
                int pId = -1;
                try {
                    pId = Integer.parseInt(paymentId.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException e) {
                    // Ignore parsing error
                }
                
                if (pId > 0) {
                    pst.setInt(1, pId);
                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        String status = rs.getString("Payment_Status");
                        isPaid = "Paid".equalsIgnoreCase(status);
                    }
                }
            } catch (SQLException e) {
                // Just log and continue
                System.err.println("Error checking payment status: " + e.getMessage());
            }
        }
        
        // If still not determined, check description for paid indicators
        if (!isPaid && description != null) {
            isPaid = description.contains("Paid") || 
                     description.contains("paid") || 
                     description.contains("PAID") ||
                     description.contains("authorized") ||
                     description.contains("Authorized");
        }
        
        // Block receipt generation for non-paid transactions
        if (!isPaid) {
            int choice = JOptionPane.showConfirmDialog(this,
                "<html><body style='width: 300px; text-align: center;'>" +
                "<b style='color: #FF6600;'>⚠️ PENDING PAYMENT</b><br><br>" +
                "This transaction has a <b>PENDING</b> status.<br>" +
                "Receipts can only be generated for <b>PAID</b> transactions.<br><br>" +
                "Would you like to view the pending payment details instead?",
                "Cannot Generate Receipt",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (choice == JOptionPane.YES_OPTION) {
                showPendingPaymentDetails(transactionId, memberInfo, amount, date, description);
            }
            return;
        }
        
        // Create dialog with 500x600 size
        JDialog receiptDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "OMNIFIT GYM RECEIPT", true);
        receiptDialog.setSize(500, 600);
        receiptDialog.setLocationRelativeTo(this);
        receiptDialog.setUndecorated(true);
        receiptDialog.setBackground(new Color(0, 0, 0, 0));
        
        // MAIN CONTENT PANEL - This will hold all receipt content
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Create metallic gradient background
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(25, 25, 35),
                    0, getHeight(), new Color(15, 15, 25)
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle hexagonal pattern for gym theme
                g2d.setColor(new Color(255, 102, 0, 15));
                g2d.setStroke(new BasicStroke(0.8f));
                
                // Draw smaller hexagon pattern
                int hexSize = 20;
                for (int x = 0; x < getWidth(); x += hexSize * 1.8) {
                    for (int y = 0; y < getHeight(); y += hexSize * 1.8) {
                        drawHexagon(g2d, x, y, hexSize);
                    }
                }
                
                g2d.dispose();
            }
            
            private void drawHexagon(Graphics2D g2d, int x, int y, int size) {
                int[] xPoints = new int[6];
                int[] yPoints = new int[6];
                for (int i = 0; i < 6; i++) {
                    double angle = 2 * Math.PI / 6 * i;
                    xPoints[i] = x + (int) (size * Math.cos(angle));
                    yPoints[i] = y + (int) (size * Math.sin(angle));
                }
                g2d.drawPolygon(xPoints, yPoints, 6);
            }
        };
        contentPanel.setLayout(new BorderLayout(10, 10));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add "PAID" watermark/stamp
        JLabel paidStamp = new JLabel("PAID");
        paidStamp.setFont(new Font("Impact", Font.BOLD, 48));
        paidStamp.setForeground(new Color(0, 255, 0, 40));
        paidStamp.setHorizontalAlignment(SwingConstants.CENTER);
        paidStamp.setVerticalAlignment(SwingConstants.CENTER);
        contentPanel.add(paidStamp, BorderLayout.CENTER);
        
        // TOP SECTION - Gym Header
        JPanel topPanel = new JPanel(new BorderLayout(5, 3));
        topPanel.setOpaque(false);
        
        // Gym logo area
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        logoPanel.setOpaque(false);
        
        JLabel lblGymIcon = new JLabel("🏋️‍♂️");
        lblGymIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblGymIcon.setForeground(new Color(255, 102, 0));
        
        JLabel lblGymName = new JLabel("OMNIFIT");
        lblGymName.setFont(new Font("Impact", Font.BOLD, 24));
        lblGymName.setForeground(new Color(255, 102, 0));
        
        JLabel lblGymFull = new JLabel("GYM CENTER");
        lblGymFull.setFont(new Font("Impact", Font.BOLD, 16));
        lblGymFull.setForeground(new Color(255, 102, 0));
        
        JLabel lblTagline = new JLabel("FITNESS DESTINATION");
        lblTagline.setFont(new Font("Segoe UI", Font.BOLD, 9));
        lblTagline.setForeground(new Color(180, 180, 180));
        
        logoPanel.add(lblGymIcon);
        
        JPanel namePanel = new JPanel(new GridLayout(3, 1, 0, 0));
        namePanel.setOpaque(false);
        namePanel.add(lblGymName);
        namePanel.add(lblGymFull);
        namePanel.add(lblTagline);
        logoPanel.add(namePanel);
        
        topPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Decorative separator
        JPanel separatorTop = createGradientSeparator();
        separatorTop.setPreferredSize(new Dimension(10, 12));
        topPanel.add(separatorTop, BorderLayout.SOUTH);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        
        // CENTER SECTION - Receipt details
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.weightx = 1.0;
        
        // Receipt header
        JPanel receiptHeaderPanel = createMetallicPanel("OFFICIAL RECEIPT", new Color(255, 102, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(receiptHeaderPanel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        // Receipt Number
        addReceiptRow(centerPanel, gbc, "RECEIPT #:", "#" + String.format("%06d", transactionId), 
                     new Color(255, 102, 0), new Color(255, 255, 255));
        
        // Transaction Date
        addReceiptRow(centerPanel, gbc, "DATE:", formatDate(date), 
                     new Color(255, 102, 0), new Color(200, 200, 200));
        
        // Add a subtle divider
        addDivider(centerPanel, gbc);
        
        // Member Information Section
        JPanel memberPanel = createInfoPanel("MEMBER", new Color(0, 200, 255));
        gbc.gridwidth = 2;
        gbc.gridy++;
        centerPanel.add(memberPanel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        addReceiptRow(centerPanel, gbc, "NAME:", memberInfo, 
                     new Color(0, 200, 255), Color.WHITE);
        
        addDivider(centerPanel, gbc);
        
        // Payment Details Section
        JPanel paymentPanel = createInfoPanel("PAYMENT", new Color(0, 255, 150));
        gbc.gridwidth = 2;
        gbc.gridy++;
        centerPanel.add(paymentPanel, gbc);
        gbc.gridwidth = 1;
        gbc.gridy++;
        
        addReceiptRow(centerPanel, gbc, "PAYMENT ID:", paymentId, 
                     new Color(0, 255, 150), Color.WHITE);
        
        // Amount with special styling
        addAmountRow(centerPanel, gbc, "AMOUNT PAID:", amount, 
                    new Color(255, 215, 0), new Color(255, 215, 0));
        
        addDivider(centerPanel, gbc);
        
        // Description Section (if exists)
        if (description != null && !description.isEmpty() && !description.equals("null")) {
            addReceiptRow(centerPanel, gbc, "DESC:", description, 
                         new Color(180, 180, 180), Color.WHITE);
            addDivider(centerPanel, gbc);
        }
        
        // Status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setOpaque(false);
        
        JLabel statusLabel = new JLabel("✓ PAYMENT COMPLETE ✓");
        statusLabel.setFont(new Font("Impact", Font.BOLD, 14));
        statusLabel.setForeground(new Color(0, 255, 0));
        statusPanel.add(statusLabel);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        centerPanel.add(statusPanel, gbc);
        gbc.gridwidth = 1;
        
        // QR Code simulation
        addQRCodeSection(centerPanel, gbc, transactionId);
        
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        
        // BOTTOM SECTION - Footer
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setOpaque(false);
        
        // Gradient separator
        JPanel separatorBottom = createGradientSeparator();
        separatorBottom.setPreferredSize(new Dimension(10, 10));
        bottomPanel.add(separatorBottom, BorderLayout.NORTH);
        
        // Thank you message
        JPanel thankYouPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        thankYouPanel.setOpaque(false);
        thankYouPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JLabel thankYou1 = new JLabel("THANK YOU FOR YOUR PAYMENT!", SwingConstants.CENTER);
        thankYou1.setFont(new Font("Impact", Font.BOLD, 14));
        thankYou1.setForeground(new Color(255, 102, 0));
        
        JLabel thankYou2 = new JLabel("🏋️‍♂️ 💪 🏋️‍♀️", SwingConstants.CENTER);
        thankYou2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        thankYou2.setForeground(new Color(255, 102, 0));
        
        thankYouPanel.add(thankYou1);
        thankYouPanel.add(thankYou2);
        
        bottomPanel.add(thankYouPanel, BorderLayout.CENTER);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 8, 10));
        
        JButton btnPrint = createStyledButton("PRINT RECEIPT", new Color(255, 102, 0));
        JButton btnClose = createStyledButton("CLOSE", new Color(100, 100, 100));
        
        btnPrint.setPreferredSize(new Dimension(120, 35));
        btnClose.setPreferredSize(new Dimension(80, 35));
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnClose.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        // FIX: Create a final reference to contentPanel for the ActionListener
        final JPanel printablePanel = contentPanel;
        
        btnPrint.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    printReceipt(printablePanel);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(Transaction.this, "Print failed: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        btnClose.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptDialog.dispose();
            }
        });
        
        actionPanel.add(btnPrint);
        actionPanel.add(btnClose);
        
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add subtle shadow effect to content panel
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 2),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        // WRAP IN SCROLLPANE - This ensures all content is accessible
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Style the scrollbar to match gym theme
        scrollPane.getVerticalScrollBar().setBackground(new Color(30, 30, 30));
        scrollPane.getVerticalScrollBar().setForeground(new Color(255, 102, 0));
        scrollPane.getHorizontalScrollBar().setBackground(new Color(30, 30, 30));
        scrollPane.getHorizontalScrollBar().setForeground(new Color(255, 102, 0));
        
        // Add scrollpane to dialog
        receiptDialog.add(scrollPane);
        receiptDialog.setVisible(true);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error generating receipt: " + e.getMessage());
        e.printStackTrace();
    }
}

private void showPendingPaymentDetails(int transactionId, String memberInfo, String amount, String date, String description) {
    JDialog pendingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Pending Payment Details", true);
    pendingDialog.setSize(400, 350);
    pendingDialog.setLocationRelativeTo(this);
    pendingDialog.setUndecorated(true);
    
    JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
    mainPanel.setBackground(new Color(30, 30, 30));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    // Header
    JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    headerPanel.setOpaque(false);
    
    JLabel iconLabel = new JLabel("⏳");
    iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    iconLabel.setForeground(new Color(255, 102, 0));
    
    JLabel titleLabel = new JLabel("PENDING PAYMENT");
    titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
    titleLabel.setForeground(new Color(255, 102, 0));
    
    headerPanel.add(iconLabel);
    headerPanel.add(titleLabel);
    
    // Details
    JPanel detailsPanel = new JPanel(new GridBagLayout());
    detailsPanel.setOpaque(false);
    detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
    
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.weightx = 1.0;
    
    addDetailRow2(detailsPanel, gbc, "Transaction ID:", String.valueOf(transactionId), 0);
    addDetailRow2(detailsPanel, gbc, "Member:", memberInfo, 1);
    addDetailRow2(detailsPanel, gbc, "Amount:", amount, 2);
    addDetailRow2(detailsPanel, gbc, "Date:", date, 3);
    addDetailRow2(detailsPanel, gbc, "Status:", "⏳ PENDING", 4);
    
    if (description != null && !description.isEmpty() && !description.equals("null")) {
        addDetailRow2(detailsPanel, gbc, "Description:", description, 5);
    }
    
    // Info message
    JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    infoPanel.setOpaque(false);
    
    JLabel infoLabel = new JLabel("<html><center>This payment is pending authorization.<br>Please wait for admin approval.</center></html>");
    infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
    infoLabel.setForeground(new Color(255, 165, 0));
    infoPanel.add(infoLabel);
    
    // Button
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setOpaque(false);
    
    JButton btnOk = createStyledButton("OK", new Color(255, 102, 0));
    btnOk.setPreferredSize(new Dimension(100, 35));
    btnOk.addActionListener(new java.awt.event.ActionListener() {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            pendingDialog.dispose();
        }
    });
    
    buttonPanel.add(btnOk);
    
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(detailsPanel, BorderLayout.CENTER);
    
    JPanel southPanel = new JPanel(new GridLayout(2, 1));
    southPanel.setOpaque(false);
    southPanel.add(infoPanel);
    southPanel.add(buttonPanel);
    
    mainPanel.add(southPanel, BorderLayout.SOUTH);
    
    pendingDialog.add(mainPanel);
    pendingDialog.setVisible(true);
}

private void addDetailRow2(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
    JLabel lblLabel = new JLabel(label);
    lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblLabel.setForeground(new Color(255, 102, 0));
    
    JLabel lblValue = new JLabel(value != null ? value : "N/A");
    lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblValue.setForeground(Color.WHITE);
    
    gbc.gridx = 0;
    gbc.gridy = row;
    gbc.weightx = 0.3;
    panel.add(lblLabel, gbc);
    
    gbc.gridx = 1;
    gbc.weightx = 0.7;
    panel.add(lblValue, gbc);
}


private void processPayment() {
    // Create payment dialog with gym theme
    JDialog paymentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "OMNIFIT - Payment Processing", true);
    paymentDialog.setLayout(new BorderLayout());
    paymentDialog.setSize(550, 700);
    paymentDialog.setLocationRelativeTo(this);
    paymentDialog.setUndecorated(true);
    
    // Main panel with gradient background
    JPanel mainPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 35), 0, getHeight(), new Color(15, 15, 25));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Add gym pattern
            g2d.setColor(new Color(255, 102, 0, 30));
            for (int i = 0; i < 10; i++) {
                g2d.drawLine(0, i * 50, getWidth(), i * 50 + 25);
            }
            g2d.dispose();
        }
    };
    mainPanel.setLayout(new BorderLayout(15, 15));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
    
    // Header Panel
    JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
    headerPanel.setOpaque(false);
    headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    
    JLabel titleIcon = new JLabel("💪");
    titleIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
    titleIcon.setForeground(new Color(255, 102, 0));
    
    JLabel titleLabel = new JLabel("PROCESS PAYMENT");
    titleLabel.setFont(new Font("Impact", Font.BOLD, 24));
    titleLabel.setForeground(new Color(255, 102, 0));
    
    JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    titlePanel.setOpaque(false);
    titlePanel.add(titleIcon);
    titlePanel.add(titleLabel);
    headerPanel.add(titlePanel, BorderLayout.CENTER);
    
    JPanel separatorLine = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            GradientPaint gp = new GradientPaint(0, 0, new Color(255, 102, 0), getWidth(), 0, new Color(255, 102, 0, 50));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), 2);
            g2d.dispose();
        }
    };
    separatorLine.setPreferredSize(new Dimension(400, 2));
    headerPanel.add(separatorLine, BorderLayout.SOUTH);
    
    // Form Panel
    JPanel formPanel = new JPanel(new GridBagLayout());
    formPanel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets(8, 10, 8, 10);
    gbc.weightx = 1.0;
    
    // Get user role
    String currentUserRole = Config.getCurrentUserRole();
    String currentUsername = Config.getCurrentUsername();
    boolean isAdmin = "Administrator".equals(currentUserRole) || "Admin".equals(currentUserRole);
    
    // Member Selection with Search Filter
    JLabel lblMemberIcon = new JLabel("👤");
    lblMemberIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    JLabel lblMember = new JLabel("SELECT MEMBER");
    lblMember.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblMember.setForeground(new Color(255, 102, 0));
    
    JPanel memberLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    memberLabelPanel.setOpaque(false);
    memberLabelPanel.add(lblMemberIcon);
    memberLabelPanel.add(lblMember);
    
    gbc.gridx = 0; gbc.gridy = 0;
    formPanel.add(memberLabelPanel, gbc);
    
    // Search field for filtering members
    JTextField txtSearchMember = new JTextField();
    txtSearchMember.setBackground(new Color(45, 45, 45));
    txtSearchMember.setForeground(Color.WHITE);
    txtSearchMember.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    txtSearchMember.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    txtSearchMember.setPreferredSize(new Dimension(350, 30));
    txtSearchMember.setToolTipText("Type to search members...");
    
    gbc.gridy = 1;
    formPanel.add(txtSearchMember, gbc);
    
    JComboBox<String> cmbMember = new JComboBox<>();
    cmbMember.setBackground(new Color(45, 45, 45));
    cmbMember.setForeground(Color.WHITE);
    cmbMember.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cmbMember.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1));
    cmbMember.setPreferredSize(new Dimension(350, 35));
    
    // Load members initially (only members with Pending payment status)
    DefaultComboBoxModel<String> memberModel = new DefaultComboBoxModel<>();
    loadPendingMembersToModel(memberModel);
    cmbMember.setModel(memberModel);
    
    gbc.gridy = 2;
    formPanel.add(cmbMember, gbc);
    
    // Search filter functionality (only pending members)
// Search filter functionality (only members with NO payments)
txtSearchMember.addKeyListener(new java.awt.event.KeyAdapter() {
    @Override
    public void keyReleased(java.awt.event.KeyEvent evt) {
        String searchText = txtSearchMember.getText().toLowerCase();
        DefaultComboBoxModel<String> filteredModel = new DefaultComboBoxModel<>();
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(
                "SELECT m.M_ID, m.Name FROM Members m " +
                "WHERE m.Membership_Status = 'Active' " +
                "AND NOT EXISTS (SELECT 1 FROM Payments p " +
                "WHERE p.Member_ID = m.M_ID " +
                "AND p.Service_ID = m.S_ID " +
                "AND (p.Payment_Status = 'Paid' OR p.Payment_Status = 'Pending')) " +
                "AND (m.Name LIKE ? OR m.M_ID LIKE ?) " +
                "ORDER BY m.Name")) {
            
            String pattern = "%" + searchText + "%";
            pst.setString(1, pattern);
            pst.setString(2, pattern);
            ResultSet rs = pst.executeQuery();
            
            filteredModel.addElement("Select Member");
            while (rs.next()) {
                filteredModel.addElement(rs.getInt("M_ID") + " - " + rs.getString("Name"));
            }
            
            if (filteredModel.getSize() == 1) {
                filteredModel.addElement("No members available for payment");
            }
            cmbMember.setModel(filteredModel);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
});
    
    // Service Display (Fixed - shows member's assigned service)
    JLabel lblServiceIcon = new JLabel("🏋️");
    lblServiceIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    JLabel lblService = new JLabel("ASSIGNED SERVICE");
    lblService.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblService.setForeground(new Color(255, 102, 0));
    
    JPanel serviceLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    serviceLabelPanel.setOpaque(false);
    serviceLabelPanel.add(lblServiceIcon);
    serviceLabelPanel.add(lblService);
    
    gbc.gridy = 3;
    formPanel.add(serviceLabelPanel, gbc);
    
    JTextField txtService = new JTextField();
    txtService.setBackground(new Color(45, 45, 45));
    txtService.setForeground(Color.WHITE);
    txtService.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    txtService.setEditable(false);
    txtService.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    
    gbc.gridy = 4;
    formPanel.add(txtService, gbc);
    
    // Amount Panel
    JLabel lblAmountIcon = new JLabel("💰");
    lblAmountIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    JLabel lblAmount = new JLabel("AMOUNT");
    lblAmount.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblAmount.setForeground(new Color(255, 102, 0));
    
    JPanel amountLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    amountLabelPanel.setOpaque(false);
    amountLabelPanel.add(lblAmountIcon);
    amountLabelPanel.add(lblAmount);
    
    gbc.gridy = 5;
    formPanel.add(amountLabelPanel, gbc);
    
    JTextField txtAmount = new JTextField();
    txtAmount.setBackground(new Color(45, 45, 45));
    txtAmount.setForeground(new Color(255, 215, 0));
    txtAmount.setFont(new Font("Segoe UI", Font.BOLD, 16));
    txtAmount.setCaretColor(Color.WHITE);
    txtAmount.setEditable(false);
    txtAmount.setHorizontalAlignment(JTextField.CENTER);
    txtAmount.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    
    gbc.gridy = 6;
    formPanel.add(txtAmount, gbc);
    
    // Payment Method
    JLabel lblMethodIcon = new JLabel("💳");
    lblMethodIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    JLabel lblMethod = new JLabel("PAYMENT METHOD");
    lblMethod.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblMethod.setForeground(new Color(255, 102, 0));
    
    JPanel methodLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    methodLabelPanel.setOpaque(false);
    methodLabelPanel.add(lblMethodIcon);
    methodLabelPanel.add(lblMethod);
    
    gbc.gridy = 7;
    formPanel.add(methodLabelPanel, gbc);
    
    JComboBox<String> cmbMethod = new JComboBox<>(new String[]{"Cash", "GCash", "PayMaya", "Bank Transfer", "Credit Card"});
    cmbMethod.setBackground(new Color(45, 45, 45));
    cmbMethod.setForeground(Color.WHITE);
    cmbMethod.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    cmbMethod.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1));
    cmbMethod.setPreferredSize(new Dimension(350, 35));
    
    gbc.gridy = 8;
    formPanel.add(cmbMethod, gbc);
    
    // Amount Received
    JLabel lblReceivedIcon = new JLabel("💸");
    lblReceivedIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
    JLabel lblReceived = new JLabel("AMOUNT RECEIVED");
    lblReceived.setFont(new Font("Segoe UI", Font.BOLD, 13));
    lblReceived.setForeground(new Color(255, 102, 0));
    
    JPanel receivedLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    receivedLabelPanel.setOpaque(false);
    receivedLabelPanel.add(lblReceivedIcon);
    receivedLabelPanel.add(lblReceived);
    
    gbc.gridy = 9;
    formPanel.add(receivedLabelPanel, gbc);
    
    JTextField txtReceived = new JTextField();
    txtReceived.setBackground(new Color(45, 45, 45));
    txtReceived.setForeground(Color.WHITE);
    txtReceived.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    txtReceived.setCaretColor(Color.WHITE);
    txtReceived.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(new Color(255, 102, 0, 100), 1),
        BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    
    gbc.gridy = 10;
    formPanel.add(txtReceived, gbc);
    
    // Change Label
    JLabel changeLabel = new JLabel("CHANGE: ₱0.00");
    changeLabel.setFont(new Font("Impact", Font.BOLD, 20));
    changeLabel.setForeground(new Color(255, 215, 0));
    changeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    
    gbc.gridy = 11;
    gbc.insets = new Insets(15, 10, 15, 10);
    formPanel.add(changeLabel, gbc);
    
    // Authorization checkbox for non-admins
    JCheckBox requireAuthCheckBox = new JCheckBox("Require Admin Authorization");
    requireAuthCheckBox.setForeground(new Color(255, 102, 0));
    requireAuthCheckBox.setBackground(new Color(45, 45, 45));
    requireAuthCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    requireAuthCheckBox.setSelected(true);
    
    if (!isAdmin) {
        gbc.gridy = 12;
        gbc.insets = new Insets(5, 10, 5, 10);
        formPanel.add(requireAuthCheckBox, gbc);
        
        JLabel lblInfo = new JLabel("Payment will be pending until admin approval");
        lblInfo.setForeground(new Color(255, 165, 0));
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 13;
        formPanel.add(lblInfo, gbc);
    }
    
    // Variables to store selected member data
    final int[] selectedMemberId = {-1};
    final double[] selectedAmount = {0};
    final int[] selectedServiceId = {-1};
    final String[] selectedMemberName = {""};
    
    // Load member details when selected
    cmbMember.addActionListener(e -> {
        if (cmbMember.getSelectedIndex() > 0) {
            String selected = (String) cmbMember.getSelectedItem();
            // Check if it's a valid member selection (not the "No members" message)
            if (selected.equals("No members with pending payments")) {
                return;
            }
            selectedMemberName[0] = selected.split(" - ")[1];
            int memberId = Integer.parseInt(selected.split(" - ")[0]);
            selectedMemberId[0] = memberId;
            
            // Fetch member's assigned service and fee from database
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(
                    "SELECT m.S_ID, s.Service_Name, s.Fee FROM Members m " +
                    "JOIN Services s ON m.S_ID = s.S_ID WHERE m.M_ID = ?")) {
                
                pst.setInt(1, memberId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    selectedServiceId[0] = rs.getInt("S_ID");
                    selectedAmount[0] = rs.getDouble("Fee");
                    txtService.setText(rs.getString("Service_Name"));
                    txtAmount.setText(String.format("₱ %,.2f", selectedAmount[0]));
                    changeLabel.setText("CHANGE: ₱0.00");
                    txtReceived.setText("");
                    changeLabel.setForeground(new Color(255, 215, 0));
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(paymentDialog, "Error fetching member details: " + ex.getMessage());
            }
        } else {
            txtService.setText("");
            txtAmount.setText("");
            selectedMemberId[0] = -1;
            selectedAmount[0] = 0;
            selectedServiceId[0] = -1;
            selectedMemberName[0] = "";
        }
    });
    
    // Live change calculation with real-time updates
    javax.swing.Timer changeTimer = new javax.swing.Timer(100, null);
    changeTimer.addActionListener(evt -> {
        try {
            if (selectedAmount[0] <= 0) return;
            String input = txtReceived.getText().trim();
            if (input.isEmpty()) {
                changeLabel.setText("CHANGE: ₱0.00");
                changeLabel.setForeground(new Color(255, 215, 0));
                return;
            }
            double received = Double.parseDouble(input);
            double change = received - selectedAmount[0];
            if (change >= 0) {
                changeLabel.setText(String.format("CHANGE: ₱%,.2f", change));
                changeLabel.setForeground(new Color(0, 255, 100));
            } else {
                changeLabel.setText(String.format("SHORT BY: ₱%,.2f", Math.abs(change)));
                changeLabel.setForeground(Color.RED);
            }
        } catch (NumberFormatException ex) {
            changeLabel.setText("INVALID AMOUNT");
            changeLabel.setForeground(Color.RED);
        }
    });
    
    txtReceived.addKeyListener(new java.awt.event.KeyAdapter() {
        @Override
        public void keyReleased(java.awt.event.KeyEvent evt) {
            changeTimer.restart();
        }
    });
    
    // Button Panel
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
    buttonPanel.setOpaque(false);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
    
    JButton btnProcess = new JButton("CONFIRM PAYMENT");
    btnProcess.setBackground(new Color(255, 102, 0));
    btnProcess.setForeground(Color.WHITE);
    btnProcess.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnProcess.setFocusPainted(false);
    btnProcess.setBorderPainted(false);
    btnProcess.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnProcess.setPreferredSize(new Dimension(200, 45));
    
    JButton btnCancel = new JButton("CANCEL");
    btnCancel.setBackground(new Color(100, 100, 100));
    btnCancel.setForeground(Color.WHITE);
    btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    btnCancel.setFocusPainted(false);
    btnCancel.setBorderPainted(false);
    btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
    btnCancel.setPreferredSize(new Dimension(120, 45));
    
    buttonPanel.add(btnProcess);
    buttonPanel.add(btnCancel);
    
    // Assemble dialog
    mainPanel.add(headerPanel, BorderLayout.NORTH);
    mainPanel.add(formPanel, BorderLayout.CENTER);
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    
    // Add shadow effect
    JPanel shadowPanel = new JPanel(new BorderLayout());
    shadowPanel.setBackground(new Color(0, 0, 0, 80));
    shadowPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 102, 0, 150), 2));
    shadowPanel.add(mainPanel, BorderLayout.CENTER);
    
    paymentDialog.add(shadowPanel);
    
    // ADD BUTTON ACTIONS BEFORE SHOWING THE DIALOG
    btnProcess.addActionListener(evt -> {
        if (selectedMemberId[0] == -1) {
            JOptionPane.showMessageDialog(paymentDialog, "Please select a member");
            return;
        }
        
        try {
            String receivedText = txtReceived.getText().trim();
            if (receivedText.isEmpty()) {
                JOptionPane.showMessageDialog(paymentDialog, "Please enter amount received");
                return;
            }
            
            double received = Double.parseDouble(receivedText);
            if (received < selectedAmount[0]) {
                JOptionPane.showMessageDialog(paymentDialog, 
                    String.format("Insufficient amount received!\n\nAmount Due: ₱%,.2f\nAmount Received: ₱%,.2f\nShort by: ₱%,.2f", 
                    selectedAmount[0], received, selectedAmount[0] - received));
                return;
            }
            
            String paymentStatus = "Paid";
            String notes = "Processed by: " + currentUsername + " (" + currentUserRole + ")";
            
            if (!isAdmin && requireAuthCheckBox.isSelected()) {
                paymentStatus = "Pending";
                notes = "PENDING AUTHORIZATION - " + notes;
            }
            
            // Insert payment record
            String paymentSql = "INSERT INTO Payments (Member_ID, Service_ID, Amount, Payment_Date, Payment_Method, Payment_Status, Notes) " +
                               "VALUES (?, ?, ?, datetime('now'), ?, ?, ?)";
            
            try (Connection conn = Config.connect();
                 PreparedStatement pst = conn.prepareStatement(paymentSql, Statement.RETURN_GENERATED_KEYS)) {
                
                pst.setInt(1, selectedMemberId[0]);
                pst.setInt(2, selectedServiceId[0]);
                pst.setDouble(3, selectedAmount[0]);
                pst.setString(4, (String) cmbMethod.getSelectedItem());
                pst.setString(5, paymentStatus);
                pst.setString(6, notes);
                
                int result = pst.executeUpdate();
                
                if (result > 0) {
                    ResultSet rs = pst.getGeneratedKeys();
                    int paymentId = -1;
                    if (rs.next()) {
                        paymentId = rs.getInt(1);
                    }
                    
                    // Record in Transactions table
                    String transSql = "INSERT INTO Transactions (Transaction_Type, Amount, Transaction_Date, Member_ID, Payment_ID, Description) " +
                                     "VALUES (?, ?, datetime('now'), ?, ?, ?)";
                    
                    try (PreparedStatement transPst = conn.prepareStatement(transSql)) {
                        String transType = paymentStatus.equals("Paid") ? "MEMBER_PAYMENT" : "PENDING_PAYMENT";
                        transPst.setString(1, transType);
                        transPst.setDouble(2, selectedAmount[0]);
                        transPst.setInt(3, selectedMemberId[0]);
                        transPst.setInt(4, paymentId);
                        
                        String transDesc = paymentStatus.equals("Paid") ? 
                            "Member payment processed" : 
                            "Member payment pending authorization";
                        transPst.setString(5, transDesc);
                        transPst.executeUpdate();
                    }
                    
                    paymentDialog.dispose();
                    loadTransactionData();
                    
                    if (dataChangeListener != null) {
                        dataChangeListener.onDataChanged();
                    }
                    
                    if (paymentStatus.equals("Paid")) {
                        double change = received - selectedAmount[0];
                        JOptionPane.showMessageDialog(paymentDialog, 
                            String.format("PAYMENT SUCCESSFUL!\n\nMember: %s\nAmount: ₱%,.2f\nChange: ₱%,.2f\nStatus: PAID", 
                            selectedMemberName[0], selectedAmount[0], change), 
                            "Payment Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(paymentDialog, 
                            "⏳ Payment submitted for authorization!\n\nMember: " + selectedMemberName[0] + 
                            "\nAmount: ₱" + String.format("%,.2f", selectedAmount[0]) + 
                            "\nStatus: PENDING\n\nAn administrator will review and approve this payment.", 
                            "Pending Authorization", 
                            JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(paymentDialog, "Please enter a valid amount received.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(paymentDialog, "Database error: " + ex.getMessage());
            ex.printStackTrace();
        }
    });
    
    btnCancel.addActionListener(evt -> {
        paymentDialog.dispose();
    });
    
    // NOW SHOW THE DIALOG
    paymentDialog.setVisible(true);
}

private void loadPendingMembersToModel(DefaultComboBoxModel<String> model) {
    model.addElement("Select Member");
    String sql = "SELECT m.M_ID, m.Name FROM Members m " +
                 "WHERE m.Membership_Status = 'Active' " +
                 "AND NOT EXISTS (SELECT 1 FROM Payments p " +
                 "WHERE p.Member_ID = m.M_ID " +
                 "AND p.Service_ID = m.S_ID " +
                 "AND (p.Payment_Status = 'Paid' OR p.Payment_Status = 'Pending')) " +
                 "ORDER BY m.Name";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) {
            model.addElement(rs.getInt("M_ID") + " - " + rs.getString("Name"));
        }
        
        if (model.getSize() == 1) {
            model.addElement("No members with pending payments");
        }
        
    } catch (SQLException e) {
        System.err.println("Error loading pending members: " + e.getMessage());
    }
}

private void loadMembersToModel(DefaultComboBoxModel<String> model) {
    model.addElement("Select Member");
    String sql = "SELECT M_ID, Name FROM Members WHERE Membership_Status = 'Active' ORDER BY Name";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) {
            model.addElement(rs.getInt("M_ID") + " - " + rs.getString("Name"));
        }
        
    } catch (SQLException e) {
        System.err.println("Error loading members: " + e.getMessage());
    }
}    
    private void loadServicesToCombo(JComboBox<String> cmb) {
        cmb.addItem("Select Service");
        String sql = "SELECT Service_Name, Fee FROM Services ORDER BY Service_Name";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                cmb.addItem(rs.getString("Service_Name") + " - ₱" + rs.getDouble("Fee"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading services: " + e.getMessage());
        }
    }
    
    private double getServiceFee(String serviceString) {
        String sql = "SELECT Fee FROM Services WHERE Service_Name = ?";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String serviceName = serviceString.split(" - ")[0];
            pst.setString(1, serviceName);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("Fee");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting service fee: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    private int getServiceId(String serviceString) {
        String sql = "SELECT S_ID FROM Services WHERE Service_Name = ?";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            String serviceName = serviceString.split(" - ")[0];
            pst.setString(1, serviceName);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("S_ID");
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting service ID: " + e.getMessage());
        }
        
        return -1;
    }
    
private void showAuthorizationDialog() {
    String currentUserRole = Config.getCurrentUserRole();
    if (!"Administrator".equals(currentUserRole) && !"Admin".equals(currentUserRole)) {
        JOptionPane.showMessageDialog(this, "Access Denied: Only Administrators can authorize payments.");
        return;
    }
    
    JDialog authDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Payment Authorization", true);
    authDialog.setLayout(new BorderLayout());
    authDialog.setSize(900, 500);
    authDialog.setLocationRelativeTo(this);
    
    String[] columns = {"Payment ID", "Member ID", "Member Name", "Service", "Amount", "Date", "Requested By", "Authorize", "Reject"};
    DefaultTableModel model = new DefaultTableModel(columns, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 7 || column == 8;
        }
    };
    
    JTable pendingTable = new JTable(model);
    pendingTable.setRowHeight(40);
    pendingTable.setBackground(new Color(45, 45, 45));
    pendingTable.setForeground(Color.WHITE);
    pendingTable.setGridColor(new Color(60, 60, 60));
    pendingTable.getTableHeader().setBackground(new Color(25, 25, 25));
    pendingTable.getTableHeader().setForeground(new Color(255, 102, 0));
    
    pendingTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer("AUTHORIZE"));
    pendingTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox(), "AUTHORIZE"));
    pendingTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer("REJECT"));
    pendingTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox(), "REJECT"));
    
    // Set column widths
    pendingTable.getColumnModel().getColumn(0).setPreferredWidth(80);
    pendingTable.getColumnModel().getColumn(1).setPreferredWidth(70);
    pendingTable.getColumnModel().getColumn(2).setPreferredWidth(120);
    pendingTable.getColumnModel().getColumn(3).setPreferredWidth(100);
    pendingTable.getColumnModel().getColumn(4).setPreferredWidth(80);
    pendingTable.getColumnModel().getColumn(5).setPreferredWidth(80);
    pendingTable.getColumnModel().getColumn(6).setPreferredWidth(100);
    pendingTable.getColumnModel().getColumn(7).setPreferredWidth(80);
    pendingTable.getColumnModel().getColumn(8).setPreferredWidth(80);
    
    loadPendingPayments(model);
    
    JScrollPane scrollPane = new JScrollPane(pendingTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    
    JButton refreshBtn = new JButton("REFRESH");
    refreshBtn.setBackground(new Color(255, 102, 0));
    refreshBtn.setForeground(Color.WHITE);
    refreshBtn.setFocusPainted(false);
    refreshBtn.setBorderPainted(false);
    refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    refreshBtn.addActionListener(e -> {
        model.setRowCount(0);
        loadPendingPayments(model);
    });
    
    JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topPanel.setBackground(new Color(30, 30, 30));
    topPanel.add(refreshBtn);
    
    authDialog.add(topPanel, BorderLayout.NORTH);
    authDialog.add(scrollPane, BorderLayout.CENTER);
    authDialog.setVisible(true);
}

private void loadPendingPayments(DefaultTableModel model) {
    String sql = "SELECT p.Payment_ID, p.Member_ID, m.Name as Member_Name, " +
                 "s.Service_Name, p.Amount, p.Payment_Date, p.Notes " +
                 "FROM Payments p " +
                 "LEFT JOIN Members m ON p.Member_ID = m.M_ID " +
                 "LEFT JOIN Services s ON p.Service_ID = s.S_ID " +
                 "WHERE p.Payment_Status = 'Pending' " +
                 "ORDER BY p.Payment_Date ASC";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(sql);
         ResultSet rs = pst.executeQuery()) {
        
        while (rs.next()) {
            String notes = rs.getString("Notes");
            String requestedBy = "Unknown";
            
            // Extract requested by from notes
            if (notes != null) {
                if (notes.contains("Processed by:")) {
                    requestedBy = notes.replace("PENDING AUTHORIZATION - ", "")
                                       .replace("Processed by:", "")
                                       .trim();
                    if (requestedBy.length() > 20) {
                        requestedBy = requestedBy.substring(0, 17) + "...";
                    }
                }
            }
            
            int paymentId = rs.getInt("Payment_ID");
            int memberId = rs.getInt("Member_ID");
            String memberName = rs.getString("Member_Name");
            String serviceName = rs.getString("Service_Name");
            double amount = rs.getDouble("Amount");
            String paymentDate = rs.getString("Payment_Date");
            
            String formattedDate = "N/A";
            if (paymentDate != null && paymentDate.length() >= 10) {
                formattedDate = paymentDate.substring(0, 10);
            }
            
            model.addRow(new Object[]{
                paymentId,
                memberId,
                memberName != null ? memberName : "Unknown",
                serviceName != null ? serviceName : "Unknown",
                "₱" + String.format("%,.2f", amount),
                formattedDate,
                requestedBy,
                "AUTHORIZE",
                "REJECT"
            });
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No pending payments found", "", "", "", "", "", "", "", ""});
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading pending payments: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void authorizePayment(int paymentId) {
        String currentAdmin = Config.getCurrentUsername();
        String updateSql = "UPDATE Payments SET Payment_Status = 'Paid', Notes = Notes || ' (Authorized by: " + currentAdmin + ")' WHERE Payment_ID = ?";
        
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(updateSql)) {
            
            pst.setInt(1, paymentId);
            int result = pst.executeUpdate();
            
            if (result > 0) {
                // Get payment details for transaction record
                String getSql = "SELECT Member_ID, Amount FROM Payments WHERE Payment_ID = ?";
                try (PreparedStatement getPst = conn.prepareStatement(getSql)) {
                    getPst.setInt(1, paymentId);
                    ResultSet rs = getPst.executeQuery();
                    if (rs.next()) {
                        // Record in Transactions table
                        String transSql = "INSERT INTO Transactions (Transaction_Type, Amount, Transaction_Date, Member_ID, Payment_ID, Description, Staff_ID) " +
                                         "VALUES ('PAYMENT_AUTHORIZED', ?, datetime('now'), ?, ?, 'Payment authorized by admin', (SELECT Staffid FROM Management WHERE Username = ?))";
                        try (PreparedStatement transPst = conn.prepareStatement(transSql)) {
                            transPst.setDouble(1, rs.getDouble("Amount"));
                            transPst.setInt(2, rs.getInt("Member_ID"));
                            transPst.setInt(3, paymentId);
                            transPst.setString(4, currentAdmin);
                            transPst.executeUpdate();
                        }
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Payment authorized successfully!");
                loadTransactionData();
                
                if (dataChangeListener != null) {
                    dataChangeListener.onDataChanged();
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error authorizing payment: " + e.getMessage());
        }
    }
    
private void showTransactionDetails() {
    int selectedRow = tblTransactions.getSelectedRow();
    if (selectedRow < 0) return;
    
    try {
        // SAFELY get values with updated indices (Type column removed)
        int transactionId = 0;
        Object idObj = tblTransactions.getValueAt(selectedRow, 0);
        if (idObj instanceof Integer) {
            transactionId = (Integer) idObj;
        } else if (idObj instanceof String) {
            try {
                transactionId = Integer.parseInt((String) idObj);
            } catch (NumberFormatException e) {
                transactionId = 0;
            }
        }
        
        // Column 1 is now Amount
        String amount = "";
        Object amountObj = tblTransactions.getValueAt(selectedRow, 1);
        if (amountObj != null) amount = amountObj.toString();
        
        // Column 2 is now Date
        String date = "";
        Object dateObj = tblTransactions.getValueAt(selectedRow, 2);
        if (dateObj != null) date = dateObj.toString();
        
        // Column 3 is now Member
        String memberInfo = "";
        Object memberObj = tblTransactions.getValueAt(selectedRow, 3);
        if (memberObj != null) memberInfo = memberObj.toString();
        
        // Column 4 is now Payment ID
        String paymentId = "";
        Object paymentObj = tblTransactions.getValueAt(selectedRow, 4);
        if (paymentObj != null) paymentId = paymentObj.toString();
        
        // Column 5 is now Description
        String description = "";
        Object descObj = tblTransactions.getValueAt(selectedRow, 5);
        if (descObj != null) description = descObj.toString();
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        addDetailRow(panel, "Transaction ID:", String.valueOf(transactionId));
        addDetailRow(panel, "Amount:", amount);
        addDetailRow(panel, "Date:", date);
        addDetailRow(panel, "Member:", memberInfo);
        addDetailRow(panel, "Payment ID:", paymentId);
        addDetailRow(panel, "Description:", description);
        
        JOptionPane.showMessageDialog(this, panel, "Transaction Details", JOptionPane.PLAIN_MESSAGE);
        
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error showing details: " + e.getMessage());
    }
}
// Helper method to add detail rows
private void addDetailRow(JPanel panel, String label, String value) {
    JLabel lblLabel = new JLabel(label);
    lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
    lblLabel.setForeground(new Color(255, 102, 0));
    
    JLabel lblValue = new JLabel("<html><font color='white'>" + (value != null && !value.isEmpty() ? value : "N/A") + "</font></html>");
    lblValue.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    
    panel.add(lblLabel);
    panel.add(lblValue);
}
    
    private String padRight(String text, int length) {
        if (text == null) text = "";
        if (text.length() > length) text = text.substring(0, length - 3) + "...";
        return String.format("%-" + length + "s", text);
    }
    
    // Custom button renderer and editor classes
class ButtonRenderer extends JButton implements TableCellRenderer {
    private String buttonText;
    
    public ButtonRenderer() {
        this("AUTHORIZE");
    }
    
    public ButtonRenderer(String text) {
        super(text);
        this.buttonText = text;
        setOpaque(true);
        setBackground(new Color(255, 102, 0));
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 11));
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        setText(buttonText);
        if (buttonText.equals("REJECT")) {
            setBackground(new Color(200, 50, 50));
        } else {
            setBackground(new Color(255, 102, 0));
        }
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private int selectedRow;
    private int paymentId;
    private JTable table;
    private String actionType;
    
    public ButtonEditor(JCheckBox checkBox, String action) {
        super(checkBox);
        this.actionType = action;
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(action.equals("AUTHORIZE") ? new Color(255, 102, 0) : new Color(200, 50, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                fireEditingStopped();
                
                if (table != null && selectedRow >= 0) {
                    try {
                        Object value = table.getValueAt(selectedRow, 0);
                        
                        if (value == null) {
                            JOptionPane.showMessageDialog(button, "Payment ID is null");
                            return;
                        }
                        
                        if (value instanceof Integer) {
                            paymentId = (Integer) value;
                        } else if (value instanceof String) {
                            String strValue = (String) value;
                            strValue = strValue.replaceAll("[^0-9]", "").trim();
                            if (!strValue.isEmpty()) {
                                try {
                                    paymentId = Integer.parseInt(strValue);
                                } catch (NumberFormatException ex) {
                                    JOptionPane.showMessageDialog(button, "Invalid Payment ID format: " + value);
                                    return;
                                }
                            } else {
                                JOptionPane.showMessageDialog(button, "Payment ID is not a valid number");
                                return;
                            }
                        }
                        
                        String memberName = "Unknown";
                        if (table.getColumnCount() > 2) {
                            Object memberValue = table.getValueAt(selectedRow, 2);
                            if (memberValue != null) {
                                memberName = memberValue.toString();
                            }
                        }
                        
                        if (actionType.equals("AUTHORIZE")) {
                            int confirm = JOptionPane.showConfirmDialog(button, 
                                "Authorize payment for " + memberName + "?\nPayment ID: " + paymentId, 
                                "Confirm Authorization", 
                                JOptionPane.YES_NO_OPTION);
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                authorizePayment(paymentId);
                            }
                        } else {
                            int confirm = JOptionPane.showConfirmDialog(button, 
                                "REJECT payment for " + memberName + "?\nPayment ID: " + paymentId + "\n\nThis action cannot be undone.", 
                                "Confirm Rejection", 
                                JOptionPane.YES_NO_OPTION);
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                rejectPayment(paymentId);
                            }
                        }
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                ((DefaultTableModel) table.getModel()).setRowCount(0);
                                loadPendingPayments((DefaultTableModel) table.getModel());
                            }
                        });
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(button, "Error processing payment: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.selectedRow = row;
        this.label = actionType;
        button.setText(label);
        return button;
    }
    
    @Override
    public Object getCellEditorValue() {
        return label;
    }
}

private void rejectPayment(int paymentId) {
    String currentAdmin = Config.getCurrentUsername();
    String rejectSql = "UPDATE Payments SET Payment_Status = 'Rejected', Notes = Notes || ' (REJECTED by: " + currentAdmin + ")' WHERE Payment_ID = ?";
    
    try (Connection conn = Config.connect();
         PreparedStatement pst = conn.prepareStatement(rejectSql)) {
        
        pst.setInt(1, paymentId);
        int result = pst.executeUpdate();
        
        if (result > 0) {
            // Get payment details for transaction record
            String getSql = "SELECT Member_ID, Amount FROM Payments WHERE Payment_ID = ?";
            try (PreparedStatement getPst = conn.prepareStatement(getSql)) {
                getPst.setInt(1, paymentId);
                ResultSet rs = getPst.executeQuery();
                if (rs.next()) {
                    // Record in Transactions table
                    String transSql = "INSERT INTO Transactions (Transaction_Type, Amount, Transaction_Date, Member_ID, Payment_ID, Description) " +
                                     "VALUES ('PAYMENT_REJECTED', ?, datetime('now'), ?, ?, 'Payment rejected by admin')";
                    try (PreparedStatement transPst = conn.prepareStatement(transSql)) {
                        transPst.setDouble(1, rs.getDouble("Amount"));
                        transPst.setInt(2, rs.getInt("Member_ID"));
                        transPst.setInt(3, paymentId);
                        transPst.executeUpdate();
                    }
                }
            }
            
            JOptionPane.showMessageDialog(this, "Payment rejected successfully!");
            loadTransactionData();
            
            // Refresh the Members panel - wrap in try-catch
            try {
                Members membersPanel = getMembersPanel();
                if (membersPanel != null) {
                    membersPanel.loadMemberData();
                }
            } catch (Exception e) {
                // Members panel might not be accessible, ignore
                System.err.println("Could not refresh members panel: " + e.getMessage());
            }
            
            if (dataChangeListener != null) {
                dataChangeListener.onDataChanged();
            }
        }
        
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error rejecting payment: " + e.getMessage());
    }
}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(10, 10, 10, 180));
        g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        summaryPanel = new javax.swing.JPanel();
        lblTotalCountLabel = new javax.swing.JLabel();
        lblTotalCount = new javax.swing.JLabel();
        lblTotalAmountLabel = new javax.swing.JLabel();
        lblTotalAmount = new javax.swing.JLabel();
        lblAverageLabel = new javax.swing.JLabel();
        lblAverageAmount = new javax.swing.JLabel();
        lblMaxLabel = new javax.swing.JLabel();
        lblMaxAmount = new javax.swing.JLabel();
        scrollTransactions = new javax.swing.JScrollPane();
        tblTransactions = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        btnViewReceipt = new javax.swing.JButton();
        btnProcessPayment = new javax.swing.JButton();
        btnAuthorize = new javax.swing.JButton();
        searchField = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("Impact", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("TRANSACTION MANAGEMENT");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 300, 30));

        summaryPanel.setOpaque(false);
        summaryPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        lblTotalCountLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTotalCountLabel.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalCountLabel.setText("Total Transactions:");
        summaryPanel.add(lblTotalCountLabel);

        lblTotalCount.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTotalCount.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalCount.setText("0");
        summaryPanel.add(lblTotalCount);

        lblTotalAmountLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTotalAmountLabel.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalAmountLabel.setText("Total Amount:");
        summaryPanel.add(lblTotalAmountLabel);

        lblTotalAmount.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblTotalAmount.setForeground(new java.awt.Color(255, 255, 255));
        lblTotalAmount.setText("₱0.00");
        summaryPanel.add(lblTotalAmount);

        lblAverageLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblAverageLabel.setForeground(new java.awt.Color(255, 255, 255));
        lblAverageLabel.setText("Average:");
        summaryPanel.add(lblAverageLabel);

        lblAverageAmount.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblAverageAmount.setForeground(new java.awt.Color(255, 255, 255));
        lblAverageAmount.setText("₱0.00");
        summaryPanel.add(lblAverageAmount);

        lblMaxLabel.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMaxLabel.setForeground(new java.awt.Color(255, 255, 255));
        lblMaxLabel.setText("Highest:");
        summaryPanel.add(lblMaxLabel);

        lblMaxAmount.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblMaxAmount.setForeground(new java.awt.Color(255, 255, 255));
        lblMaxAmount.setText("₱0.00");
        summaryPanel.add(lblMaxAmount);

        add(summaryPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 540, 30));

        tblTransactions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Transaction ID", "Amount", "Date", "Member", "Payment ID", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrollTransactions.setViewportView(tblTransactions);

        add(scrollTransactions, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, 550, 150));

        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 15, 5));

        btnViewReceipt.setText("VIEW RECEIPT");
        btnViewReceipt.setPreferredSize(new java.awt.Dimension(120, 35));
        buttonPanel.add(btnViewReceipt);

        btnProcessPayment.setText("PROCESS PAYMENT");
        btnProcessPayment.setPreferredSize(new java.awt.Dimension(150, 35));
        buttonPanel.add(btnProcessPayment);

        btnAuthorize.setText("AUTHORIZE");
        btnAuthorize.setPreferredSize(new java.awt.Dimension(100, 35));
        buttonPanel.add(btnAuthorize);

        add(buttonPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, 540, 45));

        searchField.setPreferredSize(new java.awt.Dimension(150, 30));
        add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, 30));

        btnSearch.setText("SEARCH");
        btnSearch.setPreferredSize(new java.awt.Dimension(80, 30));
        add(btnSearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 60, -1, -1));

        btnRefresh.setText("REFRESH");
        btnRefresh.setPreferredSize(new java.awt.Dimension(80, 30));
        add(btnRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 60, -1, 30));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAuthorize;
    private javax.swing.JButton btnProcessPayment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewReceipt;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JLabel lblAverageAmount;
    private javax.swing.JLabel lblAverageLabel;
    private javax.swing.JLabel lblMaxAmount;
    private javax.swing.JLabel lblMaxLabel;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalAmount;
    private javax.swing.JLabel lblTotalAmountLabel;
    private javax.swing.JLabel lblTotalCount;
    private javax.swing.JLabel lblTotalCountLabel;
    private javax.swing.JScrollPane scrollTransactions;
    private javax.swing.JTextField searchField;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JTable tblTransactions;
    // End of variables declaration//GEN-END:variables
}