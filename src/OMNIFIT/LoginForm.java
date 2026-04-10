package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class LoginForm extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    
    // Button hover animation variables
    private float loginButtonGlow = 0.0f;
    private float registerButtonGlow = 0.0f;
    private float containerPulse = 0.0f;
    
    // Swipe animation variables
    private float swipeProgress = 0.0f;
    private boolean isSwiping = false;
    private int currentImage = 1; // 1, 2, 3, or 4
    
    // Modern color scheme for buttons
    private final Color textLight = new Color(255, 255, 255);
    
    private Timer animationTimer;
    private Timer swipeTimer;
    private long lastFrameTime = System.nanoTime();

    public LoginForm(DashboardForm parent) {
        this.dashboard = parent;        
        Config.initializeDB();
        initComponents();
        customInit();
    }

    private void customInit() {
        // Add placeholders
        addPlaceholder(txtUsername, "Username or Email");
        addPlaceholder(txtPassword1, "Password");
        
        // Setup password toggle
        setupPasswordToggle();
        
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
        styleButton(btnLogin, "LOGIN", 0);
        styleButton(btnRegister, "REGISTER", 1);
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
                        loginButtonGlow = current;
                    } else {
                        registerButtonGlow = current;
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
                
                g2.setColor(textLight);
                g2.drawString(text, textX, textY);
                
                g2.dispose();
            }
        });
    }
    
    private void setupPasswordToggle() {
        eyeToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (new String(txtPassword1.getPassword()).equals("Password")) return;
                
                if (isPasswordVisible) {
                    txtPassword1.setEchoChar('●');
                    isPasswordVisible = false;
                } else {
                    txtPassword1.setEchoChar((char) 0);
                    isPasswordVisible = true;
                }
            }
        });
        eyeToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void addPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●');
                        isPasswordVisible = false;
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
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
            btnLogin.repaint();
            btnRegister.repaint();
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

    private void handleLoginProcess() {       
        final String identifier = txtUsername.getText().trim();
        String password = new String(txtPassword1.getPassword());

        if (identifier.isEmpty() || password.isEmpty() || 
            identifier.equals("Username or Email") || 
            password.equals("Password")) {
            
            showMessage("ACCESS DENIED", "Please enter valid credentials!", true);
            return;
        }

        final String role = Config.getUserRole(identifier, password);

        if (role != null) {
            showMessage("ACCESS GRANTED", "Welcome, " + identifier + "!", false);
            dashboard.loginSuccess(identifier, role);
        } else {
            showMessage("ACCESS DENIED", "Invalid credentials!", true);
        }
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

    public void clearFields() {
        txtUsername.setText("Username or Email");
        txtUsername.setForeground(Color.GRAY);

        txtPassword1.setText("Password");
        txtPassword1.setForeground(Color.GRAY);
        txtPassword1.setEchoChar((char) 0);
        
        isPasswordVisible = false;
    }

    private boolean isPasswordVisible = false;
    
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

        eyeToggle = new javax.swing.JLabel();
        lock = new javax.swing.JLabel();
        person = new javax.swing.JLabel();
        txtPassword1 = new javax.swing.JPasswordField();
        txtUsername = new javax.swing.JTextField();
        Border1 = new javax.swing.JPanel();
        Border = new javax.swing.JPanel();
        btnRegister = new javax.swing.JButton();
        btnLogin = new javax.swing.JButton();
        Swipe2 = new javax.swing.JLabel();
        Swipe1 = new javax.swing.JLabel();
        Swipe3 = new javax.swing.JLabel();
        Swipe4 = new javax.swing.JLabel();
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
        txtPassword1.setForeground(new java.awt.Color(255, 255, 255));
        txtPassword1.setBorder(null);
        add(txtPassword1, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 120, 110, 40));

        txtUsername.setBackground(new java.awt.Color(102, 102, 102));
        txtUsername.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(255, 255, 255));
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
        add(btnRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 190, 100, 30));

        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 190, 100, 30));

        Swipe2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe2.png"))); // NOI18N
        add(Swipe2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe1.png"))); // NOI18N
        add(Swipe1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe3.png"))); // NOI18N
        Swipe3.setLabelFor(Swipe3);
        add(Swipe3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        Swipe4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/swipe4.png"))); // NOI18N
        add(Swipe4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 0, 540, 360));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LoginB.png"))); // NOI18N
        add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 0, 700, 370));
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
    private javax.swing.JLabel Swipe1;
    private javax.swing.JLabel Swipe2;
    private javax.swing.JLabel Swipe3;
    private javax.swing.JLabel Swipe4;
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