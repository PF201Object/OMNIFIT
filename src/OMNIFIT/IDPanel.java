package OMNIFIT;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.*;

public class IDPanel extends javax.swing.JPanel {

    private final DashboardForm dashboard;
    private boolean isFront = true;
    private float flipScale = 1.0f; 
    private Timer flipTimer;

    public IDPanel(DashboardForm dashboard, String id, String name, String role, String email, String contact) {
        this.dashboard = dashboard;
        initComponents();
        
        // Data Initialization
        lblID.setText("" + id);
        lblName.setText("" + name.toUpperCase());
        lblRole.setText("" + role);
        lblEmail.setText("" + email);
        lblContacts.setText("+64-" + contact);
        
        Color primaryColor = new Color(92, 225, 230); // Bright Cyan
        Color hoverColor = new Color(130, 240, 245);  // Lighter Cyan
        Color textColor = new Color(20, 60, 80); 
        
        btnDone.setBackground(primaryColor);
        btnDone.setForeground(textColor);
        btnDone.setFocusPainted(false);
        btnDone.setBorder(null);
        addButtonHoverEffect(btnDone, primaryColor, hoverColor);
        
        btnBack.setBackground(primaryColor);
        btnBack.setForeground(textColor);
        btnBack.setFocusPainted(false);
        btnBack.setBorder(null);
        addButtonHoverEffect(btnBack, primaryColor, hoverColor);
        
        btnFront.setBackground(primaryColor);
        btnFront.setForeground(textColor);
        btnFront.setFocusPainted(false);
        btnFront.setBorder(null);
        addButtonHoverEffect(btnFront, primaryColor, hoverColor);
        // Initial State Setup
        
        Back.setVisible(false);
        lblContacts.setVisible(false);
        lblID.setVisible(false);
        lblName.setVisible(false);
        lblRole.setVisible(false);
        lblEmail.setVisible(false);
        lblBackSideInfo.setVisible(true);
        lblBackSideInfo1.setVisible(true);
        btnDone.setVisible(false);
        btnFront.setVisible(false);
        btnBack.setVisible(false);

        // Delayed display of controls
        Timer startTimer = new Timer(1000, e -> {
            btnDone.setVisible(true);
            btnFront.setVisible(true);
            btnBack.setVisible(true);
            revalidate();
            repaint();
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }
            private void addButtonHoverEffect(JButton button, Color baseColor, Color hoverColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });
    }

    private void performFlip(boolean targetFront) {
        if (isFront == targetFront || (flipTimer != null && flipTimer.isRunning())) return;

        flipTimer = new Timer(15, null);
        flipTimer.addActionListener(e -> {
            if (isFront != targetFront) {
                flipScale -= 0.1f;
                if (flipScale <= 0) {
                    flipScale = 0;
                    isFront = targetFront;
                    toggleCardContent(isFront);
                }
            } else {
                flipScale += 0.1f;
                if (flipScale >= 1.0f) {
                    flipScale = 1.0f;
                    flipTimer.stop();
                }
            }
            repaint();
        });
        flipTimer.start();
    }

    private void toggleCardContent(boolean frontVisible) {
        // Backgrounds
        Front.setVisible(frontVisible);
        Back.setVisible(!frontVisible);
        
        // Data Labels
        lblID.setVisible(!frontVisible);
        lblContacts.setVisible(!frontVisible);
        lblName.setVisible(!frontVisible);
        lblRole.setVisible(!frontVisible);
        lblEmail.setVisible(!frontVisible);
        
        lblBackSideInfo.setVisible(frontVisible);
        lblBackSideInfo1.setVisible(frontVisible);
    }

    /**
     * NetBeans generated code modified to match your XML structure.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        business = new javax.swing.JLabel();
        lblID = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblContacts = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblBackSideInfo1 = new javax.swing.JLabel();
        lblBackSideInfo = new javax.swing.JLabel();
        Front = new javax.swing.JLabel();
        Back = new javax.swing.JLabel();
        btnFront = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        btnDone = new javax.swing.JButton();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        business.setBackground(new java.awt.Color(0, 153, 204));
        business.setFont(new java.awt.Font("Serif", 1, 24)); // NOI18N
        business.setForeground(new java.awt.Color(0, 204, 255));
        business.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        business.setText("Welcome, This is Your Business Card!");
        add(business, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 420, -1));

        lblID.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        lblID.setForeground(new java.awt.Color(255, 255, 255));
        add(lblID, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 220, 200, 20));

        lblName.setBackground(new java.awt.Color(255, 255, 255));
        lblName.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblName.setForeground(new java.awt.Color(255, 255, 255));
        add(lblName, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, 200, 20));

        lblContacts.setBackground(new java.awt.Color(255, 255, 255));
        lblContacts.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        lblContacts.setForeground(new java.awt.Color(255, 255, 255));
        add(lblContacts, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 190, 200, 20));

        lblRole.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 130, 200, 20));

        lblEmail.setBackground(new java.awt.Color(255, 255, 255));
        lblEmail.setFont(new java.awt.Font("Serif", 0, 14)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(255, 255, 255));
        add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 160, 200, 20));

        lblBackSideInfo1.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N
        lblBackSideInfo1.setForeground(new java.awt.Color(255, 255, 255));
        lblBackSideInfo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBackSideInfo1.setText("www.Omni-Fit.com");
        add(lblBackSideInfo1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, 420, -1));

        lblBackSideInfo.setBackground(new java.awt.Color(255, 255, 255));
        lblBackSideInfo.setFont(new java.awt.Font("Serif", 1, 36)); // NOI18N
        lblBackSideInfo.setForeground(new java.awt.Color(255, 255, 255));
        lblBackSideInfo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblBackSideInfo.setText("OMNIFIT");
        add(lblBackSideInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 420, -1));

        Front.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Front.png"))); // NOI18N
        add(Front, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 80, 490, 200));

        Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/Back.png"))); // NOI18N
        add(Back, new org.netbeans.lib.awtextra.AbsoluteConstraints(-10, 80, 490, 200));

        btnFront.setText("FRONT");
        btnFront.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFrontActionPerformed(evt);
            }
        });
        add(btnFront, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 300, 100, 40));

        btnBack.setText("BACK");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 300, 100, 40));

        btnDone.setText("GO TO LOGIN");
        btnDone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDoneActionPerformed(evt);
            }
        });
        add(btnDone, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 300, 150, 40));
    }// </editor-fold>//GEN-END:initComponents

    private void btnFrontActionPerformed(java.awt.event.ActionEvent evt) {
        performFlip(true);
    }

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {
        performFlip(false);
    }

    private void btnDoneActionPerformed(java.awt.event.ActionEvent evt) {
        dashboard.btnLogoutActionPerformed(evt);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel Back;
    private javax.swing.JLabel Front;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDone;
    private javax.swing.JButton btnFront;
    private javax.swing.JLabel business;
    private javax.swing.JLabel lblBackSideInfo;
    private javax.swing.JLabel lblBackSideInfo1;
    private javax.swing.JLabel lblContacts;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblID;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblRole;
    // End of variables declaration//GEN-END:variables
    }