package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;

public final class Profile extends javax.swing.JPanel {

    public Profile() {
        initComponents();
        setOpaque(false);
    }

    public void loadUserProfile(String username) {
        String sql = "SELECT U.Username, U.Email, U.Members_Status, M.Role_Position, M.Salary_PayRate " +
                     "FROM Users U LEFT JOIN Management M ON U.Username = M.Username WHERE U.Username = ?";

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                lblUsername.setText(rs.getString("Username").toUpperCase());
                lblEmail.setText("EMAIL: " + rs.getString("Email"));
                lblStatus.setText("STATUS: " + rs.getString("Members_Status"));
                lblRole.setText("POSITION: " + (rs.getString("Role_Position") != null ? rs.getString("Role_Position") : "Member"));
                lblSalary.setText("SALARY: ₱" + String.format("%.2f", rs.getDouble("Salary_PayRate")));
            }
        } catch (SQLException e) { System.err.println(e.getMessage()); }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(15, 15, 15, 200)); 
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); 
        g2.dispose();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        lblTitle = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        lblSalary = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 22)); 
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setText("ACCOUNT PROFILE");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 25, -1, -1));

        lblUsername.setFont(new java.awt.Font("SansSerif", 1, 18)); 
        lblUsername.setForeground(new Color(0, 153, 255));
        add(lblUsername, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 400, -1));

        lblRole.setForeground(Color.LIGHT_GRAY);
        add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 400, -1));

        lblEmail.setForeground(Color.LIGHT_GRAY);
        add(lblEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 400, -1));

        lblSalary.setForeground(Color.LIGHT_GRAY);
        add(lblSalary, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 400, -1));

        lblStatus.setForeground(new Color(255, 153, 0));
        add(lblStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, 400, -1));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables 
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblSalary;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUsername;
    // End of variables declaration//GEN-END:variables 
    //GEN-FIRST:event_btnUserMouseExited 
    // TODO add your handling code here: 
    }//GEN-FIRST:event_btnUserMouseExited