package OMNIFIT;

import Config.Config;
import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;


public class Registration1 extends javax.swing.JPanel {

    private final DashboardForm dashboard;

    public Registration1(DashboardForm parent) {
        this.dashboard = parent;
        initComponents();
        customInit();
    }

    private void customInit() {
        this.setOpaque(false);
        btnNext.setContentAreaFilled(true);
        btnBack.setContentAreaFilled(true);
        
        styleTextField(txtConfirmPassword);
        styleTextField(txtContact);
        styleTextField(txtEmail);
        styleTextField(txtName);
        styleTextField(txtPassword);
        
        Color primaryColor = new Color(92, 225, 230); // Bright Cyan
        Color hoverColor = new Color(130, 240, 245);  // Lighter Cyan
        Color textColor = new Color(20, 60, 80);      // Dark Blue
        
        // Setup Login Button
        btnNext.setBackground(primaryColor);
        btnNext.setForeground(textColor);
        btnNext.setFocusPainted(false);
        btnNext.setBorder(null);
        addButtonHoverEffect(btnNext, primaryColor, hoverColor);

        // Setup Register Button
        btnBack.setBackground(primaryColor);
        btnBack.setForeground(textColor);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(null);
        addButtonHoverEffect(btnBack, primaryColor, hoverColor);
        
        
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
        
        private void styleTextField(JTextField field) {
        field.setBackground(new Color(174, 179, 184));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    public boolean validateFields() {
        String username = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());
        String contact = txtContact.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Missing Info", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        if (!username.matches("^[a-zA-Z\\s]{1,12}$")) {
            JOptionPane.showMessageDialog(this, "Name must be text only and max 12 characters!", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address (must contain @)!", "Invalid Email", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        if (!contact.matches("^\\d{11}$")) {
            JOptionPane.showMessageDialog(this, "Contact must be exactly 11 digits!", "Invalid Contact", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (Config.isUserExists(username)) {
            JOptionPane.showMessageDialog(this, "Username '" + username + "' is already taken!", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (Config.isEmailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email '" + email + "' is already registered!", "Registration Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // 5. Password Match Check
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public String getUsername() { return txtName.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public String getContact() { return txtContact.getText().trim(); }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        lblConfirm = new javax.swing.JLabel();
        txtConfirmPassword = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        txtContact = new javax.swing.JTextField();
        btnNext = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setText("Username:");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 50, 100, 20));
        add(txtName, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 70, 180, 30));

        jLabel2.setText("Email:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 100, 100, 20));
        add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, 180, 30));

        jLabel3.setText("Password:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 150, 100, 20));
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 170, 180, 30));

        lblConfirm.setText("Confirm:");
        add(lblConfirm, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 200, 100, 20));
        add(txtConfirmPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 220, 180, 30));

        jLabel4.setText("Contact:");
        add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 250, 100, -1));

        txtContact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtContactActionPerformed(evt);
            }
        });
        add(txtContact, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 270, 180, 30));

        btnNext.setText("Next Page →");
        btnNext.setBorder(null);
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });
        add(btnNext, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 280, 110, 30));

        btnBack.setText("Back to Login");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 280, 110, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LoginBB.png"))); // NOI18N
        jLabel5.setToolTipText("");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -10, 600, 380));
    }// </editor-fold>//GEN-END:initComponents

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        if(validateFields()) dashboard.showRegistrationStep2();
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        dashboard.showLogin();
    }//GEN-LAST:event_btnBackActionPerformed

    private void txtContactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtContactActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtContactActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnNext;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblConfirm;
    private javax.swing.JPasswordField txtConfirmPassword;
    private javax.swing.JTextField txtContact;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtName;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}