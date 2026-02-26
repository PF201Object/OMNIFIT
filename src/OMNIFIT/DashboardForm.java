package OMNIFIT;

import Config.Config;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class DashboardForm extends javax.swing.JFrame {
    
    private Timer animationTimer; // Declare this as a class member variable
    private javax.swing.Icon originalIcon;
    private User userPanel;
    private Management managementPanel;
    private Services servicesPanel;
    private Members membersPanel;
    private LoginForm loginPanel;
    private Registration1 registerPanel1; // Renamed for clarity
    private Registration2 registerPanel2; // New Panel
    private Profile profilePanel;    
    private IDPanel currentIDPanel;

    private final int homeOriginalY;
    private String userRole = "Guest";
    private String currentUsername = ""; 
    private int mouseX, mouseY;

    public DashboardForm() {
        initComponents();
        homeOriginalY = MEMBERS.getY();
        customInit();
        showLogin();
    }

    private void customInit() {
        this.setBackground(new Color(0, 0, 0, 0));
        this.setLocationRelativeTo(null);

        // Drag window logic
        Background.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
        });
        
        // Initialize Panels
        userPanel = new User();
        managementPanel = new Management();
        servicesPanel = new Services();
        membersPanel = new Members();
        loginPanel = new LoginForm(this);
        registerPanel1 = new Registration1(this);
        registerPanel2 = new Registration2(this, registerPanel1); // Pass ref to step 1
        profilePanel = new Profile(this);

        int x = 220, y = 30, w = 570, h = 350;

        // Add panels to the Content Pane
        getContentPane().add(userPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(managementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(servicesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(membersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(loginPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registerPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registerPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(profilePanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));

        getContentPane().setComponentZOrder(Background, getContentPane().getComponentCount() - 1);
        
        getContentPane().revalidate();
        getContentPane().repaint();

        hideAllPanels();
    }

    public void hideAllPanels() {
        userPanel.setVisible(false);
        managementPanel.setVisible(false);
        servicesPanel.setVisible(false);
        membersPanel.setVisible(false);
        loginPanel.setVisible(false);
        registerPanel1.setVisible(false);
        registerPanel2.setVisible(false);
        profilePanel.setVisible(false); 
        profile.setVisible(false); 
        btnUser.setVisible(false);
        MEMBERS.setVisible(false);
        SERVICES.setVisible(false);
        MANAGEMENT.setVisible(false);
        if (currentIDPanel != null) {
        currentIDPanel.setVisible(false);
        getContentPane().remove(currentIDPanel); // Optional: removes it from memory
        currentIDPanel = null; 
    }

    getContentPane().revalidate();
    getContentPane().repaint();   
    }
    
    public void showIDCard(String id, String name, String role, String email, String contact) {
    hideAllPanels();

    // REMOVE 'IDPanel' from the start of the next line so it uses the class variable
    currentIDPanel = new IDPanel(this, id, name, role, email, contact); 
    
    int x = 220, y = 30, w = 570, h = 350;
    getContentPane().add(currentIDPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
    getContentPane().setComponentZOrder(currentIDPanel, 0); 
    currentIDPanel.setVisible(true);

    getContentPane().revalidate();
    getContentPane().repaint();
}

    private void showPanel(JPanel panel) {
        if (loginPanel.isVisible() || registerPanel1.isVisible() || registerPanel2.isVisible()) {
             if (!userRole.equals("Guest")) {
                 loginPanel.setVisible(false);
                 registerPanel1.setVisible(false);
                 registerPanel2.setVisible(false);
             } else {
                 return;
             }
        }

        userPanel.setVisible(false);
        managementPanel.setVisible(false);
        servicesPanel.setVisible(false);
        membersPanel.setVisible(false);
        profilePanel.setVisible(false);

        if (panel instanceof User) {
            ((User) panel).loadUserData();
        } else if (panel instanceof Management) {
            ((Management) panel).loadBookingData();
        } else if (panel instanceof Members) {
            ((Members) panel).loadMemberData();
        } else if (panel instanceof Profile) {
            ((Profile) panel).loadUserProfile(currentUsername);
        }

        panel.setVisible(true);
        panel.requestFocus(); 
    }

    public void showLogin() {
        hideAllPanels();
        loginPanel.setVisible(true);
    }
    
    // Updated Registration Steps
    public void showRegistrationStep1() {
        hideAllPanels();
        registerPanel1.setVisible(true);
    }

    public void showRegistrationStep2() {
        hideAllPanels();
        registerPanel2.setVisible(true);
    }
    
    

   public void loginSuccess(String username, String role) {
    this.currentUsername = username;
    this.userRole = role; // This comes from the DB
    hideAllPanels();
    
    Config.setCurrentUser(username, role);
          
    loginPanel.setVisible(false);
    
    // Show Standard Features
    MEMBERS.setVisible(true);
    SERVICES.setVisible(true);
    profile.setVisible(true); 
    
    // IMPROVED ADMIN CHECK:
    // This handles "Admin", "ADMIN", "Administrator", and "administrator"
    boolean isAdmin = role != null && (
                      role.equalsIgnoreCase("Admin") || 
                      role.equalsIgnoreCase("Administrator")
                      );
    
    // Admin-only features
    MANAGEMENT.setVisible(isAdmin); 
    btnUser.setVisible(isAdmin);
    
    membersPanel.loadMemberData(); 
    showPanel(membersPanel);
    
    getContentPane().revalidate();
    getContentPane().repaint();
}

    // ===== ANIMATION =====
    private javax.swing.ImageIcon getGlowIcon(javax.swing.ImageIcon icon) {
    int w = icon.getIconWidth();
    int h = icon.getIconHeight();
    java.awt.image.BufferedImage bi = new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics g = bi.createGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();

    // The scale factors: {Red, Green, Blue, Alpha}
    // We boost Blue (1.5f) and Green (1.2f) to create a Cyan/Blue glow effect
    float[] scales = { 0.5f, 0.8f, 2.0f, 1.0f }; 
    float[] offsets = new float[4];
    java.awt.image.RescaleOp op = new java.awt.image.RescaleOp(scales, offsets, null);
    return new javax.swing.ImageIcon(op.filter(bi, null));
    }  
    
private void runAnimation(javax.swing.JButton btn, boolean enter) {
    if (enter) {
        btn.setForeground(new Color(255, 102, 0)); 
        // btn.setBackground(new Color(60, 60, 70)); // Uncomment if you want background change too
    } else {
        btn.setForeground(Color.WHITE);
        // btn.setBackground(new Color(45, 45, 55));
    }
}

// Add this method to your DashboardForm class to handle the hover events:

    // ===== BUTTON ACTIONS =====
    private void MEMBERSActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(membersPanel);
    }

    private void SERVICESActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(servicesPanel);
    }

    private void MANAGEMENTActionPerformed(java.awt.event.ActionEvent evt) {
        showPanel(managementPanel);
    }

    private void btnUserMouseClicked(java.awt.event.MouseEvent evt) {
        showPanel(userPanel);
    }

    private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
    userRole = "Guest";
    currentUsername = ""; // Clear the stored username too
    
    // This is the important part:
    // It calls the clearFields method we added to your LoginForm
    if (loginPanel != null) {
        loginPanel.clearFields();
    }
    
    showLogin();
    }

    // ===== HOVER EFFECTS =====
    private void MEMBERSMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(MEMBERS, true); }
    private void MEMBERSMouseExited(java.awt.event.MouseEvent evt) { runAnimation(MEMBERS, false); }

    private void SERVICESMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(SERVICES, true); }
    private void SERVICESMouseExited(java.awt.event.MouseEvent evt) { runAnimation(SERVICES, false); }

    private void MANAGEMENTMouseEntered(java.awt.event.MouseEvent evt) { runAnimation(MANAGEMENT, true); }
    private void MANAGEMENTMouseExited(java.awt.event.MouseEvent evt) { runAnimation(MANAGEMENT, false); }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        getContentPane().add(btnUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 160, 40));

        profile.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        profile.setForeground(new java.awt.Color(255, 255, 255));
        profile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/Profil1e.png"))); // NOI18N
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
        getContentPane().add(profile, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 120, 90));

        btnExit.setFont(new java.awt.Font("Verdana", 1, 18)); // NOI18N
        btnExit.setForeground(new java.awt.Color(255, 0, 0));
        btnExit.setText("CLOSE");
        btnExit.setBorderPainted(false);
        btnExit.setContentAreaFilled(false);
        btnExit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExitActionPerformed(evt);
            }
        });
        getContentPane().add(btnExit, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 390, 130, 40));

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
        getContentPane().add(MEMBERS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 140, 40));

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
        getContentPane().add(SERVICES, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 140, 40));

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
        getContentPane().add(MANAGEMENT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, 180, 40));

        Background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Dashboard.image/OmniDash.png"))); // NOI18N
        getContentPane().add(Background, new org.netbeans.lib.awtextra.AbsoluteConstraints(-270, 0, 1110, 430));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnUserMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUserMouseEntered
        runAnimation(btnUser, true);    
    }//GEN-LAST:event_btnUserMouseEntered

    private void btnUserMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnUserMouseExited
        runAnimation(btnUser, false);
    }//GEN-LAST:event_btnUserMouseExited

    private void btnUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUserActionPerformed
        showPanel(userPanel);
    }//GEN-LAST:event_btnUserActionPerformed

    private void profileMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileMouseEntered
    originalIcon = profile.getIcon(); 
    profile.setIcon(getGlowIcon((javax.swing.ImageIcon)originalIcon));
    }//GEN-LAST:event_profileMouseEntered

    private void profileMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_profileMouseExited
    if (originalIcon != null) {
        profile.setIcon(originalIcon);
    }
    }//GEN-LAST:event_profileMouseExited
    
    private void profileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_profileActionPerformed
        showPanel(profilePanel);
    }//GEN-LAST:event_profileActionPerformed

    // ===== MAIN =====
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new DashboardForm().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Background;
    private javax.swing.JButton MANAGEMENT;
    private javax.swing.JButton MEMBERS;
    private javax.swing.JButton SERVICES;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnUser;
    private javax.swing.JButton profile;
    // End of variables declaration//GEN-END:variables
}