package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public final class Profile extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    private File selectedImageFile;
    private String currentUsername;

    public Profile(DashboardForm dashboard) {
        this.dashboard = dashboard;
        initComponents();
        setOpaque(false);
        setEditMode(false); 
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
        JOptionPane.showMessageDialog(this, "Username cannot be empty!");
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
            
            // --- REFRESH LOGIC START ---
            // 1. Update the local variable so subsequent edits use the new name
            currentUsername = newUser; 
            
            // 2. Update the UI Labels immediately
            lblUsername.setText(currentUsername); // Replace with your actual Label ID
            // If you have a sidebar or header with the name, update it here too
            
            // 3. (Optional) Re-load the image if changed
            if (selectedImageFile != null) {
                // updateProfilePictureLabel(selectedImageFile.getAbsolutePath());
            }
            // --- REFRESH LOGIC END ---

            JOptionPane.showMessageDialog(this, "Profile Saved!");
            setEditMode(false); 
        }
    } catch (SQLException e) {
        if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
        JOptionPane.showMessageDialog(this, "Update Failed: " + e.getMessage());
    } finally {
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}

    public void loadUserProfile(String username) {
        this.currentUsername = username;
        String sql = "SELECT U.U_ID,U.Username, U.Email, U.Members_Status, U.Profile_Pic, M.Role_Position, M.Salary_PayRate "
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
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Consistent glass panel styling
        g2.setColor(new Color(10, 10, 10, 180)); 
        g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 30, 30); 
        
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
        add(btnEditProfile, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 130, -1));

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
    }                                         

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {                                                                                    
        dashboard.btnLogoutActionPerformed(evt);
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