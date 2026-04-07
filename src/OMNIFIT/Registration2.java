package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.*;        
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public class Registration2 extends javax.swing.JPanel {
    
    private final DashboardForm dashboard;
    private final Registration1 step1;
    private String selectedRole = "User";
    private String selectedGender = "";
    
    // Animation variables
    private float formOpacity = 0.0f;
    private float registerButtonGlow = 0.0f;
    private float backButtonGlow = 0.0f;
    private float containerPulse = 0.0f;
    private float scanlineOffset = 0.0f;
    private float genderIconPulse = 0.0f;
    private Timer animationTimer;
    private long lastFrameTime = System.nanoTime();
    private java.util.List<Particle> particles = new java.util.ArrayList<>();
    
    
    // Modern sci-fi color scheme
    private final Color accentCyan = new Color(0, 255, 255, 220);
    private final Color accentOrange = new Color(255, 120, 0, 220);
    private final Color accentPink = new Color(255, 80, 150, 220);
    private final Color textLight = new Color(220, 240, 255);
    private final Color fieldBg = new Color(30, 45, 60, 200);
    
    // UI Components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel containerPanel;
    private JLabel leftGymIcon;
    private JLabel rightGymIcon;
    private JLabel centerGymIcon;
    private JLabel genderLabel;

    public Registration2(DashboardForm parent, Registration1 step1) {
        this.dashboard = parent;
        this.step1 = step1;
        initComponents(); 
        customInit();
        initParticles();
    }

    private void customInit() {
        this.setOpaque(false);
        this.setLayout(null);
        this.setBounds(220, 30, 570, 450);
        
        // Make old components invisible
        makeOldComponentsInvisible();
        
        // Create custom styled components
        createCustomComponents();
        
        // Setup hover effects
        setupHoverEffects();
        
        // Setup Z-order
        setupZOrder();
    }
    
    private void makeOldComponentsInvisible() {
        lblGenderHeader.setVisible(false);
        jLabel5.setVisible(false);
    }
    
    private void createCustomComponents() {
        // Create title label
        titleLabel = new JLabel("GENDER SELECTION") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = "GENDER SELECTION";
                Font font = new Font("Orbitron", Font.BOLD, 18);
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
        titleLabel.setBounds(150, 10, 270, 30);
        titleLabel.setOpaque(false);
        this.add(titleLabel);
        
        // Create subtitle
        subtitleLabel = new JLabel("CHOOSE YOUR GENDER");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(accentOrange);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        subtitleLabel.setBounds(150, 35, 270, 20);
        subtitleLabel.setOpaque(false);
        this.add(subtitleLabel);
        
        // Create gym icons        
        // Create container panel
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
        containerPanel.setBounds(120, 55, 330, 280);
        containerPanel.setOpaque(false);
        this.add(containerPanel);
        
        // Create gender label
        genderLabel = new JLabel("SELECT GENDER");
        genderLabel.setFont(new Font("Orbitron", Font.BOLD, 12));
        genderLabel.setForeground(accentCyan);
        genderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        genderLabel.setBounds(100, 20, 130, 25);
        containerPanel.add(genderLabel);
        
        // Style gender selection buttons
        styleGenderButton(btnMale, "MALE", 0);
        btnMale.setBounds(50, 60, 100, 100);
        containerPanel.add(btnMale);
        
        styleGenderButton(btnFemale, "FEMALE", 1);
        btnFemale.setBounds(180, 60, 100, 100);
        containerPanel.add(btnFemale);
        
        // Style action buttons
        styleButton(btnFinalRegister, "REGISTER", 0);
        btnFinalRegister.setBounds(180, 180, 120, 35);
        containerPanel.add(btnFinalRegister);
        
        styleButton(btnBackToStep1, "← BACK", 1);
        btnBackToStep1.setBounds(30, 180, 120, 35);
        containerPanel.add(btnBackToStep1);
    }
        
private void styleGenderButton(JButton button, String text, int index) {
    // Store original icon
    final Icon originalIcon = button.getIcon();
    button.putClientProperty("originalIcon", originalIcon);
    button.putClientProperty("index", index);
    
    button.setFont(new Font("Orbitron", Font.BOLD, 10));
    button.setForeground(textLight);
    button.setFocusPainted(false);
    button.setBorderPainted(false);
    button.setContentAreaFilled(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    button.setHorizontalTextPosition(SwingConstants.CENTER);
    button.setVerticalTextPosition(SwingConstants.BOTTOM);
    
    // Add hover effect
    button.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            // Only apply hover glow if this button is NOT the selected one
            if (!selectedGender.equals(text)) {
                if (index == 0) {
                    button.setIcon(getGlowIcon((ImageIcon)originalIcon));
                } else {
                    button.setIcon(getPinkGlowIcon((ImageIcon)originalIcon));
                }
            }
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            // Only revert if this button is NOT the selected one
            if (!selectedGender.equals(text)) {
                button.setIcon(originalIcon);
            }
        }
    });
    
    // Override button painting
    button.setUI(new BasicButtonUI() {
        @Override
        public void paint(Graphics g, JComponent c) {
            JButton btn = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw selection glow if this button is selected
            if (selectedGender.equals(text)) {
                float pulse = 0.7f + 0.3f * (float)Math.sin(genderIconPulse * 3);
                int alpha = (int)(150 * pulse);
                
                // Draw outer glow rings
                for (int i = 0; i < 3; i++) {
                    g2.setColor(new Color(
                        index == 0 ? 0 : 255,
                        index == 0 ? 255 : 80,
                        index == 0 ? 255 : 150,
                        alpha / (i + 1)
                    ));
                    g2.setStroke(new BasicStroke(3 + i));
                    g2.drawOval(3 - i, 3 - i, btn.getWidth() - 6 + i*2, btn.getHeight() - 30 + i*2);
                }
                
                // Draw inner highlight
                g2.setColor(index == 0 ? accentCyan : accentPink);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(2, 2, btn.getWidth() - 5, btn.getHeight() - 25);
            }
            
            g2.dispose();
            
            // Call parent paint to draw the button content (icon and text)
            super.paint(g, c);
        }
    });
}

    private void styleButton(JButton button, String text, int index) {
        button.setText(text);
        button.setFont(new Font("Orbitron", Font.BOLD, 11));
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
                        registerButtonGlow = current;
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
    
    private ImageIcon getGlowIcon(ImageIcon icon) {
        if (icon == null) return null;
        
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        
        if (w <= 0 || h <= 0) return icon;
        
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        float[] scales = { 0.5f, 0.8f, 2.0f, 1.0f }; 
        float[] offsets = new float[4];
        java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
        return new ImageIcon(op.filter(bi, null));
    }
    
    private ImageIcon getPinkGlowIcon(ImageIcon icon) {
        if (icon == null) return null;
        
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        
        if (w <= 0 || h <= 0) return icon;
        
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        float[] scales = { 2.0f, 0.5f, 1.5f, 1.0f }; 
        float[] offsets = new float[4];
        java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
        return new ImageIcon(op.filter(bi, null));
    }
    
    private void setupHoverEffects() {
        // Add selection listeners
        btnMale.addActionListener(e -> selectGender("MALE"));
        btnFemale.addActionListener(e -> selectGender("FEMALE"));
    }
    
private void selectGender(String gender) {
    selectedGender = gender;
    
    // Reset both icons to original
    btnMale.setIcon(new ImageIcon(getClass().getResource("/image/Male.png")));
    btnFemale.setIcon(new ImageIcon(getClass().getResource("/image/Female.png")));
    
    // Apply permanent glow to selected gender
    if (gender.equals("MALE")) {
        btnMale.setIcon(getGlowIcon((ImageIcon)btnMale.getIcon()));
    } else if (gender.equals("FEMALE")) {
        btnFemale.setIcon(getPinkGlowIcon((ImageIcon)btnFemale.getIcon()));
    }
    
    // Trigger pulse animation
    startGenderPulse();
    
    // Force repaint to update glow effects
    btnMale.repaint();
    btnFemale.repaint();
}

    private void startGenderPulse() {
        Timer pulseTimer = new Timer(50, null);
        final float[] pulse = {0.0f};
        
        pulseTimer.addActionListener(e -> {
            pulse[0] += 0.2f;
            genderIconPulse = (float)Math.sin(pulse[0]) * 0.5f + 0.5f;
            
            if (pulse[0] >= Math.PI * 2) {
                pulseTimer.stop();
                genderIconPulse = 0.0f;
            }
            
            repaint();
        });
        pulseTimer.start();
    }
    
    private void drawCornerAccents(Graphics2D g2, int w, int h) {
        int size = 8;
        float pulse = 0.6f + 0.4f * (float)Math.sin(containerPulse * 3);
        
        g2.setColor(new Color(0, 255, 255, (int)(80 * pulse)));
        g2.fillOval(5, 5, size, size);
        g2.setColor(new Color(255, 120, 0, (int)(80 * pulse)));
        g2.fillOval(w - size - 5, 5, size, size);
        g2.fillOval(5, h - size - 5, size, size);
        g2.setColor(new Color(0, 255, 255, (int)(80 * pulse)));
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
        
    private void setupZOrder() {
        this.setComponentZOrder(titleLabel, 0);
        this.setComponentZOrder(subtitleLabel, 1);
        this.setComponentZOrder(containerPanel, 3);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int w = getWidth();
        int h = getHeight();
        
        // Draw subtle background gradient
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
    
    private float getFloatClientProperty(JButton button, String key, float defaultValue) {
        Object value = button.getClientProperty(key);
        if (value instanceof Float) {
            return (Float) value;
        }
        return defaultValue;
    }
    
    public void clearSelections() {
        selectedGender = "";
        btnMale.setIcon(new ImageIcon(getClass().getResource("/image/Male.png")));
        btnFemale.setIcon(new ImageIcon(getClass().getResource("/image/Female.png")));
    }
    
    private void handleRegistration() {
        if (selectedGender.isEmpty()) {
            showSciFiMessage("GENDER REQUIRED", "Please select a gender!", true);
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
            showSciFiMessage("REGISTRATION SUCCESS", "ID: " + customID, false);
            step1.clearFields();   
            this.clearSelections();
            
            Timer timer = new Timer(1500, e -> {
                dashboard.showIDCard(customID, username, selectedRole, email, contact);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
    
    private void showSciFiMessage(String title, String message, boolean isError) {
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

    private double getSalaryForRole(String role) {
        switch (role) {
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
        btnMale = new javax.swing.JButton();
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

        btnFinalRegister.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        btnFinalRegister.setText("REGISTER");
        btnFinalRegister.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnFinalRegister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinalRegisterActionPerformed(evt);
            }
        });
        add(btnFinalRegister, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 180, 130, 30));

        btnBackToStep1.setText("← Back to Info");
        btnBackToStep1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnBackToStep1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackToStep1ActionPerformed(evt);
            }
        });
        add(btnBackToStep1, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 220, 130, 30));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LoginBB.png"))); // NOI18N
        jLabel5.setToolTipText("");
        add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, -10, 600, 380));
    }// </editor-fold>//GEN-END:initComponents

    private void btnMaleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMaleMouseEntered
    if (!selectedGender.equals("MALE")) { // Changed from "Male" to "MALE"
        btnMale.setIcon(getGlowIcon((ImageIcon)btnMale.getIcon()));
    }
    }//GEN-LAST:event_btnMaleMouseEntered

    private void btnMaleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnMaleMouseExited
    if (!selectedGender.equals("MALE")) { // Changed from "Male" to "MALE"
        btnMale.setIcon(new ImageIcon(getClass().getResource("/image/Male.png")));
    }
    }//GEN-LAST:event_btnMaleMouseExited

    private void btnMaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMaleActionPerformed
 selectGender("MALE");
    }//GEN-LAST:event_btnMaleActionPerformed

    private void btnFemaleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFemaleMouseEntered
    if (!selectedGender.equals("FEMALE")) { // Changed from "Female" to "FEMALE"
        btnFemale.setIcon(getPinkGlowIcon((ImageIcon)btnFemale.getIcon()));
    }
    }//GEN-LAST:event_btnFemaleMouseEntered

    private void btnFemaleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnFemaleMouseExited
    if (!selectedGender.equals("FEMALE")) { // Changed from "Female" to "FEMALE"
        btnFemale.setIcon(new ImageIcon(getClass().getResource("/image/Female.png")));
    }
    }//GEN-LAST:event_btnFemaleMouseExited

    private void btnFemaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFemaleActionPerformed
 selectGender("FEMALE");
    }//GEN-LAST:event_btnFemaleActionPerformed


    
    private void btnFinalRegisterActionPerformed(java.awt.event.ActionEvent evt) { handleRegistration(); }
    private void btnBackToStep1ActionPerformed(java.awt.event.ActionEvent evt) { dashboard.showRegistrationStep1(); }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBackToStep1;
    private javax.swing.JButton btnFemale;
    private javax.swing.JButton btnFinalRegister;
    private javax.swing.JButton btnMale;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel lblGenderHeader;
    // End of variables declaration//GEN-END:variables
}