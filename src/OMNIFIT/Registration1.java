package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class Registration1 extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    
    // Button hover animation variables
    private float nextButtonGlow = 0.0f;
    private float backButtonGlow = 0.0f;
    private float containerPulse = 0.0f;
    
    // Swipe animation variables
    private float swipeProgress = 0.0f;
    private boolean isSwiping = false;
    private int currentImage = 1; // 1, 2, 3, or 4
    
    private Timer animationTimer;
    private Timer swipeTimer;
    private long lastFrameTime = System.nanoTime();

    public Registration1(DashboardForm parent) {
        this.dashboard = parent;
        initComponents();
        customInit();
    }
    
    private void customInit() {
        this.setOpaque(false);
        this.setBackground(new Color(0, 0, 0, 0));
        
        addPlaceholder(txtName, "Enter username");
        addPlaceholder(txtEmail, "Enter email");
        addPasswordPlaceholder(txtPassword, "Enter password");
        addPasswordPlaceholder(txtConfirmPassword, "Confirm password");
        addPlaceholder(txtContact, "09XXXXXXXXX");
        
        // Style the buttons with hover effects
        styleButtons();
        
        // Start animation timer for button glow
        startAnimationTimer();
        
        // Start swipe animation
        startSwipeAnimation();
        
        // Set initial visibility
        Swipe1.setVisible(true);
        Swipe2.setVisible(false);
        Swipe3.setVisible(false);
        Swipe4.setVisible(false);
        
        // Hide jLabel5 background
        jLabel5.setVisible(true);
    }
    
    private void startSwipeAnimation() {
        swipeTimer = new Timer(2500, e -> {
            if (!isSwiping) {
                isSwiping = true;
                swipeProgress = 0.0f;
                
                final long startTime = System.nanoTime();
                final float duration = 0.4f;
                
                Timer swipeAnimTimer = new Timer(16, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        long currentTime = System.nanoTime();
                        float elapsed = (currentTime - startTime) / 1_000_000_000.0f;
                        swipeProgress = Math.min(1.0f, elapsed / duration);
                        
                        if (swipeProgress >= 1.0f) {
                            swipeProgress = 1.0f;
                            ((Timer)evt.getSource()).stop();
                            isSwiping = false;
                            
                            // Hide all images
                            Swipe1.setVisible(false);
                            Swipe2.setVisible(false);
                            Swipe3.setVisible(false);
                            Swipe4.setVisible(false);
                            
                            // Show next image
                            if (currentImage == 1) {
                                currentImage = 2;
                                Swipe2.setVisible(true);
                            } else if (currentImage == 2) {
                                currentImage = 3;
                                Swipe3.setVisible(true);
                            } else if (currentImage == 3) {
                                currentImage = 4;
                                Swipe4.setVisible(true);
                            } else {
                                currentImage = 1;
                                Swipe1.setVisible(true);
                            }
                            repaint();
                        }
                        repaint();
                    }
                });
                swipeAnimTimer.start();
            }
        });
        swipeTimer.start();
    }
    
    private void styleButtons() {
        styleButton(btnNext, "Next Page →", 0);
        styleButton(btnBack, "Back to Login", 1);
    }
    
    private void styleButton(JButton button, String text, int index) {
        button.setText(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 12));
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
                    
                    if (idx == 0) {
                        nextButtonGlow = current;
                    } else {
                        backButtonGlow = current;
                    }
                    
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
                
                // Draw button background
                GradientPaint gradient;
                if (index == 0) {
                    gradient = new GradientPaint(
                        0, 0, new Color(0, 150, 200),
                        w, 0, new Color(0, 200, 255)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(255, 100, 0),
                        w, 0, new Color(255, 150, 0)
                    );
                }
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 8, 8);
                
                // Draw glow on hover
                if (hover > 0.01f) {
                    float pulse = 1.0f + 0.2f * (float)Math.sin(containerPulse * 3);
                    int alpha = (int)(180 * hover * pulse);
                    g2.setColor(new Color(255, 255, 255, alpha));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
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
    
    private void addPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            
            @Override
            public void focusLost(FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    private void addPasswordPlaceholder(final JPasswordField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('●');
                }
            }
            
            @Override
            public void focusLost(FocusEvent evt) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }
    
    private void startAnimationTimer() {
        animationTimer = new Timer(16, e -> {
            long currentTime = System.nanoTime();
            float deltaTime = (currentTime - lastFrameTime) / 1_000_000_000.0f;
            lastFrameTime = currentTime;
            
            // Update container pulse for button glow animation
            containerPulse += deltaTime * 3;
            
            // Repaint buttons to update glow
            btnNext.repaint();
            btnBack.repaint();
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
    
    public void clearFields() {
        txtName.setText("Enter username");
        txtName.setForeground(Color.GRAY);
        
        txtEmail.setText("Enter email");
        txtEmail.setForeground(Color.GRAY);
        
        txtPassword.setText("Enter password");
        txtPassword.setForeground(Color.GRAY);
        txtPassword.setEchoChar((char) 0);
        
        txtConfirmPassword.setText("Confirm password");
        txtConfirmPassword.setForeground(Color.GRAY);
        txtConfirmPassword.setEchoChar((char) 0);
        
        txtContact.setText("09XXXXXXXXX");
        txtContact.setForeground(Color.GRAY);
    }
    
    public boolean validateFields() {
        String username = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());
        String contact = txtContact.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() ||
            username.equals("Enter username") || email.equals("Enter email") || 
            password.equals("Enter password") || contact.equals("09XXXXXXXXX")) {
            showMessage("ERROR", "Please fill all fields!", true);
            return false;
        }

        if (!username.matches("^[a-zA-Z\\s]{1,12}$")) {
            showMessage("INVALID NAME", "Name must be text only and max 12 characters!", true);
            return false;
        }

        if (!email.contains("@")) {
            showMessage("INVALID EMAIL", "Email must contain @ symbol!", true);
            return false;
        }

        if (!contact.matches("^\\d{11}$")) {
            showMessage("INVALID CONTACT", "Contact must be exactly 11 digits!", true);
            return false;
        }
        
        if (Config.isUserExists(username)) {
            showMessage("USERNAME TAKEN", "Username '" + username + "' is already taken!", true);
            return false;
        }

        if (Config.isEmailExists(email)) {
            showMessage("EMAIL TAKEN", "Email '" + email + "' is already registered!", true);
            return false;
        }

        if (!password.equals(confirm)) {
            showMessage("PASSWORD MISMATCH", "Passwords do not match!", true);
            return false;
        }

        return true;
    }
    
    private void showMessage(String title, String message, boolean isError) {
        JDialog dialog = new JDialog();
        dialog.setUndecorated(true);
        dialog.setSize(280, 150);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(isError ? Color.RED : Color.GREEN, 2));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        titleLabel.setForeground(isError ? Color.RED : Color.GREEN);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        
        JLabel msgLabel = new JLabel(message, SwingConstants.CENTER);
        msgLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        msgLabel.setForeground(Color.WHITE);
        msgLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(msgLabel, BorderLayout.CENTER);
        
        dialog.add(panel);
        dialog.setVisible(true);
        
        new Timer(1500, e -> dialog.dispose()).start();
    }

    public String getUsername() { 
        String username = txtName.getText().trim();
        return username.equals("Enter username") ? "" : username;
    }
    
    public String getEmail() { 
        String email = txtEmail.getText().trim();
        return email.equals("Enter email") ? "" : email;
    }
    
    public String getPassword() { 
        String password = new String(txtPassword.getPassword());
        return password.equals("Enter password") ? "" : password;
    }
    
    public String getContact() { 
        String contact = txtContact.getText().trim();
        return contact.equals("09XXXXXXXXX") ? "" : contact;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (isSwiping) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            int w = Swipe1.getWidth();
            int h = Swipe1.getHeight();
            int x = Swipe1.getX();
            int y = Swipe1.getY();
            
            float progress = Math.min(1.0f, Math.max(0.0f, swipeProgress));
            int offset = (int)(w * progress);
            
            // Get current and next images
            JLabel currentImgLabel = null;
            JLabel nextImgLabel = null;
            
            if (currentImage == 1) { 
                currentImgLabel = Swipe1; 
                nextImgLabel = Swipe2; 
            } else if (currentImage == 2) { 
                currentImgLabel = Swipe2; 
                nextImgLabel = Swipe3; 
            } else if (currentImage == 3) { 
                currentImgLabel = Swipe3; 
                nextImgLabel = Swipe4; 
            } else if (currentImage == 4) { 
                currentImgLabel = Swipe4; 
                nextImgLabel = Swipe1; 
            }
            
            Image currentImg = currentImgLabel != null && currentImgLabel.getIcon() != null ? 
                ((ImageIcon)currentImgLabel.getIcon()).getImage() : null;
            Image nextImg = nextImgLabel != null && nextImgLabel.getIcon() != null ? 
                ((ImageIcon)nextImgLabel.getIcon()).getImage() : null;
            
            if (currentImg != null && nextImg != null) {
                // Current image slides LEFT out
                g2.drawImage(currentImg, x - offset, y, w, h, null);
                // Next image slides RIGHT in from the right edge
                g2.drawImage(nextImg, x + (w - offset), y, w, h, null);
            }
            
            g2.dispose();
        }
    }
    
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
        Swipe1 = new javax.swing.JLabel();
        Swipe2 = new javax.swing.JLabel();
        Swipe3 = new javax.swing.JLabel();
        Swipe4 = new javax.swing.JLabel();
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

        Swipe1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe1.png"))); // NOI18N
        add(Swipe1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe2.png"))); // NOI18N
        add(Swipe2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe3.png"))); // NOI18N
        add(Swipe3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe4.png"))); // NOI18N
        add(Swipe4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

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
    private javax.swing.JLabel Swipe1;
    private javax.swing.JLabel Swipe2;
    private javax.swing.JLabel Swipe3;
    private javax.swing.JLabel Swipe4;
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