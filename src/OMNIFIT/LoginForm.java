package OMNIFIT;

import Config.Config;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginForm extends javax.swing.JPanel {

    private final DashboardForm dashboard;

    public LoginForm(DashboardForm parent) {
        this.dashboard = parent;        
        Config.initializeDB();
        initComponents();
        customInit(); 
    }

private void customInit() {
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(500, 500));
        
        styleTextField(txtUsername);
        styleTextField(txtPassword);
        
        
        // Define our theme colors
        Color primaryColor = new Color(92, 225, 230); // Bright Cyan
        Color hoverColor = new Color(130, 240, 245);  // Lighter Cyan
        Color textColor = new Color(20, 60, 80);      // Dark Blue
        
        // Setup Login Button
        btnLogin.setBackground(primaryColor);
        btnLogin.setForeground(textColor);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(null);
        addButtonHoverEffect(btnLogin, primaryColor, hoverColor);

        // Setup Register Button
        btnRegister.setBackground(primaryColor);
        btnRegister.setForeground(textColor);
        btnRegister.setFocusPainted(false);
        btnRegister.setBorder(null);
        addButtonHoverEffect(btnRegister, primaryColor, hoverColor);
        
        person.setVisible(false);
        lock.setVisible(false);
        
        styleTextField(txtUsername, "/image/person.png");
        styleTextField(txtPassword, "/image/lock.png");
        
        person.setOpaque(false);
        lock.setOpaque(false);
        
        // Layers for icons
        this.setComponentZOrder(person, 0);
        this.setComponentZOrder(lock, 0);
        this.setComponentZOrder(txtUsername, 1);
        this.setComponentZOrder(txtPassword, 1);
    }
        private void styleTextField(JTextField field, String iconPath) {
        field.setBackground(new Color(174, 179, 184));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(new EmptyBorder(5, 35, 5, 10));
        // Get the icon image
        ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
        
        // We override the field's painting to draw the icon inside it
        field.setUI(new javax.swing.plaf.basic.BasicTextFieldUI() {
            @Override
            protected void paintBackground(Graphics g) {
                super.paintBackground(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Draw icon at X=8, and Y centered (field height is 45, icon is usually 20-30)
                int y = (field.getHeight() - icon.getIconHeight()) / 2;
                g2.drawImage(icon.getImage(), 8, y, null);
            }
        });
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
        field.setBackground(new Color(30, 45, 60));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.dispose();
        super.paintComponent(g);
    }
    
    private void handleLoginProcess() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String role = Config.getUserRole(username, password);

        if (role != null) {            
            new Thread(() -> {
                java.awt.EventQueue.invokeLater(() -> { repaint(); });
                try { Thread.sleep(300); } catch (InterruptedException e) {}
                java.awt.EventQueue.invokeLater(() -> {
                    dashboard.loginSuccess(username, role); 
                });
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lock = new javax.swing.JLabel();
        person = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnRegister = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/lock.png"))); // NOI18N
        add(lock, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 20, 20));

        person.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/person.png"))); // NOI18N
        add(person, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 70, 20, 20));
        add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 190, 40));

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, 190, 40));

        btnRegister.setText("REGISTER");
        btnRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegisterActionPerformed(evt);
            }
        });
        add(btnRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 240, 130, 30));

        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 200, 130, 30));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LoginB.png"))); // NOI18N
        jLabel1.setToolTipText("");
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -10, 600, 380));
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        handleLoginProcess();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegisterActionPerformed
        dashboard.showRegistrationStep1();
    }//GEN-LAST:event_btnRegisterActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lock;
    private javax.swing.JLabel person;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
    // GEN-FIRST:event_btnUserMouseExited 
    // TODO add your handling code here: 
    // }                                    
}