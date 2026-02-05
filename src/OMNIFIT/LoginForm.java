package OMNIFIT;

import Config.Config;
import javax.swing.JOptionPane;

public class LoginForm extends javax.swing.JPanel {

    private final DashboardForm dashboard;

    public LoginForm(DashboardForm parent) {
        this.dashboard = parent;        
        // Ensure DB is ready for queries
        Config.initializeDB();
        initComponents();
        customInit(); 
    }

    private void customInit() {
        this.setOpaque(false);
    }

    private void handleLoginProcess() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Logic: Config.getUserRole should query the Users table 
        // and return the Role from the Management table linked by Username.
        String role = Config.getUserRole(username, password);

        if (role != null) {            
            new Thread(() -> {
                java.awt.EventQueue.invokeLater(() -> { 
                    repaint(); 
                });
                
                try { Thread.sleep(300); } catch (InterruptedException e) {}

                java.awt.EventQueue.invokeLater(() -> {
                    // Success: Passes the role (e.g., "Administrator" or "Chef") to the dashboard
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

        jLabel2 = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnGoToRegister = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Username:");
        add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 70, 140, 40));
        add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 80, 170, 30));

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Password:");
        add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 120, 140, 40));
        add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 170, 30));

        btnGoToRegister.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btnGoToRegister.setText("REGISTER");
        btnGoToRegister.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnGoToRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGoToRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoToRegisterActionPerformed(evt);
            }
        });
        add(btnGoToRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 250, 160, 40));

        btnLogin.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        btnLogin.setText("LOGIN");
        btnLogin.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnLogin.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 200, 160, 40));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/log1.png"))); // NOI18N
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 680, 360));
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoToRegisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoToRegisterActionPerformed
        dashboard.showRegister();                  
    }//GEN-LAST:event_btnGoToRegisterActionPerformed

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        handleLoginProcess();
    }//GEN-LAST:event_btnLoginActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGoToRegister;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}