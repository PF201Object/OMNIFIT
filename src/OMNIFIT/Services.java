package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Services extends javax.swing.JPanel {

    public Services() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        loadMenuData(); // Kept method name for consistency with your existing dashboard calls
    }

    private void applyDashboardTheme() {
        tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroll.setOpaque(false);
        tableScroll.getViewport().setOpaque(false);
        tableScroll.setViewportBorder(null);

        tblMenu.setRowHeight(35);
        tblMenu.setShowGrid(false);
        tblMenu.setIntercellSpacing(new Dimension(0, 0));
        
        tblMenu.setBackground(new Color(15, 15, 15, 40)); 
        tblMenu.setForeground(new Color(220, 220, 220)); 
        tblMenu.setSelectionBackground(new Color(255, 255, 255, 30)); 
        tblMenu.setSelectionForeground(Color.WHITE);
        
        tblMenu.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblMenu.getTableHeader().setBackground(new Color(25, 25, 25)); 
        tblMenu.getTableHeader().setForeground(new Color(180, 180, 180));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setOpaque(false); 
        tblMenu.setDefaultRenderer(Object.class, centerRenderer);
    }

    public void loadMenuData() {
        // Updated columns to match your Services table in Config.java
        String[] columnNames = {"S_ID", "Service Name", "Type", "Fee", "Assigned Staff", "Member ID"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        String sql = "SELECT S_ID, Service_Name, Service_Type, Payment_Status, Staff_Assigned, Member_ID FROM Services";

        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("S_ID"),
                    rs.getString("Service_Name"),
                    rs.getString("Service_Type"),
                    "₱ " + String.format("%.2f", rs.getDouble("Payment_Status")),
                    rs.getString("Staff_Assigned"),
                    rs.getInt("Member_ID")
                });
            }
            tblMenu.setModel(model);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Consistent glass panel styling
        g2.setColor(new Color(10, 10, 10, 180)); 
        g2.fillRoundRect(10, 10, getWidth()-20, getHeight()-20, 30, 30); 
        
        g2.dispose();
        super.paintComponent(g);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        tableScroll = new javax.swing.JScrollPane();
        tblMenu = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("GYM SERVICES");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 290, 30));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblMenu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableScroll.setViewportView(tblMenu);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 530, 270));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblMenu;
    // End of variables declaration//GEN-END:variables
    
    // Variables declaration - do not modify                                                                                                                                                                                        
}