package OMNIFIT;

import Config.Design;
import Config.Config;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.basic.BasicButtonUI;

public final class DashboardForm extends javax.swing.JFrame {
    
    private Timer animationTimer;
    private javax.swing.Icon originalIcon;
    private User userPanel;
    private Management managementPanel;
    private Services servicesPanel;
    private Members membersPanel;
    private Profile profilePanel;
    public Design dashboardPanel;
    private Transaction transactionPanel;
    private IDPanel currentIDPanel;
    private LoginForm loginForm;
    private Registration1 registration1;
    private Registration2 registration2;
        
    // Animation timer for Welcome panel
        private Timer welcomeAnimTimer;
        private float welcomeGlowIntensity = 0f;
        private float welcomeTextPulse = 0f;
        private float welcomeParticlePhase = 0f;

    private final int homeOriginalY;
    private String userRole = "Guest";
    private String currentUsername = ""; 
    private int mouseX, mouseY;
    
    // Individual button expansion states
    private Map<JButton, ButtonAnimationState> buttonStates = new HashMap<>();
    private Timer globalAnimationTimer;
    
    // Fixed button dimensions
    private final int collapsedButtonWidth = 70;
    private final int expandedButtonWidth = 200;
    private final int buttonHeight = 55;
    private final int buttonX = 15;
    private final int dashboardY = 115;
    private final int membersY = 170;
    private final int servicesY = 225;
    private final int transactionY = 280;
    private final int managementY = 335;
    private final int userY = 390;
    private final int profileX = 25;
    private final int profileY = 20;
    
    
    // Modern sci-fi color schemes - refined and elegant
    private final Color[][] modernGradients = {
        {   // Dashboard - Cyan theme
            new Color(0, 220, 220, 240),     // Bright Cyan
            new Color(0, 180, 200, 220),     // Deep Cyan
            new Color(0, 140, 180, 200),     // Ocean Blue
            new Color(0, 100, 140, 180)      // Deep Ocean
        },
        {   // Members - Blue theme
            new Color(0, 180, 255, 240),     // Bright Azure
            new Color(0, 120, 210, 220),     // Deep Blue
            new Color(40, 60, 180, 200),     // Royal Blue
            new Color(20, 30, 120, 180)      // Midnight Blue
        },
        {   // Services - Orange theme
            new Color(255, 120, 0, 240),      // Bright Orange
            new Color(220, 80, 0, 220),       // Deep Orange
            new Color(180, 50, 0, 200),       // Red-Orange
            new Color(140, 30, 0, 180)        // Dark Red
        },
        {   // Transaction - Gold theme
            new Color(255, 215, 0, 240),      // Bright Gold
            new Color(218, 165, 32, 220),     // Golden Rod
            new Color(184, 134, 11, 200),     // Dark Golden Rod
            new Color(139, 69, 19, 180)       // Saddle Brown
        },
        {   // Management - Green theme
            new Color(0, 220, 120, 240),      // Emerald
            new Color(0, 180, 100, 220),      // Green
            new Color(0, 140, 80, 200),       // Forest Green
            new Color(0, 100, 60, 180)        // Deep Green
        },
        {   // User - Purple theme
            new Color(180, 0, 220, 240),      // Purple
            new Color(140, 0, 180, 220),      // Deep Purple
            new Color(100, 0, 140, 200),      // Violet
            new Color(70, 0, 100, 180)        // Dark Violet
        }
    };
    
    // Glow colors
    private final Color[] glowColors = {
        new Color(0, 255, 255, 80),     // Cyan glow for Dashboard
        new Color(100, 200, 255, 80),   // Blue glow for Members
        new Color(255, 180, 100, 80),   // Orange glow for Services
        new Color(255, 215, 0, 80),     // Gold glow for Transaction
        new Color(100, 255, 180, 80),   // Green glow for Management
        new Color(200, 100, 255, 80)    // Purple glow for User
    };
    
    // Icon colors
    private final Color[] iconColors = {
        new Color(0, 255, 255),    // Cyan for Dashboard
        new Color(0, 200, 255),    // Blue for Members
        new Color(255, 150, 50),   // Orange for Services
        new Color(255, 215, 0),    // Gold for Transaction
        new Color(50, 255, 150),   // Green for Management
        new Color(200, 100, 255)   // Purple for User
    };
    
    // Hover colors for icons (independent from original)
    private final Color[] hoverColors = {
        new Color(255, 255, 255),    // White for Dashboard
        new Color(255, 255, 255),    // White for Members
        new Color(255, 255, 255),    // White for Services
        new Color(255, 255, 255),    // White for Transaction
        new Color(255, 255, 255),    // White for Management
        new Color(255, 255, 255)     // White for User
    };
    
    // Icons for buttons (using Unicode)
    private final String[] buttonIcons = {"📊", "👥", "⚡", "💰", "📋", "👤"};
    private final String[] buttonTexts = {"DASHBOARD", "MEMBERS", "SERVICES", "TRANSACTION", "MANAGEMENT", "USER"};
    
    // Profile button specific
    private float profileRotation = 0f;
    private float profileGlowIntensity = 0f;
    private float profileOrbitAngle = 0f;
    private float profileBreathPulse = 0f;
    private boolean profileHovered = false;
    private Timer profileAnimTimer;
    
    

    public DashboardForm() {
        initComponents();
        homeOriginalY = MEMBERS.getY();
        
        // Initialize Welcome panel with custom painting
        customizeWelcomePanel();
        startWelcomeAnimation();
        
        customInit();
        showLogin();
        initModernButtons();
        initProfileButton();
        initExitButton();
        
        // Set fixed button positions
        setFixedButtonPositions();
        
        // Start global animation timer
        startGlobalAnimation();
        
        // Start Welcome panel animation
        startWelcomeAnimation();
    }
    private void startWelcomeAnimation() {
    welcomeAnimTimer = new Timer(30, e -> {
        welcomeGlowIntensity = 0.6f + 0.4f * (float)Math.sin(System.currentTimeMillis() * 0.003);
        welcomeTextPulse = 0.8f + 0.2f * (float)Math.sin(System.currentTimeMillis() * 0.004);
        welcomeParticlePhase += 0.1f;
        Welcome.repaint();
    });
    welcomeAnimTimer.start();
}
private void customizeWelcomePanel() {
    Welcome.setOpaque(false);
    
    // Create a custom painter for the Welcome panel
    JPanel welcomePainter = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int w = Welcome.getWidth();
            int h = Welcome.getHeight();
            
            // Draw 80% faded dark background
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, w, h);
            
            // Draw subtle gym floor pattern
            g2d.setColor(new Color(255, 215, 0, 30));
            g2d.setStroke(new BasicStroke(1f));
            
            
            
            // Draw corner accents
            g2d.setStroke(new BasicStroke(2f));
            g2d.setColor(new Color(255, 215, 0, 150));
            
            // Top-left corner
            g2d.drawLine(10, 10, 30, 10);
            g2d.drawLine(10, 10, 10, 30);
            
            // Top-right corner
            g2d.drawLine(w - 30, 10, w - 10, 10);
            g2d.drawLine(w - 10, 10, w - 10, 30);
            
            // Bottom-left corner
            g2d.drawLine(10, h - 30, 10, h - 10);
            g2d.drawLine(10, h - 10, 30, h - 10);
            
            // Bottom-right corner
            g2d.drawLine(w - 30, h - 10, w - 10, h - 10);
            g2d.drawLine(w - 10, h - 30, w - 10, h - 10);
            
            // Draw floating particles
            for (int i = 0; i < 15; i++) {
                float x = (float)((i * 27 + welcomeParticlePhase * 50) % w);
                float y = (float)((i * 33 + welcomeParticlePhase * 30) % h);
                float size = 2 + (float)Math.sin(welcomeParticlePhase + i) * 2;
                
                int alpha = (int)(80 * welcomeGlowIntensity);
                g2d.setColor(new Color(255, 215, 0, alpha));
                g2d.fillOval((int)x, (int)y, (int)size, (int)size);
            }
            
            // Draw text
            FontMetrics fm;
            int x, y = 200;
            
            // LINE 1
            g2d.setFont(new Font("Arial Black", Font.BOLD, 16));
            fm = g2d.getFontMetrics();
            String line1 = "WELCOME, OMNIFIT TEAM";
            x = (w - fm.stringWidth(line1)) / 2;
            
            // Glow effect
            g2d.setColor(new Color(255, 215, 0, (int)(80 * welcomeGlowIntensity)));
            for (int i = 1; i <= 4; i++) {
                g2d.drawString(line1, x + i, y + i);
            }
            
            // Main text
            LinearGradientPaint gradient1 = new LinearGradientPaint(
                x - 20, y - 10, x + fm.stringWidth(line1) + 20, y + 10,
                new float[]{0f, 0.5f, 1f},
                new Color[]{new Color(255, 215, 0), new Color(255, 255, 255), new Color(255, 215, 0)}
            );
            g2d.setPaint(gradient1);
            g2d.drawString(line1, x, y);
            
            // LINE 2
            y += 30;
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            fm = g2d.getFontMetrics();
            String line2 = "ADMINISTRATORS & USERS";
            x = (w - fm.stringWidth(line2)) / 2;
            g2d.setColor(new Color(100, 200, 255, (int)(200 * welcomeTextPulse)));
            g2d.drawString(line2, x, y);
            
            // LINE 3
            y += 25;
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            fm = g2d.getFontMetrics();
            String line3 = "MANAGE • TRACK • GROW • SUCCEED";
            x = (w - fm.stringWidth(line3)) / 2;
            
            LinearGradientPaint gradient3 = new LinearGradientPaint(
                x - 10, y - 5, x + fm.stringWidth(line3) + 10, y + 5,
                new float[]{0f, 0.3f, 0.7f, 1f},
                new Color[]{
                    new Color(255, 215, 0),
                    new Color(255, 255, 255),
                    new Color(218, 165, 32),
                    new Color(184, 134, 11)
                }
            );
            g2d.setPaint(gradient3);
            g2d.drawString(line3, x, y);
            
            // LINE 4
            y += 30;
            g2d.setFont(new Font("Arial", Font.ITALIC, 10));
            fm = g2d.getFontMetrics();
            String line4 = "BUILDING A STRONGER FITNESS COMMUNITY";
            x = (w - fm.stringWidth(line4)) / 2;
            g2d.setColor(new Color(200, 200, 255, (int)(180 * welcomeGlowIntensity)));
            g2d.drawString(line4, x, y);
            
            // Decorative line
            y += 10;
            g2d.setStroke(new BasicStroke(1f));
            g2d.setColor(new Color(255, 215, 0, 100));
            g2d.drawLine(w/2 - 80, y, w/2 + 80, y);
            
            g2d.dispose();
        }
    };
    
    // Set the custom painter as the Welcome panel's glass pane or overlay
    Welcome.setLayout(new java.awt.BorderLayout());
    welcomePainter.setOpaque(false);
    welcomePainter.setBounds(0, 0, Welcome.getWidth(), Welcome.getHeight());
    Welcome.add(welcomePainter, java.awt.BorderLayout.CENTER);
}

    
private void setFixedButtonPositions() {
    // Always use expanded width for consistent button size (NO COLLAPSED STATE)
    DASHBOARD.setBounds(buttonX, dashboardY, expandedButtonWidth, buttonHeight);
    MEMBERS.setBounds(buttonX, membersY, expandedButtonWidth, buttonHeight);
    SERVICES.setBounds(buttonX, servicesY, expandedButtonWidth, buttonHeight);
    TRANSACTION.setBounds(buttonX, transactionY, expandedButtonWidth, buttonHeight);
    MANAGEMENT.setBounds(buttonX, managementY, expandedButtonWidth, buttonHeight);
    btnUser.setBounds(buttonX, userY, expandedButtonWidth, buttonHeight);
    profile.setBounds(profileX, profileY, 120, 90);
    
    // Force fixed size
    DASHBOARD.setSize(expandedButtonWidth, buttonHeight);
    MEMBERS.setSize(expandedButtonWidth, buttonHeight);
    SERVICES.setSize(expandedButtonWidth, buttonHeight);
    TRANSACTION.setSize(expandedButtonWidth, buttonHeight);
    MANAGEMENT.setSize(expandedButtonWidth, buttonHeight);
    btnUser.setSize(expandedButtonWidth, buttonHeight);
}

private class ButtonAnimationState {
    float expandProgress = 0f;
    float hoverProgress = 0f;
    float glowIntensity = 0f;
    float pulsePhase = 0f;
    float lightAngle = 0f;
    float rippleIntensity = 0f;
    float scale3D = 1f;
    float rotationY = 0f;
    float floatOffset = 0f;
    boolean isHovered = false;
    boolean keepExpanded = false;
    Timer expandTimer;
    Timer hoverTimer;
    
    ButtonAnimationState() {
        expandTimer = new Timer(10, null);
        hoverTimer = new Timer(10, null);
    }
}

    private void startGlobalAnimation() {
        globalAnimationTimer = new Timer(16, e -> {
            long currentTime = System.nanoTime();
            float deltaTime = 0.016f; // Approximate 60fps
            
            for (Map.Entry<JButton, ButtonAnimationState> entry : buttonStates.entrySet()) {
                ButtonAnimationState state = entry.getValue();
                
                // Update pulse phase
                state.pulsePhase += deltaTime * 3.0f;
                
                // Update light angle
                state.lightAngle += deltaTime * 2.0f;
                
                // Update float offset for 3D effect
                state.floatOffset = (float)Math.sin(state.pulsePhase * 2) * 3;
                
                // Update rotation for 3D effect on hover
                if (state.isHovered) {
                    state.rotationY = Math.min(state.rotationY + deltaTime * 5, 15);
                } else {
                    state.rotationY = Math.max(state.rotationY - deltaTime * 5, 0);
                }
                
                // Update scale for 3D effect
                state.scale3D = 1f + (state.hoverProgress * 0.05f) + (float)Math.sin(state.pulsePhase * 4) * 0.02f;
                
                // Update ripple intensity
                if (state.isHovered) {
                    state.rippleIntensity = Math.min(state.rippleIntensity + deltaTime * 3, 1f);
                } else {
                    state.rippleIntensity = Math.max(state.rippleIntensity - deltaTime * 3, 0f);
                }
                
                // Update glow intensity based on hover
                state.glowIntensity = state.hoverProgress * (0.7f + 0.3f * (float)Math.sin(state.pulsePhase * 2));
            }
            
            repaintButtons();
        });
        globalAnimationTimer.start();
    }
    
    private void repaintButtons() {
        DASHBOARD.repaint();
        MEMBERS.repaint();
        SERVICES.repaint();
        TRANSACTION.repaint();
        MANAGEMENT.repaint();
        btnUser.repaint();
        profile.repaint();
        btnExit.repaint();
    }
    
private void initModernButtons() {
    JButton[] buttons = {DASHBOARD, MEMBERS, SERVICES, TRANSACTION, MANAGEMENT, btnUser};
    
    for (int i = 0; i < buttons.length; i++) {
        final int index = i;
        final JButton button = buttons[i];
        
        // Create animation state
        ButtonAnimationState state = new ButtonAnimationState();
        buttonStates.put(button, state);
        
        // Store properties
        button.putClientProperty("displayText", buttonTexts[i]);
        button.putClientProperty("gradientColors", modernGradients[i]);
        button.putClientProperty("glowColor", glowColors[i]);
        button.putClientProperty("iconColor", iconColors[i]);
        button.putClientProperty("hoverColor", hoverColors[i]);
        button.putClientProperty("buttonIcon", buttonIcons[i]);
        
        // Set button properties
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setRequestFocusEnabled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setHorizontalAlignment(JButton.LEFT);
        
        // Remove any default button listeners that might cause state changes
        button.setUI(new BasicButtonUI() {
            @Override
            protected void paintButtonPressed(Graphics g, javax.swing.AbstractButton b) {
                // Override to do nothing - prevents pressed state changes
            }
            
            @Override
            public void paint(Graphics g, javax.swing.JComponent c) {
                JButton btn = (JButton) c;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                
                ButtonAnimationState btnState = buttonStates.get(btn);
                if (btnState == null) return;
                
                int w = btn.getWidth();
                int h = btn.getHeight();
                
                // DETERMINE INDEX BASED ON BUTTON REFERENCE
                int buttonIndex = 0;
                if (btn == DASHBOARD) buttonIndex = 0;
                else if (btn == MEMBERS) buttonIndex = 1;
                else if (btn == SERVICES) buttonIndex = 2;
                else if (btn == TRANSACTION) buttonIndex = 3;
                else if (btn == MANAGEMENT) buttonIndex = 4;
                else if (btn == btnUser) buttonIndex = 5;
                
                // Draw the button with current state
                drawIndividualButton(g2, btn, w, h, btnState, buttonIndex);       
                
                g2.dispose();
            }
        });
        
        // Add hover listeners for individual expansion
        addIndividualButtonListeners(button, state);
    }
}

private void drawIndividualButton(Graphics2D g2, JButton btn, int w, int h, 
                                  ButtonAnimationState state, int colorIndex) {
    
    // Get button properties
    String displayText = (String) btn.getClientProperty("displayText");
    String icon = (String) btn.getClientProperty("buttonIcon");
    Color[] gradients = (Color[]) btn.getClientProperty("gradientColors");
    Color glowColor = (Color) btn.getClientProperty("glowColor");
    Color iconColor = (Color) btn.getClientProperty("iconColor");
    Color hoverColor = (Color) btn.getClientProperty("hoverColor");
    
    // USE FIXED WIDTH - no expansion
    int effectiveWidth = expandedButtonWidth;
    
    // Create button shape with fixed size
    Shape buttonShape = new RoundRectangle2D.Float(2, 2, effectiveWidth - 4, h - 4, 25, 25);
    
    // Draw outer glow based on hover
    if (state.hoverProgress > 0.01f) {
        draw3DGlow(g2, buttonShape, effectiveWidth, h, state);
    }
    
    // Draw button body with 3D gradient
    draw3DButtonBody(g2, buttonShape, effectiveWidth, h, state, gradients);
    
    // Draw light sweep effect
    if (state.hoverProgress > 0.1f) {
        drawLightSweep(g2, buttonShape, effectiveWidth, h, state.lightAngle, state.hoverProgress);
    }
    
    // Draw ripple effect
    if (state.rippleIntensity > 0.1f) {
        drawRippleEffect(g2, buttonShape, effectiveWidth, h, state.rippleIntensity, state.pulsePhase);
    }
    
    // Draw edge highlights for 3D effect
    draw3DEdgeHighlights(g2, buttonShape, effectiveWidth, h, state.hoverProgress);
    
    // Draw the icon with hover animation
    draw3DIcon(g2, 20, h/2, icon, iconColor, hoverColor, state, colorIndex);
    
    // Draw text
    draw3DText(g2, displayText, effectiveWidth, h, state, glowColor);
    
    // Draw corner accents
    if (state.hoverProgress > 0.1f) {
        drawCornerAccents(g2, effectiveWidth, h, state.hoverProgress, glowColor);
    }
}

private void addIndividualButtonListeners(final JButton button, final ButtonAnimationState state) {
    
    // Mouse adapter for hover only (no size expansion)
    MouseAdapter adapter = new MouseAdapter() {
        private Timer hoverTimer;
        private boolean isPressed = false;
        
        @Override
        public void mouseEntered(MouseEvent e) {
            state.isHovered = true;
            startHoverAnimation(true);
            button.setFont(new Font("Impact", Font.BOLD, 14));
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            state.isHovered = false;
            startHoverAnimation(false);
            isPressed = false;
            button.setFont(new Font("Impact", Font.BOLD, 14));
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            isPressed = true;
            state.rippleIntensity = 0.8f;
            button.repaint();
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (isPressed) {
                isPressed = false;
                
                // Trigger the action
                if (button == DASHBOARD) {
                    showPanel(dashboardPanel);
                } else if (button == MEMBERS) {
                    showPanel(membersPanel);
                } else if (button == SERVICES) {
                    showPanel(servicesPanel);
                } else if (button == TRANSACTION) {
                    showPanel(transactionPanel);
                } else if (button == MANAGEMENT) {
                    showPanel(managementPanel);
                } else if (button == btnUser) {
                    showPanel(userPanel);
                }
            }
        }
        
        private void startHoverAnimation(boolean enter) {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }
            
            hoverTimer = new Timer(10, null);
            hoverTimer.addActionListener(evt -> {
                float target = enter ? 1.0f : 0.0f;
                float step = 0.15f;
                
                if (enter) {
                    state.hoverProgress = Math.min(state.hoverProgress + step, target);
                } else {
                    state.hoverProgress = Math.max(state.hoverProgress - step, target);
                }
                
                if (Math.abs(state.hoverProgress - target) < 0.01f) {
                    state.hoverProgress = target;
                    hoverTimer.stop();
                }
                
                button.repaint();
            });
            hoverTimer.start();
        }
    };
    
    button.addMouseListener(adapter);
    button.addMouseMotionListener(adapter);
    
    // Override action listeners to use our custom handling
    button.removeActionListener(button.getActionListeners().length > 0 ? 
                                button.getActionListeners()[0] : null);
}

private void draw3DGlow(Graphics2D g2, Shape shape, int w, int h, ButtonAnimationState state) {
    g2.setClip(shape);
    
    float pulseIntensity = 1.0f + 0.3f * (float)Math.sin(state.pulsePhase * 3);
    int alpha = (int)(80 * state.glowIntensity * pulseIntensity);
    alpha = Math.min(255, Math.max(0, alpha));
    
    // Draw multiple glow layers for 3D effect
    for (int i = 0; i < 4; i++) {
        float strokeWidth = 5.0f + i * 3.0f;
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int currentAlpha = alpha - i * 20;
        currentAlpha = Math.min(255, Math.max(0, currentAlpha));
        
        g2.setColor(new Color(255, 255, 255, currentAlpha));
        g2.draw(shape);
    }
    
    g2.setClip(null);
}

private void draw3DButtonBody(Graphics2D g2, Shape shape, int w, int h,
                               ButtonAnimationState state, Color[] gradients) {
    
    // Create 3D gradient with light direction based on hover
    Point2D start = new Point2D.Float(0, 0);
    Point2D end = new Point2D.Float(w, h);
    
    // Adjust gradient based on rotation for 3D effect
    if (state.rotationY > 0) {
        float rotateFactor = state.rotationY / 15f;
        start = new Point2D.Float(w * 0.3f, 0);
        end = new Point2D.Float(w * 0.7f, h);
    }
    
    float[] fractions = {0.0f, 0.3f, 0.6f, 1.0f};
    Color[] colors = new Color[4];
    
    for (int i = 0; i < 4; i++) {
        // Add brightness variation based on hover and 3D rotation
        float brightness = 1.0f + (state.hoverProgress * 0.3f) + (state.rotationY / 15f * 0.2f);
        
        int r = (int)Math.min(255, gradients[i].getRed() * brightness);
        int g = (int)Math.min(255, gradients[i].getGreen() * brightness);
        int b = (int)Math.min(255, gradients[i].getBlue() * brightness);
        
        colors[i] = new Color(r, g, b, gradients[i].getAlpha());
    }
    
    LinearGradientPaint gradient = new LinearGradientPaint(start, end, fractions, colors);
    g2.setPaint(gradient);
    g2.fill(shape);
    
    // Add inner shadow for depth
    g2.setClip(shape);
    GradientPaint shadow = new GradientPaint(
        0, h/2, new Color(0, 0, 0, 0),
        0, h, new Color(0, 0, 0, 60)
    );
    g2.setPaint(shadow);
    g2.fill(shape);
    
    // Add highlight at top for 3D effect
    GradientPaint highlight = new GradientPaint(
        0, 0, new Color(255, 255, 255, 40),
        0, h/4, new Color(255, 255, 255, 0)
    );
    g2.setPaint(highlight);
    g2.fill(shape);
    
    g2.setClip(null);
}

private void draw3DIcon(Graphics2D g2, int x, int y, String icon, Color iconColor, 
                        Color hoverColor, ButtonAnimationState state, int buttonIndex) {
    
    // Calculate icon size based on hover progress
    float iconScale = 1.0f + (state.hoverProgress * 0.25f); // Scale up to 25% on hover
    int iconSize = (int)(28 * iconScale); // Base size 28, scales with hover
    
    // Set font for icon with dynamic size
    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, iconSize));
    
    // Calculate icon position with hover offset
    int iconX = x + (int)(state.floatOffset * 0.5f);
    int iconY = y + 10 + (int)(state.floatOffset * 0.3f);
    
    // Add hover lift effect
    if (state.hoverProgress > 0.1f) {
        iconY -= (int)(5 * state.hoverProgress); // Move up slightly on hover
    }
    
    // Determine which color to use for the icon
    Color currentIconColor;
    if (state.hoverProgress > 0.01f) {
        // ON HOVER: Use hover color with smooth transition
        int r = (int)(iconColor.getRed() * (1 - state.hoverProgress) + hoverColor.getRed() * state.hoverProgress);
        int g = (int)(iconColor.getGreen() * (1 - state.hoverProgress) + hoverColor.getGreen() * state.hoverProgress);
        int b = (int)(iconColor.getBlue() * (1 - state.hoverProgress) + hoverColor.getBlue() * state.hoverProgress);
        currentIconColor = new Color(r, g, b);
    } else {
        // NOT HOVERED: Use original icon color
        currentIconColor = iconColor;
    }
    
    // Draw LIGHT glow behind icon on hover (softer and brighter)
    if (state.hoverProgress > 0.1f) {
        // Softer glow with higher brightness
        for (int i = 1; i <= 4; i++) {
            float alpha = 40 * state.hoverProgress / i; // Lower alpha for lighter glow
            int glowSize = i + (int)(2 * state.hoverProgress);
            
            // Use hover color for glow but with lower opacity for lighter effect
            g2.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), 
                                  hoverColor.getBlue(), (int)alpha));
            
            g2.drawString(icon, iconX - glowSize, iconY - glowSize);
            g2.drawString(icon, iconX + glowSize, iconY + glowSize);
        }
        
        // Add a single very light glow ring
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(hoverColor.getRed(), hoverColor.getGreen(), 
                              hoverColor.getBlue(), (int)(30 * state.hoverProgress)));
        g2.drawOval(iconX - 12, iconY - 22, 24, 24);
        
        // Add a subtle white inner glow for extra lightness
        g2.setStroke(new BasicStroke(1.0f));
        g2.setColor(new Color(255, 255, 255, (int)(20 * state.hoverProgress)));
        g2.drawOval(iconX - 10, iconY - 20, 20, 20);
    }
    
    // Draw main icon with current color
    g2.setColor(currentIconColor);
    g2.drawString(icon, iconX, iconY);
}

private void draw3DText(Graphics2D g2, String text, int w, int h,
                         ButtonAnimationState state, Color glowColor) {
    
    // Set the font for the text
    g2.setFont(new Font("Impact", Font.BOLD, 18));
    
    // Calculate text position
    int textX = 55;
    int textY = (h - g2.getFontMetrics().getHeight()) / 2 + 
                g2.getFontMetrics().getAscent() - 2;
    
    // Add floating offset to text
    textY += state.floatOffset * 0.5f;
    
    // Calculate fade based on expansion
    float textAlpha = Math.min(1.0f, state.expandProgress * 1.5f);
    
    // Draw text shadow for 3D effect
    g2.setColor(new Color(0, 0, 0, (int)(100 * textAlpha)));
    g2.drawString(text, textX + 2, textY + 2);
    
    // Draw main text with gradient
    if (state.hoverProgress > 0.1f) {
        // Animated text gradient on hover
        float phase = (float)Math.sin(state.pulsePhase * 2) * 0.2f + 0.8f;
        int alpha = (int)(255 * textAlpha * phase);
        
        LinearGradientPaint textGradient = new LinearGradientPaint(
            textX, textY - 5, textX + 50, textY + 10,
            new float[]{0.0f, 1.0f},
            new Color[]{
                new Color(255, 255, 255, 255),
                new Color(glowColor.getRed(), glowColor.getGreen(), 
                         glowColor.getBlue(), alpha)
            }
        );
        g2.setPaint(textGradient);
    } else {
        g2.setColor(new Color(255, 255, 255, (int)(255 * textAlpha)));
    }
    
    g2.drawString(text, textX, textY);
}

private void drawLightSweep(Graphics2D g2, Shape shape, int w, int h,
                            float lightAngle, float hoverProgress) {
    g2.setClip(shape);
    
    float sweepX = w * (float)(Math.sin(lightAngle) * 0.5f + 0.5f);
    
    int alpha = (int)(100 * hoverProgress);
    alpha = Math.min(255, Math.max(0, alpha));
    
    GradientPaint sweep = new GradientPaint(
        sweepX - 40, 0, new Color(255, 255, 255, alpha),
        sweepX, 0, new Color(255, 255, 255, 0),
        true
    );
    
    g2.setPaint(sweep);
    g2.fill(shape);
    g2.setClip(null);
}

private void drawRippleEffect(Graphics2D g2, Shape shape, int w, int h,
                              float rippleIntensity, float pulsePhase) {
    g2.setClip(shape);
    
    int centerX = w / 2;
    int centerY = h / 2;
    float ripplePhase = pulsePhase * 5;
    
    for (int i = 0; i < 4; i++) {
        float offset = (ripplePhase + i * 2) % 12;
        float size = 20 + offset * 6;
        
        int alpha = (int)(40 * rippleIntensity * (1 - i * 0.25f) * (1 - offset/12));
        alpha = Math.min(255, Math.max(0, alpha));
        
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(new Color(255, 255, 255, alpha));
        g2.drawOval(
            (int)(centerX - size/2),
            (int)(centerY - size/2),
            (int)size,
            (int)size
        );
    }
    
    g2.setClip(null);
}

private void draw3DEdgeHighlights(Graphics2D g2, Shape shape, int w, int h, float hoverProgress) {
    g2.setStroke(new BasicStroke(2.0f));
    
    int topAlpha = (int)(150 * hoverProgress);
    int bottomAlpha = (int)(120 * hoverProgress);
    
    topAlpha = Math.min(255, Math.max(0, topAlpha));
    bottomAlpha = Math.min(255, Math.max(0, bottomAlpha));
    
    // Top edge highlight (stronger for 3D effect)
    g2.setColor(new Color(255, 255, 255, topAlpha));
    g2.drawLine(5, 5, w - 5, 5);
    
    // Left edge highlight
    g2.drawLine(5, 5, 5, h - 5);
    
    // Bottom edge shadow
    g2.setColor(new Color(0, 0, 0, bottomAlpha));
    g2.drawLine(5, h - 5, w - 5, h - 5);
    g2.drawLine(w - 5, 5, w - 5, h - 5);
}

private void drawCornerAccents(Graphics2D g2, int w, int h, float intensity, Color glowColor) {
    int accentSize = 15;
    int alpha = (int)(180 * intensity);
    alpha = Math.min(255, Math.max(0, alpha));
    
    g2.setStroke(new BasicStroke(3.0f));
    g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                          glowColor.getBlue(), alpha));
    
    // Top-left corner
    g2.drawLine(8, 8, 8 + accentSize, 8);
    g2.drawLine(8, 8, 8, 8 + accentSize);
    
    // Top-right corner
    g2.drawLine(w - 8 - accentSize, 8, w - 8, 8);
    g2.drawLine(w - 8, 8, w - 8, 8 + accentSize);
    
    // Bottom-left corner
    g2.drawLine(8, h - 8 - accentSize, 8, h - 8);
    g2.drawLine(8, h - 8, 8 + accentSize, h - 8);
    
    // Bottom-right corner
    g2.drawLine(w - 8 - accentSize, h - 8, w - 8, h - 8);
    g2.drawLine(w - 8, h - 8 - accentSize, w - 8, h - 8);
}

private void initProfileButton() {
    profile.setFont(new Font("Segoe UI", Font.BOLD, 0));
    
    // Store properties
    profile.putClientProperty("hoverProgress", 0.0f);
    profile.putClientProperty("glowIntensity", 0.0f);
    
    profile.setOpaque(false);
    profile.setContentAreaFilled(false);
    profile.setBorderPainted(false);
    profile.setFocusPainted(false);
    profile.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Add hover effects
    profile.addMouseListener(new MouseAdapter() {
        private Timer hoverTimer;
        
        @Override
        public void mouseEntered(MouseEvent e) {
            profileHovered = true;
            startProfileHoverAnimation(true);
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            profileHovered = false;
            startProfileHoverAnimation(false);
        }
        
        private void startProfileHoverAnimation(boolean enter) {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }
            
            hoverTimer = new Timer(10, null);
            hoverTimer.addActionListener(evt -> {
                float current = getFloatClientProperty(profile, "hoverProgress", 0.0f);
                float step = 0.1f;
                float target = enter ? 1.0f : 0.0f;
                
                if (target > current) {
                    current = Math.min(current + step, target);
                } else {
                    current = Math.max(current - step, target);
                }
                
                profile.putClientProperty("hoverProgress", current);
                profileGlowIntensity = current;
                
                if (Math.abs(current - target) < 0.01f) {
                    hoverTimer.stop();
                }
                
                profile.repaint();
            });
            hoverTimer.start();
        }
    });
    
    // Override painting
    profile.setUI(new BasicButtonUI() {
        @Override
        public void paint(Graphics g, javax.swing.JComponent c) {
            JButton btn = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int w = btn.getWidth();
            int h = btn.getHeight();
            float hover = getFloatClientProperty(btn, "hoverProgress", 0.0f);
            
            // Draw outer glow on hover
            if (hover > 0.01f) {
                float intensity = hover;
                int centerX = w / 2;
                int centerY = h / 2;
                int size = Math.min(w, h) - 10;
                int radius = size / 2;
                
                // Draw glowing rings
                for (int i = 0; i < 3; i++) {
                    int alpha = (int)(100 * intensity * (1 - i * 0.3f));
                    g2.setColor(new Color(100, 200, 255, alpha));
                    g2.setStroke(new BasicStroke(3 - i));
                    g2.drawOval(centerX - radius - i - 2, centerY - radius - i - 2, 
                               size + i * 2 + 4, size + i * 2 + 4);
                }
                
                // Draw orbiting particles
                float orbitAngle = (System.currentTimeMillis() * 0.003f) % (float)(Math.PI * 2);
                int numParticles = 6;
                for (int i = 0; i < numParticles; i++) {
                    double angle = orbitAngle + (i * Math.PI * 2 / numParticles);
                    int particleX = centerX + (int)((radius + 15) * Math.cos(angle));
                    int particleY = centerY + (int)((radius + 15) * Math.sin(angle));
                    
                    g2.setColor(new Color(100, 200, 255, (int)(150 * intensity)));
                    g2.fillOval(particleX - 4, particleY - 4, 8, 8);
                }
            }
            
            // Draw the original icon
            super.paint(g, c);
            
            g2.dispose();
        }
    });
}

private void initExitButton() {
    btnExit.setText("");
    btnExit.setIcon(null);
    btnExit.setFont(new Font("Segoe UI", Font.BOLD, 0));
    
    // Store properties
    btnExit.putClientProperty("hoverProgress", 0.0f);
    btnExit.putClientProperty("glowIntensity", 0.0f);
    btnExit.putClientProperty("pulsePhase", 0.0f);
    
    btnExit.setOpaque(false);
    btnExit.setContentAreaFilled(false);
    btnExit.setBorderPainted(false);
    btnExit.setFocusPainted(false);
    btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
    // Add hover effects
    btnExit.addMouseListener(new MouseAdapter() {
        private Timer hoverTimer;
        private Timer warningTimer;
        private boolean exitConfirmed = false;
        
        @Override
        public void mouseEntered(MouseEvent e) {
            startExitHoverAnimation(true);
            
            if (warningTimer == null || !warningTimer.isRunning()) {
                warningTimer = new Timer(50, null);
                warningTimer.addActionListener(evt -> {
                    float pulse = (float)Math.sin(System.currentTimeMillis() * 0.01) * 0.5f + 0.5f;
                    btnExit.putClientProperty("pulsePhase", pulse);
                    btnExit.repaint();
                });
                warningTimer.start();
            }
        }
        
        @Override
        public void mouseExited(MouseEvent e) {
            startExitHoverAnimation(false);
            if (warningTimer != null) {
                warningTimer.stop();
            }
            btnExit.putClientProperty("pulsePhase", 0.0f);
            exitConfirmed = false;
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (!exitConfirmed) {
                exitConfirmed = true;
                btnExit.putClientProperty("warningState", true);
                btnExit.repaint();
                
                Timer resetTimer = new Timer(3000, evt -> {
                    exitConfirmed = false;
                    btnExit.putClientProperty("warningState", false);
                    btnExit.repaint();
                });
                resetTimer.setRepeats(false);
                resetTimer.start();
            } else {
                int result = JOptionPane.showConfirmDialog(
                    DashboardForm.this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                } else {
                    exitConfirmed = false;
                    btnExit.putClientProperty("warningState", false);
                    btnExit.repaint();
                }
            }
        }
        
        private void startExitHoverAnimation(boolean enter) {
            if (hoverTimer != null && hoverTimer.isRunning()) {
                hoverTimer.stop();
            }
            
            hoverTimer = new Timer(10, null);
            hoverTimer.addActionListener(evt -> {
                float current = getFloatClientProperty(btnExit, "hoverProgress", 0.0f);
                float step = 0.15f;
                float target = enter ? 1.0f : 0.0f;
                
                if (target > current) {
                    current = Math.min(current + step, target);
                } else {
                    current = Math.max(current - step, target);
                }
                
                btnExit.putClientProperty("hoverProgress", current);
                btnExit.putClientProperty("glowIntensity", current);
                
                if (Math.abs(current - target) < 0.01f) {
                    hoverTimer.stop();
                }
                
                btnExit.repaint();
            });
            hoverTimer.start();
        }
    });
    
    btnExit.setUI(new BasicButtonUI() {
        @Override
        public void paint(Graphics g, javax.swing.JComponent c) {
            JButton btn = (JButton) c;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            int w = btn.getWidth();
            int h = btn.getHeight();
            float hover = getFloatClientProperty(btn, "hoverProgress", 0.0f);
            float glow = getFloatClientProperty(btn, "glowIntensity", 0.0f);
            float pulse = getFloatClientProperty(btn, "pulsePhase", 0.0f);
            boolean warning = Boolean.TRUE.equals(btn.getClientProperty("warningState"));
            
            // Draw exit button
            int centerX = w / 2;
            int centerY = h / 2;
            int size = Math.min(w, h) - 10;
            
            // Draw circle
            if (hover > 0.1f) {
                float ringSize = size/2 + 2 + pulse * 2;
                g2.setStroke(new BasicStroke(2.0f));
                g2.setColor(new Color(255, 100, 100, (int)(100 * hover)));
                g2.drawOval(centerX - (int)ringSize, centerY - (int)ringSize, 
                           (int)ringSize * 2, (int)ringSize * 2);
            }
            
            // Main circle
            RadialGradientPaint circleGrad = new RadialGradientPaint(
                centerX, centerY, size/2,
                new float[]{0.0f, 0.7f, 1.0f},
                new Color[]{
                    new Color(255, 80, 80, (int)(150 + 50 * glow)),
                    new Color(180, 40, 40, 150),
                    new Color(100, 20, 20, 100)
                }
            );
            g2.setPaint(circleGrad);
            g2.fillOval(centerX - size/2, centerY - size/2, size, size);
            
            // Draw X
            int lineLength = size / 3;
            g2.setStroke(new BasicStroke(3.0f));
            g2.setColor(Color.WHITE);
            g2.drawLine(centerX - lineLength, centerY - lineLength, 
                       centerX + lineLength, centerY + lineLength);
            g2.drawLine(centerX - lineLength, centerY + lineLength, 
                       centerX + lineLength, centerY - lineLength);
            
            g2.dispose();
        }
    });
}

private float getFloatClientProperty(JButton button, String key, float defaultValue) {
    Object value = button.getClientProperty(key);
    if (value instanceof Float) {
        return (Float) value;
    }
    return defaultValue;
}

 private void customInit() {
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);
        
        Background.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });
        
        userPanel = new User();
        managementPanel = new Management();
        servicesPanel = new Services();
        membersPanel = new Members();
        dashboardPanel = new Design();
        transactionPanel = new Transaction();
        profilePanel = new Profile(this);
        
        // Initialize new forms
        loginForm = new LoginForm(this);
        registration1 = new Registration1(this);
        registration2 = new Registration2(this, registration1);
        
        int x = 220, y = 30, w = 570, h = 350;
        
        // Add all panels
        getContentPane().add(dashboardPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(membersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(servicesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(transactionPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(managementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(userPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(profilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        
        // Add new forms
        getContentPane().add(loginForm, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registration1, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registration2, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        
        getContentPane().setComponentZOrder(Background, getContentPane().getComponentCount() - 1);
        
        getContentPane().revalidate();
        getContentPane().repaint();
        
        hideAllPanels();
    }
 
public void hideAllPanels() {
    dashboardPanel.setVisible(false);
    membersPanel.setVisible(false);
    servicesPanel.setVisible(false);
    transactionPanel.setVisible(false);
    managementPanel.setVisible(false);
    userPanel.setVisible(false);
    profilePanel.setVisible(false); 
    profile.setVisible(false); 
    DASHBOARD.setVisible(false);
    MEMBERS.setVisible(false);
    SERVICES.setVisible(false);
    TRANSACTION.setVisible(false);
    MANAGEMENT.setVisible(false);
    btnUser.setVisible(false);
    
    // Hide ALL forms
    loginForm.setVisible(false);
    registration1.setVisible(false);
    registration2.setVisible(false);
    
    if (currentIDPanel != null) {
        currentIDPanel.setVisible(false);
        getContentPane().remove(currentIDPanel);
        currentIDPanel = null; 
    }

    getContentPane().revalidate();
    getContentPane().repaint();   
}

public void showIDCard(String id, String name, String role, String email, String contact) {
    hideAllPanels();

    currentIDPanel = new IDPanel(this, id, name, role, email, contact); 
    
    int x = 220, y = 30, w = 570, h = 350;
    getContentPane().add(currentIDPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
    getContentPane().setComponentZOrder(currentIDPanel, 0); 
    currentIDPanel.setVisible(true);

    getContentPane().revalidate();
    getContentPane().repaint();
}

    public void showPanel(JPanel panel) {
        // Hide all forms if any are visible
        loginForm.setVisible(false);
        registration1.setVisible(false);
        registration2.setVisible(false);
        
        dashboardPanel.setVisible(false);
        membersPanel.setVisible(false);
        servicesPanel.setVisible(false);
        transactionPanel.setVisible(false);
        managementPanel.setVisible(false);
        userPanel.setVisible(false);
        profilePanel.setVisible(false);
        
        if (panel instanceof Design) {
            ((Design) panel).loadDashboardData();
        } else if (panel instanceof Members) {
            ((Members) panel).loadMemberData();
        } else if (panel instanceof Transaction) {
            ((Transaction) panel).loadTransactionData();
        } else if (panel instanceof Management) {
            ((Management) panel).loadBookingData();
        } else if (panel instanceof User) {
            ((User) panel).loadUserData();
        } else if (panel instanceof Profile) {
            ((Profile) panel).loadUserProfile(currentUsername);
        }
        
        panel.setVisible(true);
        panel.requestFocus();
    }
    
public void showLogin() {
    hideAllPanels();
    
    // Show Welcome panel and Logo
    Welcome.setVisible(true);
    Logo.setVisible(true);
    
    // IMPORTANT: Hide both registration forms
    registration1.setVisible(false);
    registration2.setVisible(false);
    
    // Show only login form
    loginForm.setVisible(true);
    loginForm.clearFields();
    
    getContentPane().revalidate();
    getContentPane().repaint();
}


public void showRegistrationStep1() {
    hideAllPanels();
    
    // Show Welcome panel and Logo
    Welcome.setVisible(true);
    Logo.setVisible(true);
    
    // Hide login and registration2
    loginForm.setVisible(false);
    registration2.setVisible(false);
    
    // Show only registration1
    registration1.setVisible(true);
    registration1.clearFields();
    
    getContentPane().revalidate();
    getContentPane().repaint();
}

public void showRegistrationStep2() {
    hideAllPanels();
    
    // Show Welcome panel and Logo
    Welcome.setVisible(true);
    Logo.setVisible(true);
    
    // Hide login and registration1
    loginForm.setVisible(false);
    registration1.setVisible(false);
    
    // Show only registration2
    registration2.setVisible(true);
    registration2.clearSelections();
    
    getContentPane().revalidate();
    getContentPane().repaint();
}


    public void loginSuccess(String username, String role) {
        this.currentUsername = username;
        this.userRole = role;
        hideAllPanels();
        
        Config.setCurrentUser(username, role);
        
        loginForm.setVisible(false);
        registration1.setVisible(false);
        registration2.setVisible(false);
        
        // Hide Welcome panel and Logo after successful login
        Welcome.setVisible(false);
        Logo.setVisible(false);
        
        // Show all navigation buttons
        DASHBOARD.setVisible(true);
        MEMBERS.setVisible(true);
        SERVICES.setVisible(true);
        TRANSACTION.setVisible(true);
        profile.setVisible(true);
        
        boolean isAdmin = role != null && (
                          role.equalsIgnoreCase("Admin") || 
                          role.equalsIgnoreCase("Administrator")
                          );
        
        MANAGEMENT.setVisible(isAdmin);
        btnUser.setVisible(isAdmin);
        
        // Set all buttons to expanded state initially
        JButton[] buttons = {DASHBOARD, MEMBERS, SERVICES, TRANSACTION, MANAGEMENT, btnUser};
        for (JButton button : buttons) {
            if (button.isVisible()) {
                ButtonAnimationState state = buttonStates.get(button);
                if (state != null) {
                    state.keepExpanded = true;
                    state.expandProgress = 1.0f;
                }
                button.setSize(expandedButtonWidth, buttonHeight);
                button.repaint();
            }
        }
        
        showPanel(dashboardPanel);
        getContentPane().revalidate();
        getContentPane().repaint();
    }
    
private javax.swing.ImageIcon getGlowIcon(javax.swing.ImageIcon icon) {
    if (icon == null) return null;
    
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    
    if (w <= 0 || h <= 0) return icon;
    
    java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics g = bi.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    float[] scales = { 0.5f, 0.8f, 2.0f, 1.0f }; 
    float[] offsets = new float[4];
    java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
    return new javax.swing.ImageIcon(op.filter(bi, null));
}  

private void runAnimation(javax.swing.JButton btn, boolean enter) {
    if (enter) {
        btn.setForeground(new Color(255, 180, 100)); 
    } else {
        btn.setForeground(Color.WHITE);
    }
}

private void DASHBOARDActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(dashboardPanel);
}

private void MEMBERSActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(membersPanel);
}

private void SERVICESActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(servicesPanel);
}

private void TRANSACTIONActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(transactionPanel);
}

private void MANAGEMENTActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(managementPanel);
}

private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {
    showPanel(userPanel);
}

private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {}

    void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        userRole = "Guest";
        currentUsername = "";
        
        loginForm.clearFields();
        registration1.clearFields();
        registration2.clearSelections();
        
        showLogin();
    }

private void DASHBOARDMouseEntered(java.awt.event.MouseEvent evt) { 
    runAnimation(DASHBOARD, true); 
}

private void DASHBOARDMouseExited(java.awt.event.MouseEvent evt) { 
    runAnimation(DASHBOARD, false); 
}

private void MEMBERSMouseEntered(java.awt.event.MouseEvent evt) { 
    runAnimation(MEMBERS, true); 
}

private void MEMBERSMouseExited(java.awt.event.MouseEvent evt) { 
    runAnimation(MEMBERS, false); 
}

private void SERVICESMouseEntered(java.awt.event.MouseEvent evt) { 
    runAnimation(SERVICES, true); 
}

private void SERVICESMouseExited(java.awt.event.MouseEvent evt) { 
    runAnimation(SERVICES, false); 
}

private void TRANSACTIONMouseEntered(java.awt.event.MouseEvent evt) {
    runAnimation(TRANSACTION, true);
}

private void TRANSACTIONMouseExited(java.awt.event.MouseEvent evt) {
    runAnimation(TRANSACTION, false);
}

private void MANAGEMENTMouseEntered(java.awt.event.MouseEvent evt) { 
    runAnimation(MANAGEMENT, true); 
}

private void MANAGEMENTMouseExited(java.awt.event.MouseEvent evt) { 
    runAnimation(MANAGEMENT, false); 
}

private void btnUserMouseEntered(java.awt.event.MouseEvent evt) {                                     
    runAnimation(btnUser, true);    
}                                    

private void btnUserMouseExited(java.awt.event.MouseEvent evt) {                                    
    runAnimation(btnUser, false);
}                                   

private void profileMouseEntered(java.awt.event.MouseEvent evt) {                                     
    javax.swing.Icon currentIcon = profile.getIcon();
    if (currentIcon instanceof javax.swing.ImageIcon) {
        originalIcon = currentIcon;
        javax.swing.ImageIcon glowingIcon = getGlowIcon((javax.swing.ImageIcon)originalIcon);
        if (glowingIcon != null) {
            profile.setIcon(glowingIcon);
        }
    }
}                                    

private void profileMouseExited(java.awt.event.MouseEvent evt) {                                    
    if (originalIcon !=     null) {
        profile.setIcon(originalIcon);
    }
}                                   

private void profileActionPerformed(java.awt.event.ActionEvent evt) {                                        
    showPanel(profilePanel);
} 
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Welcome = new javax.swing.JPanel();
        Logo = new javax.swing.JLabel();
        TRANSACTION = new javax.swing.JButton();
        DASHBOARD = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        profile = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        MEMBERS = new javax.swing.JButton();
        SERVICES = new javax.swing.JButton();
        MANAGEMENT = new javax.swing.JButton();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Welcome.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        Logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LOGO.png"))); // NOI18N
        Welcome.add(Logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 220, 180));

        getContentPane().add(Welcome, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 430));

        TRANSACTION.setBackground(new java.awt.Color(51, 51, 51));
        TRANSACTION.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        TRANSACTION.setForeground(new java.awt.Color(255, 255, 255));
        TRANSACTION.setText("TRANSACTION");
        TRANSACTION.setBorderPainted(false);
        TRANSACTION.setContentAreaFilled(false);
        TRANSACTION.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        TRANSACTION.setFocusPainted(false);
        TRANSACTION.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                TRANSACTIONMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                TRANSACTIONMouseExited(evt);
            }
        });
        TRANSACTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TRANSACTIONActionPerformed(evt);
            }
        });
        getContentPane().add(TRANSACTION, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 180, 40));

        DASHBOARD.setBackground(new java.awt.Color(0, 0, 0));
        DASHBOARD.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        DASHBOARD.setForeground(new java.awt.Color(255, 255, 255));
        DASHBOARD.setText("DASHBOARD");
        DASHBOARD.setBorderPainted(false);
        DASHBOARD.setContentAreaFilled(false);
        DASHBOARD.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        DASHBOARD.setFocusPainted(false);
        DASHBOARD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                DASHBOARDMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                DASHBOARDMouseExited(evt);
            }
        });
        DASHBOARD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DASHBOARDActionPerformed(evt);
            }
        });
        getContentPane().add(DASHBOARD, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, 180, 40));

        btnUser.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        btnUser.setForeground(new java.awt.Color(255, 255, 255));
        btnUser.setText("USER PANEL");
        btnUser.setBorderPainted(false);
        btnUser.setContentAreaFilled(false);
        btnUser.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnUser.setFocusPainted(false);
        btnUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnUserMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnUserMouseExited(evt);
            }
        });
        btnUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUserActionPerformed(evt);
            }
        });
        getContentPane().add(btnUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 370, 180, 40));

        profile.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        profile.setForeground(new java.awt.Color(255, 255, 255));
        profile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/logproff.png"))); // NOI18N
        profile.setBorderPainted(false);
        profile.setContentAreaFilled(false);
        profile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        profile.setFocusPainted(false);
        profile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                profileMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                profileMouseExited(evt);
            }
        });
        profile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                profileActionPerformed(evt);
            }
        });
        getContentPane().add(profile, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, 120, 110));

        btnExit.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 0, 0));
        btnExit.setText("X");
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 0, 50, 40));

        MEMBERS.setBackground(new java.awt.Color(51, 51, 51));
        MEMBERS.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        MEMBERS.setForeground(new java.awt.Color(255, 255, 255));
        MEMBERS.setText("MEMBERS");
        MEMBERS.setBorderPainted(false);
        MEMBERS.setContentAreaFilled(false);
        MEMBERS.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MEMBERS.setFocusPainted(false);
        MEMBERS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MEMBERSMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MEMBERSMouseExited(evt);
            }
        });
        MEMBERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MEMBERSActionPerformed(evt);
            }
        });
        getContentPane().add(MEMBERS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 180, 40));

        SERVICES.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        SERVICES.setForeground(new java.awt.Color(255, 255, 255));
        SERVICES.setText("SERVICES");
        SERVICES.setBorderPainted(false);
        SERVICES.setContentAreaFilled(false);
        SERVICES.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        SERVICES.setFocusPainted(false);
        SERVICES.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                SERVICESMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                SERVICESMouseExited(evt);
            }
        });
        SERVICES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SERVICESActionPerformed(evt);
            }
        });
        getContentPane().add(SERVICES, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 180, 40));

        MANAGEMENT.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        MANAGEMENT.setForeground(new java.awt.Color(255, 255, 255));
        MANAGEMENT.setText("MANAGEMENT");
        MANAGEMENT.setBorderPainted(false);
        MANAGEMENT.setContentAreaFilled(false);
        MANAGEMENT.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        MANAGEMENT.setFocusPainted(false);
        MANAGEMENT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                MANAGEMENTMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                MANAGEMENTMouseExited(evt);
            }
        });
        MANAGEMENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MANAGEMENTActionPerformed(evt);
            }
        });
        getContentPane().add(MANAGEMENT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 320, 180, 40));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/OmniDash.png"))); // NOI18N
        getContentPane().add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(-270, 0, 1110, 430));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents



    // ===== MAIN =====
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new DashboardForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JButton DASHBOARD;
    private javax.swing.JLabel Logo;
    private javax.swing.JButton MANAGEMENT;
    private javax.swing.JButton MEMBERS;
    private javax.swing.JButton SERVICES;
    private javax.swing.JButton TRANSACTION;
    private javax.swing.JPanel Welcome;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnUser;
    private javax.swing.JButton profile;
    // End of variables declaration//GEN-END:variables
}