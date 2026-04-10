package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;

public final class Profile extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    private File selectedImageFile;
    private String currentUsername;
    
    // Button hover animation variables
    private float saveButtonGlow = 0.0f;
    private float cancelButtonGlow = 0.0f;
    private float editButtonGlow = 0.0f;
    private float logoutButtonGlow = 0.0f;
    private float uploadButtonGlow = 0.0f;
    private float containerPulse = 0.0f;
    
    private Timer animationTimer;
    private long lastFrameTime = System.nanoTime();

    public Profile(DashboardForm dashboard) {
        this.dashboard = dashboard;
        initComponents();
        setOpaque(false);
        setEditMode(false);
        styleButtons();
        startAnimationTimer();
    }
    
    private void styleButtons() {
        styleButton(btnEditProfile, "EDIT PROFILE", 0, new Color(0, 150, 200), new Color(0, 200, 255));
        styleButton(btnSave, "SAVE", 1, new Color(0, 180, 0), new Color(0, 230, 0));
        styleButton(btnCancel, "CANCEL", 2, new Color(200, 50, 50), new Color(255, 80, 80));
        styleButton(btnLogout, "LOGOUT", 3, new Color(200, 50, 50), new Color(255, 80, 80));
        styleButton(btnUpload, "Change Photo", 4, new Color(100, 100, 200), new Color(150, 150, 255));
    }
    
    private void styleButton(JButton button, String text, int index, Color color1, Color color2) {
        button.setText(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.putClientProperty("index", index);
        button.putClientProperty("hoverProgress", 0.0f);
        
        button.addMouseListener(new MouseAdapter() {
            private Timer hoverTimer;
            private float targetIntensity = 0.0f;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                targetIntensity = 1.0f;
                startHoverAnimation(button, targetIntensity, index);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                targetIntensity = 0.0f;
                startHoverAnimation(button, targetIntensity, index);
            }
            
            private void startHoverAnimation(JButton btn, float target, int idx) {
                if (hoverTimer != null && hoverTimer.isRunning()) {
                    hoverTimer.stop();
                }
                
                hoverTimer = new Timer(10, null);
                hoverTimer.addActionListener(evt -> {
                    float current = getFloatClientProperty(btn, "hoverProgress", 0.0f);
                    float step = 0.15f;
                    
                    if (target > current) {
                        current = Math.min(current + step, target);
                    } else {
                        current = Math.max(current - step, target);
                    }
                    
                    btn.putClientProperty("hoverProgress", current);
                    
                    if (idx == 0) editButtonGlow = current;
                    else if (idx == 1) saveButtonGlow = current;
                    else if (idx == 2) cancelButtonGlow = current;
                    else if (idx == 3) logoutButtonGlow = current;
                    else if (idx == 4) uploadButtonGlow = current;
                    
                    if (Math.abs(current - target) < 0.01f) {
                        hoverTimer.stop();
                    }
                    
                    btn.repaint();
                });
                hoverTimer.start();
            }
        });
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton btn = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = btn.getWidth();
                int h = btn.getHeight();
                float hover = getFloatClientProperty(btn, "hoverProgress", 0.0f);
                
                // Draw 3D button background
                GradientPaint gradient = new GradientPaint(0, 0, color1, w, h, color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                
                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 80));
                        g2.fillRoundRect(2, 2, w, h, 12, 12);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
                
                // Draw glow on hover
                if (hover > 0.01f) {
                    float pulse = 1.0f + 0.2f * (float)Math.sin(containerPulse * 3);
                    int alpha = (int)(200 * hover * pulse);
                    g2.setColor(new Color(255, 255, 255, alpha));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(2, 2, w - 5, h - 5, 10, 10);
                }
                
                // Draw text
                g2.setFont(btn.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(text)) / 2;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                
                g2.setColor(Color.WHITE);
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        });
    }
    
    private void startAnimationTimer() {
        animationTimer = new Timer(16, e -> {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = currentTime;
            
            containerPulse += deltaTime * 3;
            
            btnEditProfile.repaint();
            btnSave.repaint();
            btnCancel.repaint();
            btnLogout.repaint();
            btnUpload.repaint();
        });
        animationTimer.start();
    }
    
    private float getFloatClientProperty(JButton button, String key, float defaultValue) {
        Object value = button.getClientProperty(key);
        if (value instanceof Float) {
            return (Float) value;
        }
        return defaultValue;
    }

    private void setEditMode(boolean editing) {
        lblRole.setVisible(!editing);
        lblEmail.setVisible(!editing);
        lblSalary.setVisible(!editing);
        lblStatus.setVisible(!editing);
        btnLogout.setVisible(!editing);
        btnEditProfile.setVisible(!editing);
        lblUsername.setVisible(!editing);

        lblUserHint.setVisible(editing);
        txtNewUsername.setVisible(editing);
        btnUpload.setVisible(editing);
        btnSave.setVisible(editing);
        btnCancel.setVisible(editing);
        
        if (editing) {
            txtNewUsername.setText(lblUsername.getText());
        }
    }

    private void uploadImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = chooser.getSelectedFile();
            displayImage(selectedImageFile.getAbsolutePath());
        }
    }

    private void displayImage(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(path);
                Image scaledImage = originalIcon.getImage().getScaledInstance(140, 130, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(scaledImage));
                lblImagePreview.setText(""); 
            } catch (Exception e) {
                lblImagePreview.setText("Error");
            }
        } else {
            lblImagePreview.setIcon(null);
            lblImagePreview.setText("No Image");
        }
    }

    private void saveProfileChanges() {
        String newUser = txtNewUsername.getText().trim();
        if (newUser.isEmpty()) {
            showStyledMessage("ERROR", "Username cannot be empty!", true);
            return;
        }

        Connection conn = null;
        try {
            conn = Config.connect();
            conn.setAutoCommit(false);

            // Update Management table
            String sqlMgmt = "UPDATE Management SET Username = ? WHERE Username = ?";
            PreparedStatement pstMgmt = conn.prepareStatement(sqlMgmt);
            pstMgmt.setString(1, newUser);
            pstMgmt.setString(2, currentUsername);
            pstMgmt.executeUpdate();

            // Update Users table
            String sqlUsers = "UPDATE Users SET Username = ?, Profile_Pic = ? WHERE Username = ?";
            PreparedStatement pstUsers = conn.prepareStatement(sqlUsers);
            pstUsers.setString(1, newUser);
            pstUsers.setString(2, (selectedImageFile != null) ? selectedImageFile.getAbsolutePath() : null);
            pstUsers.setString(3, currentUsername);
            
            int rows = pstUsers.executeUpdate();

            if (rows > 0) {
                conn.commit();
                currentUsername = newUser; 
                lblUsername.setText(currentUsername);
                
                showStyledMessage("SUCCESS", "Profile Saved Successfully!", false);
                setEditMode(false);
                loadUserProfile(currentUsername);
            }
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            showStyledMessage("ERROR", "Update Failed: " + e.getMessage(), true);
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    public void loadUserProfile(String username) {
        this.currentUsername = username;
        String sql = "SELECT U.U_ID, U.Username, U.Email, U.Members_Status, U.Profile_Pic, M.Role_Position, M.Salary_PayRate "
                   + "FROM Users U LEFT JOIN Management M ON U.Username = M.Username WHERE U.Username = ?";

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblUsername.setText(rs.getString("Username").toUpperCase());
                AccountID.setText("Account ID: " + rs.getString("U_ID"));
                lblEmail.setText("EMAIL: " + rs.getString("Email"));
                lblStatus.setText("STATUS: " + rs.getString("Members_Status"));
                lblRole.setText("POSITION: " + (rs.getString("Role_Position") != null ? rs.getString("Role_Position") : "Member"));
                lblSalary.setText("SALARY: P" + String.format("%.2f", rs.getDouble("Salary_PayRate")));
                
                String imagePath = rs.getString("Profile_Pic");
                displayImage(imagePath);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void showStyledMessage(String title, String message, boolean isError) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(300, 160);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(new Color(0, 0, 0, 0));
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 40, 240));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createLineBorder(isError ? new Color(255, 80, 80) : new Color(0, 255, 100), 2));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(isError ? new Color(255, 100, 100) : new Color(0, 255, 100));
        titleLabel.setBorder(new EmptyBorder(20, 10, 10, 10));
        
        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setBorder(new EmptyBorder(5, 10, 20, 10));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msgLabel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        new Timer(1800, e -> dialog.dispose()).start();
    }
    
    private void showLogoutConfirmation() {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setModal(true);
        
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(30, 30, 40, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.setColor(new Color(255, 100, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("CONFIRM LOGOUT");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(255, 100, 100));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, 25, 350, 30);
        panel.add(titleLabel);
        
        JLabel msgLabel = new JLabel("Are you sure you want to logout?");
        msgLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        msgLabel.setBounds(0, 65, 350, 25);
        panel.add(msgLabel);
        
        JLabel warningLabel = new JLabel("Unsaved changes will be lost.");
        warningLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        warningLabel.setForeground(new Color(255, 200, 100));
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningLabel.setBounds(0, 90, 350, 20);
        panel.add(warningLabel);
        
        // Yes button
        JButton yesBtn = new JButton("YES, LOGOUT");
        yesBtn.setBounds(50, 130, 110, 35);
        yesBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        yesBtn.setForeground(Color.WHITE);
        yesBtn.setBackground(new Color(200, 50, 50));
        yesBtn.setFocusPainted(false);
        yesBtn.setBorderPainted(false);
        yesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yesBtn.addActionListener(e -> {
            dialog.dispose();
            dashboard.btnLogoutActionPerformed(null);
        });
        
        // No button
        JButton noBtn = new JButton("CANCEL");
        noBtn.setBounds(190, 130, 110, 35);
        noBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        noBtn.setForeground(Color.WHITE);
        noBtn.setBackground(new Color(80, 80, 100));
        noBtn.setFocusPainted(false);
        noBtn.setBorderPainted(false);
        noBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        noBtn.addActionListener(e -> dialog.dispose());
        
        // Style buttons with hover effects
        styleDialogButton(yesBtn, new Color(200, 50, 50), new Color(255, 80, 80));
        styleDialogButton(noBtn, new Color(80, 80, 100), new Color(120, 120, 140));
        
        panel.add(yesBtn);
        panel.add(noBtn);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void styleDialogButton(JButton button, Color color1, Color color2) {
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color2);
                button.repaint();
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color1);
                button.repaint();
            }
        });
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                JButton btn = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = btn.getWidth();
                int h = btn.getHeight();
                
                GradientPaint gradient = new GradientPaint(0, 0, color1, w, h, color2);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 8, 8);
                
                g2.setColor(Color.WHITE);
                g2.setFont(btn.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textX = (w - fm.stringWidth(btn.getText())) / 2;
                int textY = (h - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(btn.getText(), textX, textY);
                
                g2.dispose();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Modern glass panel styling
        g2.setColor(new Color(15, 20, 30, 200)); 
        g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 30, 30);
        
        // Subtle border
        g2.setColor(new Color(0, 150, 200, 100));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(10, 10, getWidth() - 21, getHeight() - 21, 30, 30);
        
        g2.dispose();
        super.paintComponent(g);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AccountID = new javax.swing.JLabel();
        btnEditProfile = new javax.swing.JButton();
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        Backgroundprofile = new javax.swing.JLabel();
        lblImagePreview = new javax.swing.JLabel();
        lblUserHint = new javax.swing.JLabel();
        txtNewUsername = new javax.swing.JTextField();
        btnUpload = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        lblRole = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblSalary = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        AccountID.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        AccountID.setForeground(new java.awt.Color(255, 255, 255));
        AccountID.setText("ACCOUNT ID:");
        add(AccountID, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 190, -1));

        btnEditProfile.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnEditProfile.setForeground(new java.awt.Color(192, 192, 192));
        btnEditProfile.setText("EDIT PROFILE");
        btnEditProfile.setBorderPainted(false);
        btnEditProfile.setContentAreaFilled(false);
        btnEditProfile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });
        add(btnEditProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 270, 110, -1));

        lblTitle.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("My Profile");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 160, -1));

        lblUsername.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblUsername.setForeground(new java.awt.Color(0, 153, 255));
        lblUsername.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblUsername.setText("USERNAME");
        add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 190, 140, 30));

        Backgroundprofile.setForeground(new java.awt.Color(128, 128, 128));
        Backgroundprofile.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Backgroundprofile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/png-removebg-preview.png"))); // NOI18N
        add(Backgroundprofile, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 40, 180, 150));

        lblImagePreview.setForeground(new java.awt.Color(128, 128, 128));
        lblImagePreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblImagePreview.setText("No Image");
        add(lblImagePreview, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 50, 140, 130));

        lblUserHint.setForeground(new java.awt.Color(255, 255, 255));
        lblUserHint.setText("New Username:");
        add(lblUserHint, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 170, 110, -1));

        txtNewUsername.setBackground(new java.awt.Color(30, 30, 30));
        txtNewUsername.setForeground(new java.awt.Color(255, 255, 255));
        add(txtNewUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 190, 140, 30));

        btnUpload.setText("Change Photo");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });
        add(btnUpload, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 230, 140, -1));

        btnSave.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnSave.setForeground(new java.awt.Color(0, 255, 0));
        btnSave.setText("SAVE");
        btnSave.setContentAreaFilled(false);
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        add(btnSave, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 270, -1, -1));

        btnCancel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        btnCancel.setForeground(new java.awt.Color(255, 0, 0));
        btnCancel.setText("CANCEL");
        btnCancel.setContentAreaFilled(false);
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 270, -1, -1));

        lblRole.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        lblRole.setText("POSITION: ");
        add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 190, -1));

        lblEmail.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        lblEmail.setText("EMAIL: ");
        add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 190, -1));

        lblSalary.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        lblSalary.setForeground(new java.awt.Color(255, 255, 255));
        lblSalary.setText("SALARY: ");
        add(lblSalary, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 190, -1));

        lblStatus.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(255, 153, 0));
        lblStatus.setText("STATUS: ");
        add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 190, -1));

        btnLogout.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 102, 102));
        btnLogout.setText("LOGOUT");
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 257, 110, 40));
    }// </editor-fold>//GEN-END:initComponents
    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {
        setEditMode(true);
    }

    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {
        uploadImage();
    }

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {
        saveProfileChanges();
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        setEditMode(false);
        loadUserProfile(currentUsername);
    }

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        showLogoutConfirmation();
    }                                                                             

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel AccountID;
    private javax.swing.JLabel Backgroundprofile;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpload;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblImagePreview;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSalary;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserHint;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JTextField txtNewUsername;
    // End of variables declaration//GEN-END:variables
}