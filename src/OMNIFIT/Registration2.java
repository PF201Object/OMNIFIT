package OMNIFIT;

import Config.Config;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class Registration2 extends javax.swing.JPanel {
    
    private final DashboardForm dashboard;
    private final Registration1 step1;
    private String selectedRole = "";
    private String selectedGender = "";
    private int homeOriginalY; 
    
    public Registration2(DashboardForm parent, Registration1 step1) {
        this.dashboard = parent;
        this.step1 = step1;
        initComponents(); 
        customInit();     
    }

    private void customInit() {
        this.setOpaque(false);
        // Synchronize Y coordinate for animation baseline
        homeOriginalY = btnMale.getY();
        
        // Setup visual style for all interaction buttons
        JButton[] selectionButtons = {btnMale, btnFemale, btnManager, btnCoach, btnRecep, btnStaff, btnBackToStep1};
        for (JButton btn : selectionButtons) {
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setBorder(BorderFactory.createEmptyBorder());
        }
        
        btnBackToStep1.setBackground(new Color(0, 153, 0));
        btnBackToStep1.setForeground(Color.WHITE);
        btnBackToStep1.setContentAreaFilled(true);
        
        btnFinalRegister.setBackground(new Color(0, 153, 0));
        btnFinalRegister.setForeground(Color.WHITE);
        btnFinalRegister.setContentAreaFilled(true);
        
        Color primaryColor = new Color(92, 225, 230); // Bright Cyan
        Color hoverColor = new Color(130, 240, 245);  // Lighter Cyan
        Color textColor = new Color(20, 60, 80);      // Dark Blue
        
        // Setup Login Button
        btnFinalRegister.setBackground(primaryColor);
        btnFinalRegister.setForeground(textColor);
        btnFinalRegister.setFocusPainted(false);
        btnFinalRegister.setBorder(null);
        addButtonHoverEffect(btnFinalRegister, primaryColor, hoverColor);

        // Setup Register Button
        btnBackToStep1.setBackground(primaryColor);
        btnBackToStep1.setForeground(textColor);
        btnBackToStep1.setFocusPainted(false);
        btnBackToStep1.setBorder(null);
        addButtonHoverEffect(btnBackToStep1, primaryColor, hoverColor);
    }
    
        private void addButtonHoverEffect(JButton button, Color baseColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    // --- Glow Logic (Matched to Dashboard Style) ---
    private javax.swing.ImageIcon getPinkGlowIcon(javax.swing.ImageIcon icon) {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics g = bi.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    // {Red, Green, Blue, Alpha}
    // High Red + High Blue = Pink/Magenta
    float[] scales = { 2.0f, 0.5f, 1.5f, 1.0f }; 
    float[] offsets = new float[4];
    java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
    return new javax.swing.ImageIcon(op.filter(bi, null));
    }
    
    private javax.swing.ImageIcon getGlowIcon(javax.swing.ImageIcon icon) {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics g = bi.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    // The scale factors: {Red, Green, Blue, Alpha}
    // We boost Blue (1.5f) and Green (1.2f) to create a Cyan/Blue glow effect
    float[] scales = { 0.5f, 0.8f, 2.0f, 1.0f }; 
    float[] offsets = new float[4];
    java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
    return new javax.swing.ImageIcon(op.filter(bi, null));
    }
        
    private void applyGlowEffect(javax.swing.JButton btn, boolean enter) {
        if (enter) {
            // Save original as a property of the button itself
            btn.putClientProperty("origIcon", btn.getIcon());
            btn.setIcon(getGlowIcon((javax.swing.ImageIcon)btn.getIcon()));
            runAnimation(btn, true);
        } else {
            // Retrieve original from the button property
            javax.swing.Icon orig = (javax.swing.Icon)btn.getClientProperty("origIcon");
            if (orig != null) {
                btn.setIcon(orig);
            }
            runAnimation(btn, false);
        }
    }
        
    private void runAnimation(javax.swing.JButton btn, boolean enter) {
        btn.setForeground(enter ? new Color(0, 153, 255) : Color.WHITE);

        Timer timer = new Timer(10, e -> {
            if (enter && btn.getY() > homeOriginalY - 6) {
                btn.setLocation(btn.getX(), btn.getY() - 1);
            } else if (!enter && btn.getY() < homeOriginalY) {
                btn.setLocation(btn.getX(), btn.getY() + 1);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }
    
    // --- Selection Logic ---

    private void selectGender(String gender, JButton source) {
        this.selectedGender = gender;
        
        // 1. Reset both icons to their default state first
        // Note: You'll need to make sure you have the default icons available
        btnMale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Male.png")));
        btnFemale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Female.png")));
        
        // 2. Apply the "Permanent" glow to the selected one
        if (gender.equals("Male")) {
            btnMale.setIcon(getGlowIcon((javax.swing.ImageIcon)btnMale.getIcon()));
        } else if (gender.equals("Female")) {
            btnFemale.setIcon(getPinkGlowIcon((javax.swing.ImageIcon)btnFemale.getIcon()));
        }

        // Keep borders transparent as requested
        btnMale.setBorderPainted(false);
        btnFemale.setBorderPainted(false);
    }   

    private void selectRole(String role, JButton source) {
        this.selectedRole = role;
        JButton[] roles = {btnManager, btnCoach, btnRecep, btnStaff}; 
        for(JButton b : roles) b.setForeground(Color.BLACK);
        source.setForeground(new Color(0, 102, 204));
    }

    private void handleRegistration() {
        if (selectedGender.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Gender!", "Gender Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedRole.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a Role!", "Role Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customID = generateNextOMNIId();
        String username = step1.getUsername();
        String email = step1.getEmail();
        String contact = step1.getContact();
        
        boolean success = Config.registerUser(
            customID, username, step1.getPassword(), 
            email, step1.getContact(), selectedGender,
            selectedRole, "Active", getSalaryForRole(selectedRole)
        );        

        if (success) {
            // Instead of just going to login, show the ID Card
            dashboard.showIDCard(customID, username, selectedRole, email, contact);
        }
    }

    private double getSalaryForRole(String role) {
        switch (role) {
            case "GYM Manager": return 30000.00;
            case "Coach/Instructor": return 25000.00;
            case "Receptionist": return 15000.00;
            case "Staff": return 10000.00;
            default: return 0.00;
        }
    }

    private String generateNextOMNIId() {
        String lastId = Config.getLastUserID(); 
        int nextNumber = (lastId != null && lastId.startsWith("OMNI-00-")) 
                         ? Integer.parseInt(lastId.substring(8)) + 1 : 1001;
        return "OMNI-00-" + nextNumber;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblGenderHeader = new javax.swing.JLabel();
        btnFemale = new javax.swing.JButton();
        btnRecep = new javax.swing.JButton();
        btnStaff = new javax.swing.JButton();
        btnMale = new javax.swing.JButton();
        lblRoleHeader = new javax.swing.JLabel();
        btnManager = new javax.swing.JButton();
        btnCoach = new javax.swing.JButton();
        btnFinalRegister = new javax.swing.JButton();
        btnBackToStep1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblGenderHeader.setText("Gender:");
        add(lblGenderHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 100, 20));

        btnFemale.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnFemale.setForeground(new java.awt.Color(255, 255, 255));
        btnFemale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Female.png"))); // NOI18N
        btnFemale.setBorderPainted(false);
        btnFemale.setContentAreaFilled(false);
        btnFemale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFemale.setFocusPainted(false);
        btnFemale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnFemaleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnFemaleMouseExited(evt);
            }
        });
        btnFemale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFemaleActionPerformed(evt);
            }
        });
        add(btnFemale, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 90, 70, 50));

        btnRecep.setText("RECEPTIONIST");
        btnRecep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRecepActionPerformed(evt);
            }
        });
        add(btnRecep, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 210, 80, 30));

        btnStaff.setText("STAFF");
        btnStaff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStaffActionPerformed(evt);
            }
        });
        add(btnStaff, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 210, 80, 30));

        btnMale.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnMale.setForeground(new java.awt.Color(255, 255, 255));
        btnMale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Male.png"))); // NOI18N
        btnMale.setBorderPainted(false);
        btnMale.setContentAreaFilled(false);
        btnMale.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnMale.setFocusPainted(false);
        btnMale.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnMaleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnMaleMouseExited(evt);
            }
        });
        btnMale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMaleActionPerformed(evt);
            }
        });
        add(btnMale, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 90, 70, 50));

        lblRoleHeader.setText("Select Position:");
        add(lblRoleHeader, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, 100, 20));

        btnManager.setText("Manager");
        btnManager.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManagerActionPerformed(evt);
            }
        });
        add(btnManager, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 170, 80, 30));

        btnCoach.setText("Coach");
        btnCoach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCoachActionPerformed(evt);
            }
        });
        add(btnCoach, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 170, 80, 30));

        btnFinalRegister.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnFinalRegister.setText("REGISTER NOW");
        btnFinalRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFinalRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalRegisterActionPerformed(evt);
            }
        });
        add(btnFinalRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 230, 170, 40));

        btnBackToStep1.setText("← Back to Info");
        btnBackToStep1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBackToStep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackToStep1ActionPerformed(evt);
            }
        });
        add(btnBackToStep1, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 280, 130, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LoginBB.png"))); // NOI18N
        jLabel5.setToolTipText("");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -10, 600, 380));
    }// </editor-fold>//GEN-END:initComponents

    private void btnMaleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMaleMouseEntered
        if (!selectedGender.equals("Male")) {
            btnMale.setIcon(getGlowIcon((javax.swing.ImageIcon)btnMale.getIcon()));
        }
    }//GEN-LAST:event_btnMaleMouseEntered

    private void btnMaleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMaleMouseExited
        if (!selectedGender.equals("Male")) {
            btnMale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Male.png")));
        }
    }//GEN-LAST:event_btnMaleMouseExited

    private void btnMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaleActionPerformed
        selectGender("Male", btnMale);
    }//GEN-LAST:event_btnMaleActionPerformed

    private void btnFemaleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFemaleMouseEntered
        if (!selectedGender.equals("Female")) {
            btnFemale.setIcon(getPinkGlowIcon((javax.swing.ImageIcon)btnFemale.getIcon()));
        }
    }//GEN-LAST:event_btnFemaleMouseEntered

    private void btnFemaleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFemaleMouseExited
        if (!selectedGender.equals("Female")) {
            btnFemale.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Female.png")));
        }
    }//GEN-LAST:event_btnFemaleMouseExited

    private void btnFemaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFemaleActionPerformed
        selectGender("Female", btnFemale);
    }//GEN-LAST:event_btnFemaleActionPerformed

    private void btnRecepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRecepActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRecepActionPerformed

    private void btnStaffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStaffActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnStaffActionPerformed


    // (Remaining ActionListeners for Manager, Coach, etc. remain as you had them)
    private void btnManagerActionPerformed(java.awt.event.ActionEvent evt) { selectRole("GYM Manager", btnManager); }
    private void btnCoachActionPerformed(java.awt.event.ActionEvent evt) { selectRole("Coach/Instructor", btnCoach); }

    
    private void btnFinalRegisterActionPerformed(java.awt.event.ActionEvent evt) { handleRegistration(); }
    private void btnBackToStep1ActionPerformed(java.awt.event.ActionEvent evt) { dashboard.showRegistrationStep1(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackToStep1;
    private javax.swing.JButton btnCoach;
    private javax.swing.JButton btnFemale;
    private javax.swing.JButton btnFinalRegister;
    private javax.swing.JButton btnMale;
    private javax.swing.JButton btnManager;
    private javax.swing.JButton btnRecep;
    private javax.swing.JButton btnStaff;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblGenderHeader;
    private javax.swing.JLabel lblRoleHeader;
    // End of variables declaration//GEN-END:variables
}