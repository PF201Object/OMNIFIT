package OMNIFIT;

import Config.Config;
import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public final class Members extends javax.swing.JPanel {

    public Members() {
        initComponents();
        setOpaque(false);
        applyDashboardTheme(); 
        loadMemberData(); 
    }

		private void applyDashboardTheme() {
			tableScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			tableScroll.setOpaque(false);
			tableScroll.getViewport().setOpaque(false);

			tblEvents.setRowHeight(35);
			tblEvents.setShowGrid(false);
			tblEvents.setIntercellSpacing(new Dimension(0, 0));
			
			// FIX: Solid background color stops text from overlapping/ghosting
			tblEvents.setBackground(new Color(30, 30, 30)); 
			tblEvents.setForeground(new Color(220, 220, 220)); 
			tblEvents.setSelectionBackground(new Color(255, 102, 0, 100)); // Subtle orange selection
			
			tblEvents.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
			tblEvents.getTableHeader().setBackground(new Color(25, 25, 25)); 
			tblEvents.getTableHeader().setForeground(new Color(180, 180, 180));
			
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			
			// CRITICAL FIX: Set to true so each cell clears its previous frame
			centerRenderer.setOpaque(true); 
			tblEvents.setDefaultRenderer(Object.class, centerRenderer);
		}

		public void loadMemberData() {
			String[] columnNames = {"ID", "Name", "Contact", "Email", "Joined", "Status", "Type"};
			DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
				@Override
				public boolean isCellEditable(int row, int column) { return false; }
			};
			
			// FIX: Clear existing rows before loading fresh data
			model.setRowCount(0);

			String sql = "SELECT M_ID, Name, Contact_No, Email, Join_date, Membership_Status, Membership_Type FROM Members";

			try (Connection conn = Config.connect();
				 PreparedStatement pst = conn.prepareStatement(sql);
				 ResultSet rs = pst.executeQuery()) {

				while (rs.next()) {
					model.addRow(new Object[]{
						rs.getInt("M_ID"),
						rs.getString("Name"),
						rs.getString("Contact_No"),
						rs.getString("Email"),
						rs.getString("Join_date"),
						rs.getString("Membership_Status"),
						rs.getString("Membership_Type")
					});
				}
				tblEvents.setModel(model);

			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
			}
		}

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        tblEvents = new javax.swing.JTable();

        setOpaque(false);
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblTitle.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        lblTitle.setForeground(new java.awt.Color(255, 255, 255));
        lblTitle.setText("GYM MEMBERS");
        add(lblTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 290, 30));

        tableScroll.setBorder(null);
        tableScroll.setOpaque(false);

        tblEvents.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tableScroll.setViewportView(tblEvents);

        add(tableScroll, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, 530, 270));
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblTitle;
    private javax.swing.JScrollPane tableScroll;
    private javax.swing.JTable tblEvents;
}// End of variables declaration//GEN-END:variables