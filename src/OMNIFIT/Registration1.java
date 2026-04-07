package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.border.EmptyBorder;


public class Registration1 extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    
    // Animation variables
    private float formOpacity = 0.0f;
    private float nextButtonGlow = 0.0f;
    private float backButtonGlow = 0.0f;
    private float[] fieldGlowIntensity = new float[5];
    private float[] fieldPulsePhase = new float[5];
    private float containerPulse = 0.0f;
    private float scanlineOffset = 0.0f;
    private Timer animationTimer;
    private long lastFrameTime = System.nanoTime();
    private java.util.List<Particle> particles = new java.util.ArrayList<>();
   
    
    // Modern sci-fi color scheme
    private final Color accentCyan = new Color(0, 255, 255, 220);
    private final Color accentOrange = new Color(255, 120, 0, 220);
    private final Color accentPurple = new Color(150, 0, 255, 220);
    private final Color textLight = new Color(220, 240, 255);
    private final Color fieldBg = new Color(30, 45, 60, 200);
    
    // UI Components
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JPanel containerPanel;
    private JPanel[] fieldPanels = new JPanel[5];
    private JLabel[] fieldIcons = new JLabel[5];
    private JLabel leftGymIcon;
    private JLabel rightGymIcon;
    private JLabel centerGymIcon;

    public Registration1(DashboardForm parent) {
        this.dashboard = parent;
        initComponents();
        customInit();
        initParticles();
    }
    
    private void customInit() {
        this.setOpaque(false);
        this.setLayout(null);
        this.setBounds(220, 30, 570, 450);
        
        // Initialize field glow arrays
        for (int i = 0; i < 5; i++) {
            fieldGlowIntensity[i] = 0.0f;
            fieldPulsePhase[i] = 0.0f;
        }
        
        // Make old components invisible
        makeOldComponentsInvisible();
        
        // Create custom styled components
        createCustomComponents();
        
        // Style text fields
        styleAllFields();
        
        // Add hover effects
        setupHoverEffects();
        
        // Setup Z-order
        setupZOrder();
    }
    
    private void makeOldComponentsInvisible() {
        jLabel1.setVisible(false);
        jLabel2.setVisible(false);
        jLabel3.setVisible(false);
        jLabel4.setVisible(false);
        lblConfirm.setVisible(false);
        jLabel5.setVisible(false);
    }
    
    private void createCustomComponents() {
        // Create title label
        titleLabel = new JLabel("MEMBERSHIP REGISTRATION") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                String text = "MEMBERSHIP REGISTRATION";
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
        subtitleLabel = new JLabel("ENTER YOUR DETAILS");
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
        
        // Create field panels
        String[] labels = {"Username:", "Email:", "Password:", "Confirm:", "Contact:"};
        String[] placeholders = {"Enter username", "Enter email", "••••••••", "••••••••", "09XXXXXXXXX"};
        char[] echoChars = {0, 0, '●', '●', 0};
        Color[] colors = {accentCyan, accentOrange, accentPurple, accentOrange, accentCyan};
        
        for (int i = 0; i < 5; i++) {
            final int index = i;
            
            // Create field panel
            fieldPanels[i] = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw background
                    g2.setColor(fieldBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    
                    // Draw glow effect on focus
                    float glow = fieldGlowIntensity[index];
                    if (glow > 0.01f) {
                        float pulseGlow = 1.0f + 0.2f * (float)Math.sin(fieldPulsePhase[index] * 3);
                        int alpha = (int)(100 * glow * pulseGlow);
                        g2.setColor(new Color(colors[index].getRed(), colors[index].getGreen(), colors[index].getBlue(), alpha));
                        g2.setStroke(new BasicStroke(2.0f));
                        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 10, 10);
                    }
                    
                    // Draw bottom line
                    g2.setColor(colors[index]);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(5, getHeight() - 2, getWidth() - 5, getHeight() - 2);
                    
                    g2.dispose();
                }
            };
            fieldPanels[i].setBounds(20, 15 + i * 40, 290, 35);
            fieldPanels[i].setOpaque(false);
            containerPanel.add(fieldPanels[i]);
            
            // Create field icon
            fieldIcons[i] = new JLabel(createFieldIcon(i, 16));
            fieldIcons[i].setBounds(8, 8, 20, 20);
            fieldPanels[i].add(fieldIcons[i]);
            
            // Get the appropriate text field
            JTextField field = null;
            if (i == 0) field = txtName;
            else if (i == 1) field = txtEmail;
            else if (i == 2) field = txtPassword;
            else if (i == 3) field = txtConfirmPassword;
            else if (i == 4) field = txtContact;
            
            if (field != null) {
                field.setBounds(32, 3, 240, 28);
                field.setOpaque(false);
                field.setBorder(null);
                field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                field.setForeground(textLight);
                field.setCaretColor(colors[i]);
                fieldPanels[i].add(field);
                
// REPLACE the FocusListener section (around line 180-210):
if (field instanceof JPasswordField) {
    final JPasswordField pwdField = (JPasswordField) field;
    pwdField.setText(placeholders[i]);
    pwdField.setEchoChar((char) 0);
    
    final String currentPlaceholder = placeholders[i];
    final char currentEchoChar = echoChars[i];
    
    pwdField.addFocusListener(new FocusAdapter() {
        @Override
        public void focusGained(FocusEvent e) {
            if (pwdField.getText().equals(currentPlaceholder)) {
                pwdField.setText("");
                pwdField.setForeground(textLight);
                pwdField.setEchoChar(currentEchoChar);
            }
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            if (pwdField.getText().isEmpty()) {
                pwdField.setText(currentPlaceholder);
                pwdField.setForeground(new Color(150, 180, 200));
                pwdField.setEchoChar((char) 0);
            }
        }
    });
} else {
                    addPlaceholder(field, placeholders[i]);
                }
            }
        }
        
        // Style buttons
        styleButton(btnNext, "NEXT →", 0);
        btnNext.setBounds(180, 220, 120, 35);
        containerPanel.add(btnNext);
        
        styleButton(btnBack, "← BACK", 1);
        btnBack.setBounds(30, 220, 120, 35);
        containerPanel.add(btnBack);
    }
    
    
    private ImageIcon createFieldIcon(int index, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color color;
        switch(index) {
            case 0: color = accentCyan; break; // Username
            case 1: color = accentOrange; break; // Email
            case 2: color = accentPurple; break; // Password
            case 3: color = accentOrange; break; // Confirm
            default: color = accentCyan; // Contact
        }
        
        g2.setColor(color);
        g2.setStroke(new BasicStroke(2));
        
        switch(index) {
            case 0: // User icon
                g2.drawOval(4, 2, 8, 8);
                g2.drawLine(2, 12, 14, 12);
                break;
            case 1: // Email icon
                g2.drawRect(2, 4, 12, 8);
                g2.drawLine(2, 5, 8, 9);
                g2.drawLine(8, 9, 14, 5);
                break;
            case 2: // Password icon
            case 3:
                g2.drawOval(4, 2, 8, 8);
                g2.fillOval(7, 5, 2, 2);
                break;
            case 4: // Contact icon
                g2.drawRect(2, 2, 12, 12);
                g2.drawLine(2, 6, 14, 6);
                g2.drawLine(2, 10, 14, 10);
                break;
        }
        
        g2.dispose();
        return new ImageIcon(image);
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
    
    private void styleAllFields() {
        // Style text fields
        styleTextField(txtName);
        styleTextField(txtEmail);
        styleTextField(txtPassword);
        styleTextField(txtConfirmPassword);
        styleTextField(txtContact);
    }
    
    private void styleTextField(JTextField field) {
        field.setBackground(new Color(174, 179, 184));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
            field.setBorder(new EmptyBorder(5, 10, 5, 10));
    }
    
    private void addPlaceholder(final JTextField field, final String placeholder) {
        field.setText(placeholder);
        field.setForeground(new Color(150, 180, 200));
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(textLight);
                }
            }
            
            @Override
            public void focusLost(FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(150, 180, 200));
                }
            }
        });
    }
    
    private void setupHoverEffects() {
        // Add focus listeners for field glow effects
        Component[] fields = {txtName, txtEmail, txtPassword, txtConfirmPassword, txtContact};
        
        for (int i = 0; i < fields.length; i++) {
            final int index = i;
            fields[i].addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    startFieldGlowAnimation(index, true);
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    startFieldGlowAnimation(index, false);
                }
            });
        }
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
                
                if (fieldPanels[fieldIndex] != null) {
                    fieldPanels[fieldIndex].repaint();
                }
                
                if (Math.abs(fieldGlowIntensity[fieldIndex] - target) < 0.01f) {
                    ((Timer)e.getSource()).stop();
                }
            }
        });
        glowTimer.start();
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
        this.setComponentZOrder(containerPanel, 5);
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
    
    public void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtContact.setText("");
    }
    
    public boolean validateFields() {
        String username = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirmPassword.getPassword());
        String contact = txtContact.getText().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || contact.isEmpty() ||
            username.equals("Enter username") || email.equals("Enter email") || 
            password.equals("••••••••") || contact.equals("09XXXXXXXXX")) {
            showSciFiMessage("ERROR", "Please fill all fields!", true);
            return false;
        }

        if (!username.matches("^[a-zA-Z\\s]{1,12}$")) {
            showSciFiMessage("INVALID NAME", "Name must be text only and max 12 characters!", true);
            return false;
        }

        if (!email.contains("@")) {
            showSciFiMessage("INVALID EMAIL", "Email must contain @ symbol!", true);
            return false;
        }

        if (!contact.matches("^\\d{11}$")) {
            showSciFiMessage("INVALID CONTACT", "Contact must be exactly 11 digits!", true);
            return false;
        }
        
        if (Config.isUserExists(username)) {
            showSciFiMessage("USERNAME TAKEN", "Username '" + username + "' is already taken!", true);
            return false;
        }

        if (Config.isEmailExists(email)) {
            showSciFiMessage("EMAIL TAKEN", "Email '" + email + "' is already registered!", true);
            return false;
        }

        if (!password.equals(confirm)) {
            showSciFiMessage("PASSWORD MISMATCH", "Passwords do not match!", true);
            return false;
        }

        return true;
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