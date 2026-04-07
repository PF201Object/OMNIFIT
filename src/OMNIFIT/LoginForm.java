package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class LoginForm extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    
    // Animation variables
    private float formOpacity = 0.0f;
    private float loginButtonGlow = 0.0f;
    private float registerButtonGlow = 0.0f;
    private float[] fieldGlowIntensity = new float[2];
    private float[] fieldPulsePhase = new float[2];
    private float containerPulse = 0.0f;
    private float scanlineOffset = 0.0f;
    
    
    // Modern sci-fi color scheme
    private final Color transparentDark = new Color(10, 20, 30, 200);
    private final Color transparentMedium = new Color(20, 40, 60, 200);
    private final Color accentCyan = new Color(0, 255, 255, 220);
    private final Color accentOrange = new Color(255, 120, 0, 220);
    private final Color textLight = new Color(220, 240, 255);
    
    // UI Components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel containerPanel;
    private JPanel usernamePanel;
    private JPanel passwordPanel;
    private JLabel usernameIcon;
    private JLabel passwordIcon;
    private JLabel eyeIcon;
    private Timer animationTimer;
    private long lastFrameTime = System.nanoTime();
    private java.util.List<Particle> particles = new java.util.ArrayList<>();

    public LoginForm(DashboardForm parent) {
        this.dashboard = parent;        
        Config.initializeDB();
        initComponents();
        customInit(); 
        initParticles();
    }

    private void customInit() {
        this.setOpaque(false);
        this.setLayout(null);
        this.setBounds(220, 30, 570, 350); // Match Dashboard panel size
        
        // Initialize field glow arrays
        for (int i = 0; i < 2; i++) {
            fieldGlowIntensity[i] = 0.0f;
            fieldPulsePhase[i] = 0.0f;
        }
        
        // Create custom styled components
        createCustomComponents();
        
        // Add placeholders
        addPlaceholder(txtUsername, "Username or Email");
        addPlaceholder(txtPassword1, "Password");
        
        // Style the container panel
        styleContainer();
        
        // Setup password toggle
        setupPasswordToggle();
        
        // Add hover effects
        setupHoverEffects();
        
        // Set component Z-order
        setupZOrder();
        
        // Make old components invisible
        makeOldComponentsInvisible();
    }
    
    private void makeOldComponentsInvisible() {
        lock.setVisible(false);
        person.setVisible(false);
        Border.setVisible(false);
        Border1.setVisible(false);
        eyeToggle.setVisible(false);
    }
    
    private void createCustomComponents() {
        // Create title label
        titleLabel = new JLabel("OMNIFIT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = "OMNIFIT";
                Font font = new Font("Orbitron", Font.BOLD, 36);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                
                // 3D text effect
                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(0, 255, 255, 50 - i * 10));
                    g2.drawString(text, x - i, y - i);
                }
                
                // Main text gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentCyan,
                    getWidth(), 0, accentOrange
                );
                g2.setPaint(gradient);
                g2.drawString(text, x, y);
                
                g2.dispose();
            }
        };
        titleLabel.setBounds(150, 15, 270, 50);
        titleLabel.setOpaque(false);
        this.add(titleLabel);
        
        // Create subtitle
        subtitleLabel = new JLabel("FITNESS ACCESS TERMINAL");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(accentOrange);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(150, 55, 270, 20);
        subtitleLabel.setOpaque(false);
        this.add(subtitleLabel);
        
        
        // Create container panel for form
        containerPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw semi-transparent background
                g2.setColor(new Color(15, 25, 35, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw border with gradient
                float pulse = 0.7f + 0.3f * (float)Math.sin(containerPulse * 2);
                int borderAlpha = (int)(150 * pulse);
                
                GradientPaint borderGradient = new GradientPaint(
                    0, 0, new Color(0, 255, 255, borderAlpha),
                    getWidth(), 0, new Color(255, 120, 0, borderAlpha)
                );
                g2.setPaint(borderGradient);
                g2.setStroke(new BasicStroke(2.0f));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Draw corner accents
                drawCornerAccents(g2, getWidth(), getHeight());
                
                g2.dispose();
            }
        };
        containerPanel.setBounds(150, 80, 270, 200);
        containerPanel.setOpaque(false);
        this.add(containerPanel);
        
        // Create username panel
        usernamePanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2.setColor(new Color(30, 45, 60, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw glow effect on focus
                float glow = fieldGlowIntensity[0];
                if (glow > 0.01f) {
                    float pulseGlow = 1.0f + 0.2f * (float)Math.sin(fieldPulsePhase[0] * 3);
                    int alpha = (int)(100 * glow * pulseGlow);
                    g2.setColor(new Color(0, 255, 255, alpha));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                }
                
                // Draw bottom line
                g2.setColor(new Color(0, 255, 255, 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(5, getHeight() - 2, getWidth() - 5, getHeight() - 2);
                
                g2.dispose();
            }
        };
        usernamePanel.setBounds(20, 30, 230, 40);
        usernamePanel.setOpaque(false);
        containerPanel.add(usernamePanel);
        
        // Create username icon
        usernameIcon = new JLabel(createIcon('U', 16));
        usernameIcon.setBounds(8, 10, 20, 20);
        usernamePanel.add(usernameIcon);
        
        // Reposition username field
        txtUsername.setBounds(32, 5, 185, 30);
        txtUsername.setOpaque(false);
        txtUsername.setBorder(null);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtUsername.setForeground(textLight);
        txtUsername.setCaretColor(accentCyan);
        usernamePanel.add(txtUsername);
        
        // Create password panel
        passwordPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                g2.setColor(new Color(30, 45, 60, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Draw glow effect on focus
                float glow = fieldGlowIntensity[1];
                if (glow > 0.01f) {
                    float pulseGlow = 1.0f + 0.2f * (float)Math.sin(fieldPulsePhase[1] * 3);
                    int alpha = (int)(100 * glow * pulseGlow);
                    g2.setColor(new Color(255, 120, 0, alpha));
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                }
                
                // Draw bottom line
                g2.setColor(new Color(255, 120, 0, 100));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(5, getHeight() - 2, getWidth() - 5, getHeight() - 2);
                
                g2.dispose();
            }
        };
        passwordPanel.setBounds(20, 80, 230, 40);
        passwordPanel.setOpaque(false);
        containerPanel.add(passwordPanel);
        
        // Create password icon
        passwordIcon = new JLabel(createIcon('P', 16));
        passwordIcon.setBounds(8, 10, 20, 20);
        passwordPanel.add(passwordIcon);
        
        // Create eye icon
        eyeIcon = new JLabel(createEyeIcon());
        eyeIcon.setBounds(200, 10, 20, 20);
        eyeIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        passwordPanel.add(eyeIcon);
        
        // Reposition password field
        txtPassword1.setBounds(32, 5, 165, 30);
        txtPassword1.setOpaque(false);
        txtPassword1.setBorder(null);
        txtPassword1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtPassword1.setForeground(textLight);
        txtPassword1.setCaretColor(accentOrange);
        passwordPanel.add(txtPassword1);
    }
    
    private ImageIcon createIcon(char letter, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (letter == 'U') {
            g2.setColor(accentCyan);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(4, 10, 12, 10);
            g2.fillOval(2, 6, 6, 6);
            g2.fillOval(8, 6, 6, 6);
        } else {
            g2.setColor(accentOrange);
            g2.fillOval(2, 2, 12, 12);
            g2.setColor(new Color(30, 45, 60));
            g2.fillOval(5, 5, 6, 6);
        }
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    private ImageIcon createEyeIcon() {
        int size = 20;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2.setColor(accentOrange);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawOval(3, 6, 14, 8);
        g2.drawOval(8, 8, 4, 4);
        
        g2.dispose();
        return new ImageIcon(image);
    }
    
    private class ManAnimationPanel extends JPanel {
        private String position;
        private float liftProgress = 0.0f;
        private float squatProgress = 0.0f;
        
        public ManAnimationPanel(String pos) {
            this.position = pos;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            int centerX = w / 2;
            int groundY = h - 30;
            
            // Draw gym floor
            g2.setColor(new Color(0, 255, 255, 50));
            g2.setStroke(new BasicStroke(1));
            g2.drawLine(10, groundY, w - 10, groundY);
            
            // Calculate animation values
            float bodyY = groundY - 60;
            float armAngle = 0;
            float legAngle = 0;
            float bodyBend = 0;

            // Draw body (torso)
            g2.setColor(accentCyan);
            g2.setStroke(new BasicStroke(3));
            
            // Head
            g2.fillOval(centerX - 8, (int)bodyY - 25, 16, 16);
            g2.setColor(Color.WHITE);
            g2.fillOval(centerX - 4, (int)bodyY - 21, 3, 3);
            g2.fillOval(centerX + 1, (int)bodyY - 21, 3, 3);
            
            // Torso
            g2.setColor(accentCyan);
            g2.drawLine(centerX, (int)bodyY - 9, centerX, (int)bodyY + 15);
            
            // Arms with lifting animation
            int armBaseY = (int)bodyY;
            
            // Left arm
            int leftArmX = centerX - 12;
            int leftArmY = armBaseY - (int)(armAngle / 2);
            g2.drawLine(centerX, armBaseY, leftArmX, leftArmY);
            
            // Right arm
            int rightArmX = centerX + 12;
            int rightArmY = armBaseY - (int)(armAngle / 2);
            g2.drawLine(centerX, armBaseY, rightArmX, rightArmY);
            
            // Hands (weight plates for lifter, empty for squatter)
            if (position.equals("left")) {
                g2.setColor(accentOrange);
                g2.fillOval(leftArmX - 5, leftArmY - 5, 10, 10);
                g2.fillOval(rightArmX - 5, rightArmY - 5, 10, 10);
            }
            
            // Legs with squat animation
            int legBaseY = (int)bodyY + 15;
            
            // Left leg
            int leftLegX = centerX - 8;
            int leftLegY = legBaseY + 15 + (int)(legAngle);
            g2.drawLine(centerX, legBaseY, leftLegX, leftLegY);
            
            // Right leg
            int rightLegX = centerX + 8;
            int rightLegY = legBaseY + 15 + (int)(legAngle);
            g2.drawLine(centerX, legBaseY, rightLegX, rightLegY);
            
            // Feet
            g2.drawLine(leftLegX - 5, leftLegY, leftLegX + 5, leftLegY);
            g2.drawLine(rightLegX - 5, rightLegY, rightLegX + 5, rightLegY);
            
            // Draw barbell for lifter
            if (position.equals("left") && liftProgress > 0) {
                g2.setColor(accentCyan);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(centerX - 25, (int)bodyY - 30 - (int)(liftProgress * 10),
                           centerX + 25, (int)bodyY - 30 - (int)(liftProgress * 10));
                
                // Weight plates on barbell
                g2.setColor(accentOrange);
                g2.fillOval(centerX - 35, (int)bodyY - 35 - (int)(liftProgress * 10), 10, 10);
                g2.fillOval(centerX + 25, (int)bodyY - 35 - (int)(liftProgress * 10), 10, 10);
            }
            
            g2.dispose();
        }
        
        public void updateAnimation() {
            repaint();
        }
    }
    
    private void styleContainer() {
        // Style login button
        styleButton(btnLogin, "LOGIN", 0);
        btnLogin.setBounds(30, 135, 100, 35);
        containerPanel.add(btnLogin);
        
        // Style register button
        styleButton(btnRegister, "REGISTER", 1);
        btnRegister.setBounds(140, 135, 100, 35);
        containerPanel.add(btnRegister);
    }
    
    private void styleButton(JButton button, String text, int index) {
        button.setText(text);
        button.setFont(new Font("Orbitron", Font.BOLD, 12));
        button.setForeground(textLight);
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
                        0, 0, new Color(0, 150, 255, 200),
                        w, 0, new Color(0, 255, 255, 200)
                    );
                } else {
                    gradient = new GradientPaint(
                        0, 0, new Color(255, 120, 0, 200),
                        w, 0, new Color(255, 200, 0, 200)
                    );
                }
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, w, h, 10, 10);
                
                // Draw glow on hover
                if (hover > 0.01f) {
                    float pulse = 1.0f + 0.2f * (float)Math.sin(containerPulse * 3);
                    int alpha = (int)(150 * hover * pulse);
                    g2.setColor(new Color(255, 255, 255, alpha));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
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
        eyeIcon.addMouseListener(new MouseAdapter() {
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
    }
    
    private void setupHoverEffects() {
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                startFieldGlowAnimation(0, true);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                startFieldGlowAnimation(0, false);
            }
        });
        
        txtPassword1.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                startFieldGlowAnimation(1, true);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                startFieldGlowAnimation(1, false);
            }
        });
    }
    
    private void startFieldGlowAnimation(int fieldIndex, boolean enter) {
        Timer glowTimer = new Timer(10, null);
        glowTimer.addActionListener(new ActionListener() {
            float target = enter ? 1.0f : 0.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (enter) {
                    fieldGlowIntensity[fieldIndex] = Math.min(fieldGlowIntensity[fieldIndex] + 0.1f, target);
                } else {
                    fieldGlowIntensity[fieldIndex] = Math.max(fieldGlowIntensity[fieldIndex] - 0.1f, target);
                }
                
                usernamePanel.repaint();
                passwordPanel.repaint();
                
                if (Math.abs(fieldGlowIntensity[fieldIndex] - target) < 0.01f) {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        glowTimer.start();
    }
    
    private void addPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(150, 180, 200));

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(textLight);
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
                    field.setForeground(new Color(150, 180, 200));
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }
    
    private void drawCornerAccents(Graphics2D g2, int w, int h) {
        int size = 10;
        float pulse = 0.6f + 0.4f * (float)Math.sin(containerPulse * 3);
        
        g2.setColor(new Color(0, 255, 255, (int)(100 * pulse)));
        g2.fillOval(5, 5, size, size);
        g2.setColor(new Color(255, 120, 0, (int)(100 * pulse)));
        g2.fillOval(w - size - 5, 5, size, size);
        g2.fillOval(5, h - size - 5, size, size);
        g2.setColor(new Color(0, 255, 255, (int)(100 * pulse)));
        g2.fillOval(w - size - 5, h - size - 5, size, size);
    }
    
    private void initParticles() {
        Timer particleTimer = new Timer(50, e -> {
            if (Math.random() > 0.7) {
                particles.add(new Particle());
            }
            
            java.util.Iterator<Particle> it = particles.iterator();
            while (it.hasNext()) {
                Particle p = it.next();
                p.update();
                if (p.isDead()) {
                    it.remove();
                }
            }
            
            repaint();
        });
        particleTimer.start();
    }
    
    private class Particle {
        float x, y;
        float vx, vy;
        float life;
        float maxLife;
        
        Particle() {
            x = (float)(Math.random() * 570);
            y = (float)(Math.random() * 350);
            vx = (float)(Math.random() - 0.5f) * 2;
            vy = (float)(Math.random() - 0.5f) * 2;
            maxLife = (float)(Math.random() * 100 + 50);
            life = maxLife;
        }
        
        void update() {
            x += vx;
            y += vy;
            life -= 1;
        }
        
        boolean isDead() {
            return life <= 0 || x < 0 || x > 570 || y < 0 || y > 350;
        }
        
        void draw(Graphics2D g2) {
            int alpha = (int)(100 * (life / maxLife));
            g2.setColor(new Color(0, 255, 255, alpha));
            g2.fillOval((int)x, (int)y, 2, 2);
        }
    }
    
    private float getFloatClientProperty(JButton button, String key, float defaultValue) {
        Object value = button.getClientProperty(key);
        if (value instanceof Float) {
            return (Float) value;
        }
        return defaultValue;
    }
    
    private void setupZOrder() {
        this.setComponentZOrder(titleLabel, 0);
        this.setComponentZOrder(subtitleLabel, 1);
        this.setComponentZOrder(containerPanel, 4);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw subtle background gradient (only within this panel)
        int w = getWidth();
        int h = getHeight();
        
        GradientPaint baseGradient = new GradientPaint(
            0, 0, new Color(5, 10, 15, 30),
            w, h, new Color(15, 25, 35, 30)
        );
        g2.setPaint(baseGradient);
        g2.fillRect(0, 0, w, h);
        
        // Draw particles
        for (Particle p : particles) {
            p.draw(g2);
        }
        
        // Draw scanning line
        float scanPos = (scanlineOffset * h) % (h * 2) - h;
        g2.setColor(new Color(0, 255, 255, 20));
        g2.fillRect(0, (int)scanPos, w, 2);
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, formOpacity));
        
        g2.dispose();
        super.paintComponent(g);
    }

private void handleLoginProcess() {       
    final String identifier = txtUsername.getText().trim();
    String password = new String(txtPassword1.getPassword());

    if (identifier.isEmpty() || password.isEmpty() || 
        identifier.equals("Username or Email") || 
        password.equals("Password")) {
        
        showMessage("ACCESS DENIED", "Please enter valid credentials!", true);
        return; // Add return here to prevent further execution
    }

    final String role = Config.getUserRole(identifier, password);

    if (role != null) {
        showMessage("ACCESS GRANTED", "Welcome, " + identifier + "!", false);
        
        // Simply call loginSuccess without any timer that might interfere
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
        dialog.setBackground(new Color(0, 0, 0, 0));
        
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(15, 25, 35, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                Color borderColor = isError ? new Color(255, 80, 80) : new Color(0, 255, 255);
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
                
                g2.setFont(new Font("Orbitron", Font.BOLD, 14));
                FontMetrics fm = g2.getFontMetrics();
                int titleX = (getWidth() - fm.stringWidth(title)) / 2;
                
                g2.setColor(borderColor);
                g2.drawString(title, titleX, 45);
                
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.setColor(Color.WHITE);
                fm = g2.getFontMetrics();
                int msgX = (getWidth() - fm.stringWidth(message)) / 2;
                g2.drawString(message, msgX, 85);
                
                g2.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(280, 130));
        panel.setOpaque(false);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setVisible(true);
        
        new Timer(1500, e -> dialog.dispose()).start();
    }

    public void clearFields() {
        txtUsername.setText("Username or Email");
        txtUsername.setForeground(new Color(150, 180, 200));

        txtPassword1.setText("Password");
        txtPassword1.setForeground(new Color(150, 180, 200));
        txtPassword1.setEchoChar((char) 0);
        
        isPasswordVisible = false;
    }

    private boolean isPasswordVisible = false;
    
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
        add(btnRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 190, 110, 30));

        btnLogin.setText("LOGIN");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });
        add(btnLogin, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 190, 110, 30));
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
    private javax.swing.JLabel lock;
    private javax.swing.JLabel person;
    private javax.swing.JPasswordField txtPassword1;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
                                   
}