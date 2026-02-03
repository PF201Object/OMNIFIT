package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Management extends javax.swing.JPanel {

    public Management() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        loadBookingData(); // Keeps same method name for Dashboard compatibility
    }

	private void applyDashboardTheme() {
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		tableScroll.setOpaque(false);
		tableScroll.getViewport().setOpaque(false);
		tableScroll.setViewportBorder(null);

		tblBookings.setRowHeight(35);
		tblBookings.setShowGrid(false);
		tblBookings.setIntercellSpacing(new Dimension(0, 0));
		
		// FIX: Use a solid color to prevent overlapping text artifacts
		tblBookings.setBackground(new Color(30, 30, 30)); 
		tblBookings.setForeground(new Color(220, 220, 220)); 
		tblBookings.setSelectionBackground(new Color(255, 102, 0, 100)); // Orange highlight
		tblBookings.setSelectionForeground(Color.WHITE);
		
		tblBookings.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblBookings.getTableHeader().setBackground(new Color(25, 25, 25)); 
		tblBookings.getTableHeader().setForeground(new Color(180, 180, 180));
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		// FIX: Set to true so the cell erases old text before painting new text
		centerRenderer.setOpaque(true); 
		tblBookings.setDefaultRenderer(Object.class, centerRenderer);
	}

    /**
     * Updated to match your Management Table Schema: 
     * Staffid, Username, Role_Position, Salary_PayRate, WorkEmail
     */
	public void loadBookingData() {
		String[] columnNames = {"Staff ID", "Username", "Role", "Pay Rate", "Work Email"};
		DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int column) { return false; }
		};
		
		// FIX: Clear existing rows before loading fresh data from the database
		model.setRowCount(0);

		String sql = "SELECT Staffid, Username, Role_Position, Salary_PayRate, WorkEmail FROM Management";

		try (Connection conn = Config.connect();
			 PreparedStatement pst = conn.prepareStatement(sql);
			 ResultSet rs = pst.executeQuery()) {

			while (rs.next()) {
				model.addRow(new Object[]{
					rs.getInt("Staffid"),
					rs.getString("Username"),
					rs.getString("Role_Position"),
					"₱ " + String.format("%,.2f", rs.getDouble("Salary_PayRate")), // Added comma for thousands
					rs.getString("WorkEmail")
				});
			}
			tblBookings.setModel(model);

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
		}
	}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Glassmorphism background
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
        tblBookings = new javax.swing.JTable();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); 
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("STAFF MANAGEMENT");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 290, 30));

        tableScroll.setOpaque(false);
        tblBookings.setModel(new javax.swing.table.DefaultTableModel(new Object [][] {}, new String [] {}));
        tableScroll.setViewportView(tblBookings);

        // Adjusted constraints to fit the panel better
        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 530, 270));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblBookings;
}// End of variables declaration//GEN-END:variables