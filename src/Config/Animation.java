    /*
     * Unified Login & Registration Panel with Smooth Expandable Animation
     * 4K HD Quality - Sci-Fi Gym Membership Theme
     * Side-by-Side Layout for Better Visibility
     */
    package Config;

    import Config.Config;
    import OMNIFIT.DashboardForm;
    import java.awt.*;
    import java.awt.event.*;
    import java.util.ArrayList;
    import java.util.List;
    import javax.swing.*;
    import javax.swing.Timer;

    public class Animation extends JPanel {

        private final DashboardForm dashboard;

        // Animation variables
        private float expandProgress = 0f;
        private float loginButtonGlow = 0f;
        private float registerButtonGlow = 0f;
        private float[] fieldGlowIntensity = new float[10];
        private float[] fieldPulsePhase = new float[10];
        private float containerPulse = 0f;
        private float scanlineOffset = 0f;
        private float particlePhase = 0f;
        private float genderIconPulse = 0f;

        private boolean isExpanded = false;
        private boolean isAnimating = false;
        private boolean showingLogin = true; // true = login, false = register
        private boolean showingGenderSelection = false;

        private Timer animationTimer;
        private Timer particleTimer;
        private List<Particle> particles = new ArrayList<>();
        private final int PARTICLE_COUNT = 30;

        // Panel dimensions
        private final int PANEL_WIDTH = 570;
        private final int PANEL_HEIGHT = 350;
        private final int EXPANDED_HEIGHT = 400; // Reduced to 400 for better fit

        // Colors
        private final Color accentCyan = new Color(0, 255, 255, 220);
        private final Color accentOrange = new Color(255, 120, 0, 220);
        private final Color accentPurple = new Color(150, 0, 255, 220);
        private final Color accentPink = new Color(255, 80, 150, 220);
        private final Color textLight = new Color(220, 240, 255);
        private final Color fieldBg = new Color(30, 45, 60, 200);

        // Login components
        private JPanel loginTitleButton;
        private JTextField loginUsernameField;
        private JPasswordField loginPasswordField;
        private JCheckBox rememberCheck;
        private JLabel registerLink;
        private JButton loginButton;

        // Register components - Step 1
        private JPanel registerTitleButton;
        private JTextField regNameField;
        private JTextField regEmailField;
        private JPasswordField regPasswordField;
        private JPasswordField regConfirmField;
        private JTextField regContactField;
        private JButton nextButton;
        private JButton backButton;

        // Register components - Step 2 (Gender)
        private JButton btnMale;
        private JButton btnFemale;
        private JButton registerButton;
        private String selectedGender = "";

        // Mode toggle buttons
        private JButton loginModeButton;
        private JButton registerModeButton;

        // State
        private String currentUsername = "";
        private String currentEmail = "";
        private String currentPassword = "";
        private String currentContact = "";

        public Animation(DashboardForm dashboard) {
            this.dashboard = dashboard;
            Config.initializeDB();

            setOpaque(false);
            setLayout(null);
            setBounds(220, 30, PANEL_WIDTH, PANEL_HEIGHT);

            initComponents();
            initAnimations();
            initParticles();
            setupActions();

            // Start collapsed
            isExpanded = false;
            expandProgress = 0f;
            showingGenderSelection = false;
            updateComponentPositions();
            updateComponentVisibility();
        }

        private void initComponents() {
            // Mode toggle buttons
            createModeToggleButtons();

            // Create login components
            createLoginComponents();

            // Create register components
            createRegisterComponents();

            // Add all components
            add(loginModeButton);
            add(registerModeButton);
            add(loginTitleButton);
            add(registerTitleButton);
            add(loginUsernameField);
            add(loginPasswordField);
            add(rememberCheck);
            add(loginButton);
            add(registerLink);
            add(regNameField);
            add(regEmailField);
            add(regPasswordField);
            add(regConfirmField);
            add(regContactField);
            add(nextButton);
            add(backButton);
            add(btnMale);
            add(btnFemale);
            add(registerButton);

            // Set initial visibility
            updateComponentVisibility();
        }

        private void createModeToggleButtons() {
            // Login mode button
            loginModeButton = new JButton("LOGIN") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    GradientPaint gp;
                    if (showingLogin) {
                        gp = new GradientPaint(0, 0, accentOrange, w, 0, accentCyan);
                    } else {
                        gp = new GradientPaint(0, 0, new Color(60, 60, 70), w, 0, new Color(40, 40, 50));
                    }
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, w, h, 20, 20);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Impact", Font.BOLD, 14));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (w - fm.stringWidth("LOGIN")) / 2;
                    int y = (h + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString("LOGIN", x, y);

                    g2.dispose();
                }
            };
            loginModeButton.setBounds(150, 10, 100, 35);
            loginModeButton.setOpaque(false);
            loginModeButton.setContentAreaFilled(false);
            loginModeButton.setBorderPainted(false);
            loginModeButton.setFocusPainted(false);
            loginModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Register mode button
            registerModeButton = new JButton("REGISTER") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    GradientPaint gp;
                    if (!showingLogin) {
                        gp = new GradientPaint(0, 0, accentOrange, w, 0, accentPurple);
                    } else {
                        gp = new GradientPaint(0, 0, new Color(60, 60, 70), w, 0, new Color(40, 40, 50));
                    }
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, w, h, 20, 20);

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Impact", Font.BOLD, 14));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (w - fm.stringWidth("REGISTER")) / 2;
                    int y = (h + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString("REGISTER", x, y);

                    g2.dispose();
                }
            };
            registerModeButton.setBounds(270, 10, 100, 35);
            registerModeButton.setOpaque(false);
            registerModeButton.setContentAreaFilled(false);
            registerModeButton.setBorderPainted(false);
            registerModeButton.setFocusPainted(false);
            registerModeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        private void createLoginComponents() {
            // Login Title Button (visible when collapsed)
            loginTitleButton = createTitleButton("⚡ LOGIN ⚡", "ACCESS TERMINAL", accentCyan, accentOrange);
            loginTitleButton.setBounds(160, 50, 250, 90);

            // Username field
            loginUsernameField = createTextField("Username or Email", 0);
            loginUsernameField.setBounds(100, 160, 170, 35);

            // Password field
            loginPasswordField = createPasswordField("Password", 1);
            loginPasswordField.setBounds(300, 160, 170, 35);

            // Remember checkbox
            rememberCheck = new JCheckBox("Remember Access");
            rememberCheck.setOpaque(false);
            rememberCheck.setForeground(new Color(180, 180, 180));
            rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            rememberCheck.setFocusPainted(false);
            rememberCheck.setBounds(100, 205, 150, 25);

            // Login button
            loginButton = createActionButton("LOGIN", 0, 300, 200, 170, 35);
            loginButton.addActionListener(e -> handleLogin());

            // Register link
            registerLink = new JLabel("<html><u>Create Account</u></html>");
            registerLink.setForeground(accentCyan);
            registerLink.setFont(new Font("Segoe UI", Font.BOLD, 11));
            registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
            registerLink.setBounds(100, 240, 100, 20);
            registerLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switchToRegister();
                }
            });

        }

        public void showLogin() {
        switchToLogin();
    }

    public void showRegistration() {
        switchToRegister();
    }


        private void createRegisterComponents() {
            // Register Title Button (visible when collapsed)
            registerTitleButton = createTitleButton("⚡ REGISTER ⚡", "NEW USER", accentOrange, accentPurple);
            registerTitleButton.setBounds(160, 50, 250, 90);

            // Name field
            regNameField = createTextField("Full Name", 2);
            regNameField.setBounds(100, 160, 170, 35);

            // Email field
            regEmailField = createTextField("Email Address", 3);
            regEmailField.setBounds(300, 160, 170, 35);

            // Password field
            regPasswordField = createPasswordField("Password", 4);
            regPasswordField.setBounds(100, 205, 170, 35);

            // Confirm password field
            regConfirmField = createPasswordField("Confirm Password", 5);
            regConfirmField.setBounds(300, 205, 170, 35);

            // Contact field
            regContactField = createTextField("Contact Number", 6);
            regContactField.setBounds(200, 250, 170, 35);

            // Gender buttons
            createGenderButtons();

            // Next button (to gender selection)
            nextButton = createActionButton("NEXT →", 0, 350, 300, 120, 30);
            nextButton.addActionListener(e -> expandToGenderSelection());

            // Back button
            backButton = createActionButton("← BACK", 1, 100, 300, 100, 30);
            backButton.addActionListener(e -> switchToLogin());

            // Register button (only visible during gender selection)
            registerButton = createActionButton("COMPLETE REGISTRATION", 0, 160, 300, 250, 35);
            registerButton.addActionListener(e -> handleRegistration());
            registerButton.setName("registerButton");
        }

        private void createGenderButtons() {
            btnMale = new JButton("MALE") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (selectedGender.equals("MALE")) {
                        float pulse = 0.7f + 0.3f * (float)Math.sin(genderIconPulse * 3);
                        for (int i = 0; i < 3; i++) {
                            g2.setColor(new Color(0, 255, 255, (int)(100 * pulse / (i+1))));
                            g2.setStroke(new BasicStroke(3 - i));
                            g2.drawOval(3 - i, 3 - i, getWidth() - 6 + i*2, getHeight() - 30 + i*2);
                        }
                    }

                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            btnMale.setIcon(new ImageIcon(getClass().getResource("/image/Male.png")));
            btnMale.setBounds(160, 160, 100, 100);
            btnMale.setOpaque(false);
            btnMale.setContentAreaFilled(false);
            btnMale.setBorderPainted(false);
            btnMale.setFocusPainted(false);
            btnMale.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMale.setHorizontalTextPosition(SwingConstants.CENTER);
            btnMale.setVerticalTextPosition(SwingConstants.BOTTOM);
            btnMale.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnMale.setForeground(textLight);

            btnFemale = new JButton("FEMALE") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (selectedGender.equals("FEMALE")) {
                        float pulse = 0.7f + 0.3f * (float)Math.sin(genderIconPulse * 3);
                        for (int i = 0; i < 3; i++) {
                            g2.setColor(new Color(255, 80, 150, (int)(100 * pulse / (i+1))));
                            g2.setStroke(new BasicStroke(3 - i));
                            g2.drawOval(3 - i, 3 - i, getWidth() - 6 + i*2, getHeight() - 30 + i*2);
                        }
                    }

                    super.paintComponent(g);
                    g2.dispose();
                }
            };
            btnFemale.setIcon(new ImageIcon(getClass().getResource("/image/Female.png")));
            btnFemale.setBounds(310, 160, 100, 100);
            btnFemale.setOpaque(false);
            btnFemale.setContentAreaFilled(false);
            btnFemale.setBorderPainted(false);
            btnFemale.setFocusPainted(false);
            btnFemale.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnFemale.setHorizontalTextPosition(SwingConstants.CENTER);
            btnFemale.setVerticalTextPosition(SwingConstants.BOTTOM);
            btnFemale.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnFemale.setForeground(textLight);

            btnMale.addActionListener(e -> selectGender("MALE"));
            btnFemale.addActionListener(e -> selectGender("FEMALE"));
        }

        private JPanel createTitleButton(String mainText, String subText, Color color1, Color color2) {
            return new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    int w = getWidth();
                    int h = getHeight();

                    // Background with gradient
                    GradientPaint bgGradient = new GradientPaint(
                        0, 0, new Color(30, 40, 60, 200),
                        w, 0, new Color(20, 30, 50, 200)
                    );
                    g2.setPaint(bgGradient);
                    g2.fillRoundRect(0, 0, w, h, 30, 30);

                    // Border glow
                    float pulse = 0.7f + 0.3f * (float)Math.sin(containerPulse * 3);
                    int borderAlpha = (int)(150 * pulse);
                    GradientPaint borderGradient = new GradientPaint(
                        0, 0, new Color(color1.getRed(), color1.getGreen(), color1.getBlue(), borderAlpha),
                        w, 0, new Color(color2.getRed(), color2.getGreen(), color2.getBlue(), borderAlpha)
                    );
                    g2.setPaint(borderGradient);
                    g2.setStroke(new BasicStroke(2.0f));
                    g2.drawRoundRect(1, 1, w-3, h-3, 30, 30);

                    // Main text
                    g2.setFont(new Font("Impact", Font.BOLD, 24));
                    g2.setColor(Color.WHITE);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (w - fm.stringWidth(mainText)) / 2;
                    g2.drawString(mainText, textX, h/2 - 5);

                    // Sub text
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    fm = g2.getFontMetrics();
                    textX = (w - fm.stringWidth(subText)) / 2;
                    g2.setColor(new Color(200, 200, 200));
                    g2.drawString(subText, textX, h/2 + 15);

                    // Pulsing indicator when collapsed
                    if (!isExpanded) {
                        int alpha = (int)(100 + 100 * Math.sin(containerPulse * 3));
                        g2.setColor(new Color(255, 102, 0, alpha));
                        g2.fillOval(w/2 - 15, h - 15, 30, 5);
                    }

                    g2.dispose();
                }
            };
        }

        private JTextField createTextField(String placeholder, int index) {
            JTextField field = new JTextField(placeholder) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(fieldBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                    if (hasFocus()) {
                        float glow = fieldGlowIntensity[index];
                        if (glow > 0.01f) {
                            g2.setColor(new Color(255, 102, 0, (int)(100 * glow)));
                            g2.setStroke(new BasicStroke(2.0f));
                            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 10, 10);
                        }
                    }

                    g2.setColor(new Color(0, 255, 255, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(5, getHeight()-2, getWidth()-5, getHeight()-2);

                    super.paintComponent(g);
                    g2.dispose();
                }
            };

            field.setOpaque(false);
            field.setBorder(null);
            field.setForeground(new Color(150, 180, 200));
            field.setCaretColor(accentCyan);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(Color.WHITE);
                    }
                    startFieldGlowAnimation(index, true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(placeholder);
                        field.setForeground(new Color(150, 180, 200));
                    }
                    startFieldGlowAnimation(index, false);
                }
            });

            return field;
        }

        private JPasswordField createPasswordField(String placeholder, int index) {
            JPasswordField field = new JPasswordField(placeholder) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2.setColor(fieldBg);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                    if (hasFocus()) {
                        float glow = fieldGlowIntensity[index];
                        if (glow > 0.01f) {
                            g2.setColor(new Color(255, 102, 0, (int)(100 * glow)));
                            g2.setStroke(new BasicStroke(2.0f));
                            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 10, 10);
                        }
                    }

                    g2.setColor(new Color(255, 120, 0, 100));
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawLine(5, getHeight()-2, getWidth()-5, getHeight()-2);

                    super.paintComponent(g);
                    g2.dispose();
                }
            };

            field.setOpaque(false);
            field.setBorder(null);
            field.setForeground(new Color(150, 180, 200));
            field.setCaretColor(accentOrange);
            field.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            field.setEchoChar((char)0);

            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(placeholder)) {
                        field.setText("");
                        field.setForeground(Color.WHITE);
                        field.setEchoChar('•');
                    }
                    startFieldGlowAnimation(index, true);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getPassword().length == 0) {
                        field.setEchoChar((char)0);
                        field.setText(placeholder);
                        field.setForeground(new Color(150, 180, 200));
                    }
                    startFieldGlowAnimation(index, false);
                }
            });

            return field;
        }

        private JButton createActionButton(String text, int index, int x, int y, int w, int h) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    float hover = getFloatClientProperty(this, "hoverProgress", 0.0f);

                    GradientPaint gp;
                    if (index == 0) {
                        gp = new GradientPaint(0, 0, new Color(0, 150, 255, 200),
                                              w, 0, new Color(0, 255, 255, 200));
                    } else {
                        gp = new GradientPaint(0, 0, new Color(255, 120, 0, 200),
                                              w, 0, new Color(255, 200, 0, 200));
                    }
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, w, h, 15, 15);

                    if (hover > 0.01f) {
                        int alpha = (int)(150 * hover);
                        g2.setColor(new Color(255, 255, 255, alpha));
                        g2.setStroke(new BasicStroke(2));
                        g2.drawRoundRect(1, 1, w-3, h-3, 15, 15);
                    }

                    g2.setColor(Color.WHITE);
                    g2.setFont(new Font("Impact", Font.BOLD, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = (w - fm.stringWidth(text)) / 2;
                    int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(text, textX, textY);

                    g2.dispose();
                }
            };

            button.setBounds(x, y, w, h);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.putClientProperty("hoverProgress", 0.0f);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    startButtonHover(button, true, index);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    startButtonHover(button, false, index);
                }
            });

            return button;
        }

        private void initAnimations() {
            animationTimer = new Timer(16, e -> {
                if (isAnimating) {
                    float delta = 0.08f;
                    if (isExpanded) {
                        expandProgress = Math.min(expandProgress + delta, 1.0f);
                    } else {
                        expandProgress = Math.max(expandProgress - delta, 0.0f);
                    }

                    containerPulse += 0.05f;
                    for (int i = 0; i < fieldPulsePhase.length; i++) {
                        fieldPulsePhase[i] += 0.1f;
                    }

                    updateComponentPositions();
                    updateComponentVisibility();
                    repaint();

                    if ((isExpanded && expandProgress >= 1.0f) || 
                        (!isExpanded && expandProgress <= 0.0f)) {
                        isAnimating = false;
                    }
                }
            });
        }

        private void initParticles() {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                particles.add(new Particle());
            }

            particleTimer = new Timer(50, e -> {
                particlePhase += 0.05f;
                for (Particle p : particles) {
                    p.update();
                }
                repaint();
            });
            particleTimer.start();
        }

        private void updateComponentPositions() {
            int baseY = 50;
            int spacing = 45;
            float easedProgress = easeOutCubic(expandProgress);

            // Mode buttons - always visible
            loginModeButton.setLocation(150, 10);
            registerModeButton.setLocation(270, 10);

            if (showingLogin) {
                // Login mode
                loginTitleButton.setBounds(160, (int)(baseY + (1 - easedProgress) * 20), 250, 90);

                if (expandProgress > 0.1f) {
                    loginUsernameField.setBounds(100, (int)(baseY + 110 + (1 - easedProgress) * 20), 170, 35);
                    loginPasswordField.setBounds(300, (int)(baseY + 110 + (1 - easedProgress) * 20), 170, 35);
                    rememberCheck.setBounds(100, (int)(baseY + 155 + (1 - easedProgress) * 20), 150, 25);
                    loginButton.setBounds(300, (int)(baseY + 150 + (1 - easedProgress) * 20), 170, 35);
                    registerLink.setBounds(100, (int)(baseY + 190 + (1 - easedProgress) * 20), 100, 20);
                }
            } else {
                // Register mode
                registerTitleButton.setBounds(160, (int)(baseY + (1 - easedProgress) * 20), 250, 90);

                if (expandProgress > 0.1f) {
                    if (showingGenderSelection) {
                        // Gender selection view
                        btnMale.setBounds(160, (int)(baseY + 110 + (1 - easedProgress) * 20), 100, 100);
                        btnFemale.setBounds(310, (int)(baseY + 110 + (1 - easedProgress) * 20), 100, 100);
                        registerButton.setBounds(160, (int)(baseY + 220 + (1 - easedProgress) * 20), 250, 35);
                    } else {
                        // Step 1 fields
                        regNameField.setBounds(100, (int)(baseY + 110 + (1 - easedProgress) * 20), 170, 35);
                        regEmailField.setBounds(300, (int)(baseY + 110 + (1 - easedProgress) * 20), 170, 35);
                        regPasswordField.setBounds(100, (int)(baseY + 155 + (1 - easedProgress) * 20), 170, 35);
                        regConfirmField.setBounds(300, (int)(baseY + 155 + (1 - easedProgress) * 20), 170, 35);
                        regContactField.setBounds(200, (int)(baseY + 200 + (1 - easedProgress) * 20), 170, 35);
                        nextButton.setBounds(350, (int)(baseY + 250 + (1 - easedProgress) * 20), 120, 30);
                        backButton.setBounds(100, (int)(baseY + 250 + (1 - easedProgress) * 20), 100, 30);
                    }
                }
            }
        }

        private void updateComponentVisibility() {
            float threshold = 0.1f;
            boolean visible = expandProgress > threshold;

            // Always hide both title buttons initially
            loginTitleButton.setVisible(false);
            registerTitleButton.setVisible(false);

            if (showingLogin) {
                // Show login title button
                loginTitleButton.setVisible(true);

                // Hide all register components
                regNameField.setVisible(false);
                regEmailField.setVisible(false);
                regPasswordField.setVisible(false);
                regConfirmField.setVisible(false);
                regContactField.setVisible(false);
                btnMale.setVisible(false);
                btnFemale.setVisible(false);
                nextButton.setVisible(false);
                backButton.setVisible(false);
                registerButton.setVisible(false);

                // Show login components based on expansion
                loginUsernameField.setVisible(visible);
                loginPasswordField.setVisible(visible);
                rememberCheck.setVisible(visible);
                loginButton.setVisible(visible);
                registerLink.setVisible(visible);

            } else {
                // Show register title button
                registerTitleButton.setVisible(true);

                // Hide all login components
                loginUsernameField.setVisible(false);
                loginPasswordField.setVisible(false);
                rememberCheck.setVisible(false);
                loginButton.setVisible(false);
                registerLink.setVisible(false);

                if (showingGenderSelection) {
                    // Gender selection view
                    regNameField.setVisible(false);
                    regEmailField.setVisible(false);
                    regPasswordField.setVisible(false);
                    regConfirmField.setVisible(false);
                    regContactField.setVisible(false);
                    nextButton.setVisible(false);
                    backButton.setVisible(false);

                    btnMale.setVisible(visible);
                    btnFemale.setVisible(visible);
                    registerButton.setVisible(visible);
                } else {
                    // Step 1 fields
                    regNameField.setVisible(visible);
                    regEmailField.setVisible(visible);
                    regPasswordField.setVisible(visible);
                    regConfirmField.setVisible(visible);
                    regContactField.setVisible(visible);
                    btnMale.setVisible(false);
                    btnFemale.setVisible(false);
                    registerButton.setVisible(false);

                    nextButton.setVisible(visible);
                    backButton.setVisible(visible);
                }
            }
        }

        private void setupActions() {
            // Mode toggle actions
            loginModeButton.addActionListener(e -> {
                if (!showingLogin) {
                    switchToLogin();
                }
            });

            registerModeButton.addActionListener(e -> {
                if (showingLogin) {
                    switchToRegister();
                }
            });

            // Title button clicks to expand
            loginTitleButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (showingLogin) toggle();
                }
            });

            registerTitleButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (!showingLogin) toggle();
                }
            });
        }

    private void switchToLogin() {
        showingLogin = true;
        showingGenderSelection = false;
        selectedGender = "";
        isExpanded = false;
        expandProgress = 0f;
        updateComponentPositions();
        updateComponentVisibility();
        repaint();
    }    
        private void switchToRegister() {
            showingLogin = false;
            showingGenderSelection = false;
            selectedGender = "";
            isExpanded = false;
            expandProgress = 0f;
            updateComponentPositions();
            updateComponentVisibility();
            repaint();
        }

    private void expandToGenderSelection() {
        if (validateStep1()) {
            // Save step 1 data
            currentUsername = regNameField.getText();
            currentEmail = regEmailField.getText();
            currentPassword = new String(regPasswordField.getPassword());
            currentContact = regContactField.getText();

            showingGenderSelection = true;
            expand(); // This will trigger the animation

            // Force update positions immediately
            updateComponentPositions();
            updateComponentVisibility();
            repaint();
        }
    }

        private boolean validateStep1() {
            String name = regNameField.getText();
            String email = regEmailField.getText();
            String pass = new String(regPasswordField.getPassword());
            String confirm = new String(regConfirmField.getPassword());
            String contact = regContactField.getText();

            if (name.isEmpty() || name.equals("Full Name") ||
                email.isEmpty() || email.equals("Email Address") ||
                pass.isEmpty() || pass.equals("Password") ||
                confirm.isEmpty() || confirm.equals("Confirm Password") ||
                contact.isEmpty() || contact.equals("Contact Number")) {
                showMessage("ERROR", "Please fill all fields!", true);
                return false;
            }

            if (!name.matches("^[a-zA-Z\\s]{1,20}$")) {
                showMessage("INVALID NAME", "Name must be text only!", true);
                return false;
            }

            if (!email.contains("@")) {
                showMessage("INVALID EMAIL", "Email must contain @ symbol!", true);
                return false;
            }

            if (Config.isUserExists(name)) {
                showMessage("USERNAME TAKEN", "Username already exists!", true);
                return false;
            }

            if (Config.isEmailExists(email)) {
                showMessage("EMAIL TAKEN", "Email already registered!", true);
                return false;
            }

            if (!pass.equals(confirm)) {
                showMessage("PASSWORD MISMATCH", "Passwords do not match!", true);
                return false;
            }

            if (!contact.matches("^\\d{11}$")) {
                showMessage("INVALID CONTACT", "Contact must be 11 digits!", true);
                return false;
            }

            return true;
        }

        private void selectGender(String gender) {
            selectedGender = gender;
            startGenderPulse();
            repaint();
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

    private void handleLogin() {
        String username = loginUsernameField.getText();
        String password = new String(loginPasswordField.getPassword());

        if (username.isEmpty() || username.equals("Username or Email") ||
            password.isEmpty() || password.equals("Password")) {
            showMessage("ACCESS DENIED", "Please enter credentials!", true);
            return;
        }

        String role = Config.getUserRole(username, password);

        if (role != null) {
            showMessage("ACCESS GRANTED", "Welcome, " + username + "!", false);

            Timer timer = new Timer(1000, e -> {
                // First, login success
                dashboard.loginSuccess(username, role);

                // Ensure dashboard panel is visible and front
                dashboard.showPanel(dashboard.dashboardPanel);

                // Add a subtle animation to the dashboard panel
                dashboard.dashboardPanel.setVisible(true);

                // Optional: Trigger any data loading in the dashboard
                if (dashboard.dashboardPanel instanceof Design) {
                    ((Design) dashboard.dashboardPanel).loadDashboardData();
                }

                // Bring dashboard to front
                dashboard.dashboardPanel.requestFocus();
                dashboard.dashboardPanel.repaint();
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            showMessage("ACCESS DENIED", "Invalid credentials!", true);
        }
    }

    private void handleRegistration() {
        if (selectedGender.isEmpty()) {
            showMessage("GENDER REQUIRED", "Please select a gender!", true);
            return;
        }

        String customID = generateNextOMNIId();

        boolean success = Config.registerUser(
            customID, currentUsername, currentPassword,
            currentEmail, currentContact, selectedGender,
            "User", "Active", 0.0
        );

        if (success) {
            showMessage("REGISTRATION SUCCESS", "ID: " + customID, false);

            Timer timer = new Timer(1500, e -> {
                // Clear fields in this panel
                clearFields();

                // Reset this panel's state (but don't show it)
                showingLogin = true;
                showingGenderSelection = false;
                selectedGender = "";
                isExpanded = false;
                expandProgress = 0f;

                // IMPORTANT: Hide this panel first
                setVisible(false);

                // Then show the ID card
                dashboard.showIDCard(customID, currentUsername, "User", currentEmail, currentContact);

                // DO NOT call dashboard.showLogin() here - that will override the ID card
                // The ID card has its own "GO TO LOGIN" button that will call showLogin()
            });
            timer.setRepeats(false);
            timer.start();
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

        private String generateNextOMNIId() {
            String lastId = Config.getLastUserID();
            int nextNumber = (lastId != null && lastId.startsWith("OMNI-00-")) 
                             ? Integer.parseInt(lastId.substring(8)) + 1 : 1001;
            return "OMNI-00-" + nextNumber;
        }

        private void startFieldGlowAnimation(int index, boolean enter) {
            Timer glowTimer = new Timer(10, null);
            glowTimer.addActionListener(e -> {
                if (enter) {
                    fieldGlowIntensity[index] = Math.min(fieldGlowIntensity[index] + 0.1f, 1.0f);
                } else {
                    fieldGlowIntensity[index] = Math.max(fieldGlowIntensity[index] - 0.1f, 0.0f);
                }

                if (Math.abs(fieldGlowIntensity[index] - (enter ? 1.0f : 0.0f)) < 0.01f) {
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            glowTimer.start();
        }

        private void startButtonHover(JButton button, boolean enter, int index) {
            Timer hoverTimer = new Timer(10, null);
            hoverTimer.addActionListener(e -> {
                float target = enter ? 1.0f : 0.0f;
                float current = getFloatClientProperty(button, "hoverProgress", 0.0f);

                if (enter) {
                    current = Math.min(current + 0.15f, target);
                } else {
                    current = Math.max(current - 0.15f, target);
                }

                button.putClientProperty("hoverProgress", current);

                if (index == 0) {
                    loginButtonGlow = current;
                } else {
                    registerButtonGlow = current;
                }

                if (Math.abs(current - target) < 0.01f) {
                    ((Timer)e.getSource()).stop();
                }
                repaint();
            });
            hoverTimer.start();
        }

        private float easeOutCubic(float x) {
            return (float)(1 - Math.pow(1 - x, 3));
        }

        private float getFloatClientProperty(JComponent comp, String key, float defaultValue) {
            Object value = comp.getClientProperty(key);
            return (value instanceof Float) ? (Float) value : defaultValue;
        }

        public void expand() {
            if (!isExpanded && !isAnimating) {
                isExpanded = true;
                isAnimating = true;
                animationTimer.start();
            }
        }

        public void collapse() {
            if (isExpanded && !isAnimating) {
                isExpanded = false;
                isAnimating = true;
                animationTimer.start();
            }
        }

        public void toggle() {
            if (isExpanded) {
                collapse();
            } else {
                expand();
            }
        }

        public boolean isExpanded() {
            return isExpanded;
        }

    public void clearFields() {
        loginUsernameField.setText("Username or Email");
        loginPasswordField.setText("Password");
        loginPasswordField.setEchoChar((char)0);

        regNameField.setText("Full Name");
        regEmailField.setText("Email Address");
        regPasswordField.setText("Password");
        regPasswordField.setEchoChar((char)0);
        regConfirmField.setText("Confirm Password");
        regConfirmField.setEchoChar((char)0);
        regContactField.setText("Contact Number");

        selectedGender = "";
        showingGenderSelection = false;

        // Force login mode
        showingLogin = true;
        isExpanded = false;
        expandProgress = 0f;

        // Update UI
        updateComponentPositions();
        updateComponentVisibility();
        repaint();
    }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth();
            int h = getHeight();

            // Background gradient
            GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(5, 5, 15, 30),
                w, h, new Color(15, 5, 25, 30)
            );
            g2.setPaint(bgGradient);
            g2.fillRect(0, 0, w, h);

            // Draw particles
            for (Particle p : particles) {
                p.draw(g2);
            }

            // Draw grid
            drawGrid(g2, w, h);

            // Draw scanning line
            scanlineOffset = (scanlineOffset + 0.5f) % h;
            g2.setColor(new Color(0, 255, 255, 30));
            g2.fillRect(0, (int)scanlineOffset, w, 2);

            g2.dispose();
            super.paintComponent(g);
        }

        private void drawGrid(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(0, 255, 255, 20));
            g2.setStroke(new BasicStroke(0.5f));

            for (int x = 0; x < w; x += 40) {
                float glow = (float)Math.sin(x * 0.02 + particlePhase) * 0.5f + 0.5f;
                g2.setColor(new Color(0, 255, 255, (int)(15 * glow)));
                g2.drawLine(x, 0, x, h);
            }

            for (int y = 0; y < h; y += 40) {
                float glow = (float)Math.sin(y * 0.02 + particlePhase * 2) * 0.5f + 0.5f;
                g2.setColor(new Color(255, 102, 0, (int)(15 * glow)));
                g2.drawLine(0, y, w, y);
            }
        }

        private class Particle {
            float x, y;
            float vx, vy;
            float size;
            float alpha;
            Color color;

            Particle() {
                x = (float)Math.random() * PANEL_WIDTH;
                y = (float)Math.random() * PANEL_HEIGHT;
                vx = (float)(Math.random() - 0.5f) * 0.5f;
                vy = (float)(Math.random() - 0.5f) * 0.5f;
                size = (float)Math.random() * 2 + 1;
                alpha = (float)Math.random() * 0.5f + 0.3f;
                color = Math.random() > 0.5 ? accentCyan : accentOrange;
            }

            void update() {
                x += vx;
                y += vy;

                if (x < 0 || x > PANEL_WIDTH || y < 0 || y > PANEL_HEIGHT) {
                    x = (float)Math.random() * PANEL_WIDTH;
                    y = (float)Math.random() * PANEL_HEIGHT;
                }

                alpha = 0.3f + 0.3f * (float)Math.sin(particlePhase + x * 0.01);
            }

            void draw(Graphics2D g2) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 100)));
                g2.fillOval((int)x, (int)y, (int)size, (int)size);
            }
        }
    }