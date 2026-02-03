package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class User extends javax.swing.JPanel {

    public User() {
        initComponents(); 
        setOpaque(false);
        applyDashboardTheme(); 
        loadUserData();        
    }

	private void applyDashboardTheme() {
		tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
		tableScroll.setOpaque(false);
		tableScroll.getViewport().setOpaque(false);

		tblUsers.setRowHeight(35);
		tblUsers.setShowGrid(false);
		
		// FIX: Use a solid color to prevent text overlapping
		tblUsers.setBackground(new Color(30, 30, 30)); 
		tblUsers.setForeground(new Color(220, 220, 220)); 
                tblUsers.setSelectionBackground(new Color(255, 102, 0, 100)); // Orange highlight
		tblUsers.setSelectionForeground(Color.WHITE);
		
		tblUsers.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
		tblUsers.getTableHeader().setBackground(new Color(25, 25, 25)); 
		tblUsers.getTableHeader().setForeground(new Color(180, 180, 180));
		// Create a renderer that properly clears the background
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		// CRITICAL: Set this to true so the cell erases old text before drawing new text
		centerRenderer.setOpaque(true); 
		
		tblUsers.setDefaultRenderer(Object.class, centerRenderer);
	}

		public void loadUserData() {
			String[] columnNames = {"ID", "Username", "Email Address", "Contact #", "User Role", "Account Status"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
				@Override
				public boolean isCellEditable(int row, int column) { return false; }
			};
			model.setRowCount(0);

			// Corrected SQL: Joining Users and Management to get the Role
			String sql = "SELECT U.U_ID, U.Username, U.Email, U.Contact_No, M.Role_Position, U.Members_Status " +
						 "FROM Users U " +
						 "LEFT JOIN Management M ON U.Username = M.Username";

			try (Connection conn = Config.connect();
				 PreparedStatement pst = conn.prepareStatement(sql);
				 ResultSet rs = pst.executeQuery()) {
				
				while (rs.next()) {
					model.addRow(new Object[]{
						rs.getInt("U_ID"),
						rs.getString("Username"), // Use Username instead of Name
						rs.getString("Email"),
						rs.getString("Contact_No"),
						rs.getString("Role_Position"), // From Management table
						rs.getString("Members_Status") // Correct column name
					});
				}
				tblUsers.setModel(model);
				
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "User Load Error: " + e.getMessage());
				e.printStackTrace();
			}
		}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Consistent glass panel styling (fills the container)
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
        tblUsers = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("USER RECORDS");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 400, 30));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableScroll.setViewportView(tblUsers);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 530, 270));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
    
    // Variables declaration - do not modify                                                                                                                                                                                        
}