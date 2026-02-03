package OMNIFIT;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class DashboardForm extends javax.swing.JFrame {

    // Panel Declarations
    private Profile profilePanel;    
    private User userPanel;
    private Management managementPanel;
    private Services servicesPanel;
    private Members membersPanel;
    private LoginForm loginPanel;
    private RegistrationForm registerPanel;

    // State Variables
    private final int homeOriginalY;
    private String userRole = "Guest";
    private String currentUsername = ""; 
    private int mouseX, mouseY;

    public DashboardForm() {
        initComponents();
        homeOriginalY = 10; // Fixed Y coordinate from your XML
        customInit();
        showLogin();
        
    }

    private void customInit() {
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);

        // Movement Logic for Undecorated Frame
        Background.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });

        // Initialize all Sub-Panels
        userPanel = new User();
        managementPanel = new Management();
        servicesPanel = new Services();
        membersPanel = new Members();
        loginPanel = new LoginForm(this);
        registerPanel = new RegistrationForm(this);
        profilePanel = new Profile();

        // Layout Constraints matching your design space
        int x = 220, y = 30, w = 570, h = 350;

        getContentPane().add(userPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(managementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(servicesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(membersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(loginPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(profilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));

        // Background indexing
        getContentPane().setComponentZOrder(Background, getContentPane().getComponentCount() - 1);
        hideAllPanels();
    }

    public void hideAllPanels() {
        userPanel.setVisible(false);
        managementPanel.setVisible(false);
        servicesPanel.setVisible(false);
        membersPanel.setVisible(false);
        loginPanel.setVisible(false);
        registerPanel.setVisible(false);
        profilePanel.setVisible(false); 

        logo.setVisible(true);
        ABOUT.setVisible(true);
        btnUser.setVisible(false);
        MEMBERS.setVisible(false);
        SERVICES.setVisible(false);
        MANAGEMENT.setVisible(false);
        btnLogout.setVisible(false);
        profile.setVisible(false); 
    }

    private void showPanel(JPanel panel) {
        if (loginPanel.isVisible() || registerPanel.isVisible()) {
             if (!userRole.equals("Guest")) {
                 loginPanel.setVisible(false);
                 registerPanel.setVisible(false);
             } else { return; }
        }

        userPanel.setVisible(false);
        managementPanel.setVisible(false);
        servicesPanel.setVisible(false);
        membersPanel.setVisible(false);
        profilePanel.setVisible(false);

        // Load specific data when panel is shown
        if (panel instanceof Profile) {
            ((Profile) panel).loadUserProfile(currentUsername);
        }

        panel.setVisible(true);
        panel.requestFocus();
    }

    public void loginSuccess(String username, String role) {
    this.currentUsername = username;
    this.userRole = role;
        hideAllPanels();
                
        ABOUT.setVisible(false);
        btnLogout.setVisible(true);
        MEMBERS.setVisible(true);
        SERVICES.setVisible(true);
        profile.setVisible(true); 
        
        // Role-based access
        boolean isAdmin = "Administrator".equalsIgnoreCase(role);
        MANAGEMENT.setVisible(isAdmin); 
        btnUser.setVisible(isAdmin);    
        
        showPanel(membersPanel);
    }

    public void showLogin() { hideAllPanels(); loginPanel.setVisible(true); }
    public void showRegister() { hideAllPanels(); registerPanel.setVisible(true); }

    private void runAnimation(javax.swing.JButton btn, boolean enter) {
        btn.setForeground(enter ? new Color(0, 153, 255) : Color.WHITE);
        Timer timer = new Timer(10, e -> {
            if (enter && btn.getY() > homeOriginalY - 6) {
                btn.setLocation(btn.getX(), btn.getY() - 1);
            } else if (!enter && btn.getY() < homeOriginalY) {
                btn.setLocation(btn.getX(), btn.getY() + 1);
            } else { ((Timer) e.getSource()).stop(); }
        });
        timer.start();
    }

    // Event Handlers
    private void MEMBERSActionPerformed(java.awt.event.ActionEvent evt) { showPanel(membersPanel); }
    private void SERVICESActionPerformed(java.awt.event.ActionEvent evt) { showPanel(servicesPanel); }
    private void MANAGEMENTActionPerformed(java.awt.event.ActionEvent evt) { showPanel(managementPanel); }
    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) { showPanel(userPanel); }
    private void profileActionPerformed(java.awt.event.ActionEvent evt) { showPanel(profilePanel); }
    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) { System.exit(0); }
    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) { 
        userRole = "Guest"; 
        currentUsername = "";
        showLogin(); 
    }
    private void ABOUTActionPerformed(java.awt.event.ActionEvent evt) { /* Logic for About panel */ }

    // ===== MOUSE EVENTS (Handlers for Animations) =====
    private void MEMBERSMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(MEMBERS, true); }
    private void MEMBERSMouseExited(java.awt.event.MouseEvent evt) { runAnimation(MEMBERS, false); }
    private void SERVICESMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(SERVICES, true); }
    private void SERVICESMouseExited(java.awt.event.MouseEvent evt) { runAnimation(SERVICES, false); }
    private void MANAGEMENTMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(MANAGEMENT, true); }
    private void MANAGEMENTMouseExited(java.awt.event.MouseEvent evt) { runAnimation(MANAGEMENT, false); }
    private void profileMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(profile, true); }
    private void profileMouseExited(java.awt.event.MouseEvent evt) { runAnimation(profile, false); }
    private void ABOUTMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(ABOUT, true); }
    private void ABOUTMouseExited(java.awt.event.MouseEvent evt) { runAnimation(ABOUT, false); }
    private void btnUserMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(btnUser, true); }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logo = new javax.swing.JLabel();
        profile = new javax.swing.JButton();
        ABOUT = new javax.swing.JButton();
        btnUser = new javax.swing.JButton();
        btnExit = new javax.swing.JButton();
        MEMBERS = new javax.swing.JButton();
        SERVICES = new javax.swing.JButton();
        MANAGEMENT = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        Background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LOGO.png"))); 
        getContentPane().add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 260, 220));

        profile.setFont(new java.awt.Font("Serif", 1, 18)); 
        profile.setForeground(new java.awt.Color(255, 255, 255));
        profile.setText("PROFILE");
        profile.setBorderPainted(false);
        profile.setContentAreaFilled(false);
        profile.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        profile.addActionListener(evt -> profileActionPerformed(evt));
        profile.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { profileMouseEntered(evt); }
            public void mouseExited(MouseEvent evt) { profileMouseExited(evt); }
        });
        getContentPane().add(profile, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 10, 140, 40));

        ABOUT.setFont(new java.awt.Font("Serif", 1, 18)); 
        ABOUT.setForeground(new java.awt.Color(255, 255, 255));
        ABOUT.setText("ABOUT US");
        ABOUT.setBorderPainted(false);
        ABOUT.setContentAreaFilled(false);
        ABOUT.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ABOUT.addActionListener(evt -> ABOUTActionPerformed(evt));
        getContentPane().add(ABOUT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 40));

        btnUser.setFont(new java.awt.Font("Serif", 1, 18)); 
        btnUser.setForeground(new java.awt.Color(255, 255, 255));
        btnUser.setText("USER PANEL");
        btnUser.setBorderPainted(false);
        btnUser.setContentAreaFilled(false);
        btnUser.addActionListener(evt -> btnUserActionPerformed(evt));
        getContentPane().add(btnUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 10, 180, 40));

        btnExit.setFont(new java.awt.Font("Serif", 1, 24)); 
        btnExit.setForeground(new java.awt.Color(255, 0, 0));
        btnExit.setText("X");
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.addActionListener(evt -> btnExitActionPerformed(evt));
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(790, 0, 50, 40));

        MEMBERS.setFont(new java.awt.Font("Serif", 1, 18)); 
        MEMBERS.setForeground(new java.awt.Color(255, 255, 255));
        MEMBERS.setText("MEMBERS");
        MEMBERS.setBorderPainted(false);
        MEMBERS.setContentAreaFilled(false);
        MEMBERS.addActionListener(evt -> MEMBERSActionPerformed(evt));
        getContentPane().add(MEMBERS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 40));

        SERVICES.setFont(new java.awt.Font("Serif", 1, 18)); 
        SERVICES.setForeground(new java.awt.Color(255, 255, 255));
        SERVICES.setText("SERVICES");
        SERVICES.setBorderPainted(false);
        SERVICES.setContentAreaFilled(false);
        SERVICES.addActionListener(evt -> SERVICESActionPerformed(evt));
        getContentPane().add(SERVICES, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 130, 40));

        MANAGEMENT.setFont(new java.awt.Font("Serif", 1, 18)); 
        MANAGEMENT.setForeground(new java.awt.Color(255, 255, 255));
        MANAGEMENT.setText("MANAGEMENT");
        MANAGEMENT.setBorderPainted(false);
        MANAGEMENT.setContentAreaFilled(false);
        MANAGEMENT.addActionListener(evt -> MANAGEMENTActionPerformed(evt));
        getContentPane().add(MANAGEMENT, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 10, 180, 40));

        btnLogout.setFont(new java.awt.Font("Serif", 1, 24)); 
        btnLogout.setForeground(new java.awt.Color(255, 102, 102));
        btnLogout.setText("LOGOUT");
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.addActionListener(evt -> btnLogoutActionPerformed(evt));
        getContentPane().add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 370, 160, 50));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/OmniDash.png"))); 
        getContentPane().add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(-270, 0, 1110, 430));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new DashboardForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ABOUT;
    private javax.swing.JLabel Background;
    private javax.swing.JButton MANAGEMENT;
    private javax.swing.JButton MEMBERS;
    private javax.swing.JButton SERVICES;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnUser;
    private javax.swing.JLabel logo;
    private javax.swing.JButton profile;
    // End of variables declaration//GEN-END:variables
    
    // Variables declaration - do not modify                                                                                                                                                   
    private void btnUserMouseExited(java.awt.event.MouseEvent evt) {
        runAnimation(btnUser, false);
    }
                                        
}