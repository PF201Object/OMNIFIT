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
                
        
        // Define our theme colors
        Color primaryColor = new Color(92, 225, 230); // Bright Cyan
        Color hoverColor = new Color(130, 240, 245);  // Lighter Cyan
        Color textColor = new Color(20, 60, 80);      // Dark Blue
        
        addPlaceholder(txtUsername, "Username or Email");
        addPlaceholder(txtPassword1, "Password");
        
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
        
        isPasswordVisible = false; 
        eyeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        person.setVisible(true);
        lock.setVisible(true);
                
        person.setOpaque(false);
        lock.setOpaque(false);
        
        setupPasswordToggle();
        
        // Layers for icons
        this.setComponentZOrder(eyeToggle, 0);
        this.setComponentZOrder(Border, 1);
        this.setComponentZOrder(person, 0);
        this.setComponentZOrder(lock, 0);
        this.setComponentZOrder(txtUsername, 1);
    }

    public void clearFields() {
    // Reset Username
    txtUsername.setText("Username or Email");
    txtUsername.setForeground(Color.WHITE);

    // Reset Password
    txtPassword1.setText("Password");
    txtPassword1.setForeground(Color.WHITE);
    txtPassword1.setEchoChar((char) 0); // Make the placeholder visible
    
    // Reset the visibility flag
    isPasswordVisible = false;
}

        private boolean isPasswordVisible = false;
         
    
private void setupPasswordToggle() {
    eyeToggle.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            // Ignore if it's just the placeholder
            if (new String(txtPassword1.getPassword()).equals("Password")) return;

            if (isPasswordVisible) {
                txtPassword1.setEchoChar('●');
                isPasswordVisible = false;
                // You can swap the icon here if you have a "Hidden" version
                // eyeToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/EyeHidden.png")));
            } else {
                txtPassword1.setEchoChar((char) 0);
                isPasswordVisible = true;
                // eyeToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/EyeToggle.png")));
            }
        }
    });
}

private void addPlaceholder(final JTextField field, final String placeholder) {
    field.setText(placeholder);
    field.setForeground(Color.WHITE); 

    if (field instanceof JPasswordField) {
        ((JPasswordField) field).setEchoChar((char) 0);
    }

    field.addFocusListener(new java.awt.event.FocusAdapter() {
        @Override
        public void focusGained(java.awt.event.FocusEvent evt) {
            if (field.getText().equals(placeholder)) {
                field.setText("");
                if (field instanceof JPasswordField) {
                    ((JPasswordField) field).setEchoChar('●');
                    isPasswordVisible = false;
                }
            }
        }

        @Override
        public void focusLost(java.awt.event.FocusEvent evt) {
            if (field.getText().isEmpty()) {
                field.setText(placeholder);
                if (field instanceof JPasswordField) {
                    ((JPasswordField) field).setEchoChar((char) 0);
                }
            }
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
        // 'identifier' can now be either the Username or the Email address
        final String identifier = txtUsername.getText().trim();
        String password = new String(txtPassword1.getPassword());

        if (identifier.isEmpty() || password.isEmpty()) {
            
            JOptionPane.showMessageDialog(this, "Please enter credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call the updated Config method
        final String role = Config.getUserRole(identifier, password);

        if (role != null) {            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() { repaint(); }
                    });

                    try { Thread.sleep(300); } catch (InterruptedException e) {}

                    java.awt.EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            // Pass the login details to the dashboard
                            dashboard.loginSuccess(identifier, role); 
                        }
                    });
                }
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username/Email or Password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        eyeToggle = new javax.swing.JLabel();
        lock = new javax.swing.JLabel();
        person = new javax.swing.JLabel();
        txtPassword1 = new javax.swing.JPasswordField();
        txtUsername = new javax.swing.JTextField();
        Border1 = new javax.swing.JPanel();
        Border = new javax.swing.JPanel();
        btnRegister = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        eyeToggle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/EyeToggle.png"))); // NOI18N
        add(eyeToggle, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 130, -1, 20));

        lock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/lock.png"))); // NOI18N
        add(lock, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 130, 20, 20));

        person.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/person.png"))); // NOI18N
        add(person, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 70, 20, 20));

        txtPassword1.setBackground(new java.awt.Color(102, 102, 102));
        txtPassword1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtPassword1.setBorder(null);
        add(txtPassword1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 110, 40));

        txtUsername.setBackground(new java.awt.Color(102, 102, 102));
        txtUsername.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtUsername.setBorder(null);
        txtUsername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUsernameActionPerformed(evt);
            }
        });
        add(txtUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 60, 150, 40));

        Border1.setBackground(new java.awt.Color(102, 102, 102));
        add(Border1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 60, 190, 40));

        Border.setBackground(new java.awt.Color(102, 102, 102));
        add(Border, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 120, 190, 40));

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

    private void txtUsernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUsernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUsernameActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Border;
    private javax.swing.JPanel Border1;
    private javax.swing.JButton btnLogin;
    private javax.swing.JButton btnRegister;
    private javax.swing.JLabel eyeToggle;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lock;
    private javax.swing.JLabel person;
    private javax.swing.JPasswordField txtPassword1;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
                                   
}