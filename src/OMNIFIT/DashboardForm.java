package OMNIFIT;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class DashboardForm extends javax.swing.JFrame {

    private User userPanel;
    private Management managementPanel;
    private Services servicesPanel;
    private Members membersPanel;
    private LoginForm loginPanel;
    private RegistrationForm registerPanel;

    private final int homeOriginalY;
    private String userRole = "Guest";
    private int mouseX, mouseY;

    public DashboardForm() {
        initComponents();
        // Set homeOriginalY BEFORE adding panels
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
        registerPanel = new RegistrationForm(this);

        int x = 220, y = 30, w = 570, h = 350;

        // Add panels to the Content Pane
        getContentPane().add(userPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(managementPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(servicesPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(membersPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(loginPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));
        getContentPane().add(registerPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(x, y, w, h));

        // CRITICAL FIX: Ensure the Background is at the absolute back (highest index)
        // and buttons/panels are at the front (lowest index)
        getContentPane().setComponentZOrder(Background, getContentPane().getComponentCount() - 1);
        
        // Refresh UI
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
        registerPanel.setVisible(false);
        logo.setVisible(true);
        ABOUT.setVisible(true);

        btnUser.setVisible(false);
        MEMBERS.setVisible(false);
        SERVICES.setVisible(false);
        MANAGEMENT.setVisible(false);
        btnLogout.setVisible(false);
    }

		private void showPanel(JPanel panel) {
			// Only block if login/register are actually visible
			if (loginPanel.isVisible() || registerPanel.isVisible()) {
				 if (!userRole.equals("Guest")) {
					 loginPanel.setVisible(false);
					 registerPanel.setVisible(false);
				 } else {
					 return;
				 }
			}

			userPanel.setVisible(false);
			managementPanel.setVisible(false);
			servicesPanel.setVisible(false);
			membersPanel.setVisible(false);

			// REFRESH LOGIC: Check which panel is being shown and reload its data
			if (panel instanceof User) {
				((User) panel).loadUserData();
			} else if (panel instanceof Management) {
				((Management) panel).loadBookingData();
			} else if (panel instanceof Members) {
				((Members) panel).loadMemberData();
			}


			panel.setVisible(true);
			panel.requestFocus(); 
		}

    public void showLogin() {
        hideAllPanels();
        loginPanel.setVisible(true);
    }
    
    public void showRegister() {
        hideAllPanels();
        registerPanel.setVisible(true);
    }

	public void loginSuccess(String role) {
		this.userRole = role;
		hideAllPanels();
                
                ABOUT.setVisible(false);
                logo.setVisible(true);
		btnLogout.setVisible(true);
		MEMBERS.setVisible(true);
		SERVICES.setVisible(true);
		boolean isAdmin = "Administrator".equalsIgnoreCase(role);
		
		MANAGEMENT.setVisible(isAdmin); 
		btnUser.setVisible(isAdmin);    
		
		// Refresh members specifically on login
		membersPanel.loadMemberData(); 
		showPanel(membersPanel);
		
		getContentPane().revalidate();
		getContentPane().repaint();
	}

    // ===== ANIMATION =====
    private void runAnimation(javax.swing.JButton btn, boolean enter) {
        btn.setForeground(enter ? new Color(0, 153, 255) : Color.WHITE);

        Timer timer = new Timer(10, e -> {
            if (enter && btn.getY() > homeOriginalY - 6) {
                btn.setLocation(btn.getX(), btn.getY() - 1);
            } else if (!enter && btn.getY() < homeOriginalY) {
                btn.setLocation(btn.getX(), btn.getY() + 1);
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

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

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {
        userRole = "Guest";
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

        logo = new javax.swing.JLabel();
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

        logo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/LOGO.png"))); // NOI18N
        getContentPane().add(logo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 260, 220));

        ABOUT.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        ABOUT.setForeground(new java.awt.Color(255, 255, 255));
        ABOUT.setText("ABOUT US");
        ABOUT.setBorderPainted(false);
        ABOUT.setContentAreaFilled(false);
        ABOUT.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        ABOUT.setFocusPainted(false);
        ABOUT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                ABOUTMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                ABOUTMouseExited(evt);
            }
        });
        ABOUT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ABOUTActionPerformed(evt);
            }
        });
        getContentPane().add(ABOUT, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 160, 40));

        btnUser.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
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
        getContentPane().add(btnUser, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 10, 180, 40));

        btnExit.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
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

        MEMBERS.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
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
        getContentPane().add(MEMBERS, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 130, 40));

        SERVICES.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
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
        getContentPane().add(SERVICES, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, 130, 40));

        MANAGEMENT.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
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
        getContentPane().add(MANAGEMENT, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 10, 180, 40));

        btnLogout.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        btnLogout.setForeground(new java.awt.Color(255, 102, 102));
        btnLogout.setText("LOGOUT");
        btnLogout.setBorderPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });
        getContentPane().add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 370, 160, 50));

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

    private void ABOUTMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ABOUTMouseEntered
        runAnimation(ABOUT, true);    
    }//GEN-LAST:event_ABOUTMouseEntered

    private void ABOUTMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ABOUTMouseExited
        runAnimation(ABOUT, false);
    }//GEN-LAST:event_ABOUTMouseExited

    private void ABOUTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ABOUTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ABOUTActionPerformed

    // ===== MAIN =====
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
    // End of variables declaration//GEN-END:variables
}
