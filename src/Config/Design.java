package Config;

import Config.Config;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.Timer;

public final class Design extends JPanel {
    
    // Dashboard metrics
    private int activeMembers = 0;
    private int activeStaff = 0;
    private int pendingAuthorizations = 0;
    private int totalServices = 0;
    private double totalTransactions = 0.0;
    
    // Animation variables
    private float[] glowIntensities = new float[5];
    private float[] pulsePhases = new float[5];
    private float[] floatOffsets = new float[5];
    private Timer animationTimer;
    private Timer refreshTimer;

    // Color schemes for each card
    private final Color[][] cardGradients = {
        {   // Active Members Card - Blue/Teal theme
            new Color(0, 180, 220, 240),
            new Color(0, 120, 200, 220),
            new Color(0, 80, 160, 200),
            new Color(0, 40, 120, 180)
        },
        {   // Active Staff Card - Green theme
            new Color(0, 220, 120, 240),
            new Color(0, 180, 100, 220),
            new Color(0, 140, 80, 200),
            new Color(0, 100, 60, 180)
        },
        {   // Pending Auth Card - Orange/Amber theme
            new Color(255, 160, 0, 240),
            new Color(220, 130, 0, 220),
            new Color(180, 100, 0, 200),
            new Color(140, 70, 0, 180)
        },
        {   // Total Services Card - Purple theme
            new Color(180, 100, 255, 240),
            new Color(140, 60, 220, 220),
            new Color(100, 40, 180, 200),
            new Color(70, 20, 140, 180)
        },
        {   // Total Transactions Card - Gold theme
            new Color(255, 215, 0, 240),
            new Color(220, 180, 0, 220),
            new Color(180, 140, 0, 200),
            new Color(140, 100, 0, 180)
        }
    };
    
    // Glow colors for each card
    private final Color[] glowColors = {
        new Color(0, 200, 255, 100),    // Blue glow for Members
        new Color(0, 255, 150, 100),    // Green glow for Staff
        new Color(255, 180, 50, 100),   // Orange glow for Pending
        new Color(200, 120, 255, 100),  // Purple glow for Services
        new Color(255, 215, 100, 100)   // Gold glow for Transactions
    };
    
    // Icons for each card (using Unicode)
    private final String[] cardIcons = {"💪", "💼", "⏳", "🏋️", "💰"};
    
    // Card titles
    private final String[] cardTitles = {
        "ACTIVE MEMBERS",
        "ACTIVE STAFF",
        "PENDING AUTH",
        "TOTAL SERVICES",
        "TOTAL TRANS"
    };
    
    public Design() {
        setOpaque(false);
        setLayout(null);
        
        // Initialize animation arrays
        for (int i = 0; i < 5; i++) {
            glowIntensities[i] = 0.5f + (float)Math.random() * 0.3f;
            pulsePhases[i] = (float)(Math.random() * Math.PI * 2);
            floatOffsets[i] = 0f;
        }
        
        // Start animation timer
        startAnimation();
        
        startAutoRefresh();

        // Load initial data
        loadDashboardData();
    }
    
     private void startAutoRefresh() {
        refreshTimer = new Timer(3000, e -> {
            // Only refresh if panel is visible
            if (isVisible()) {
                loadDashboardData();
            }
        });
        refreshTimer.start();
    }
    
    private void startAnimation() {
        animationTimer = new Timer(16, e -> {
            long currentTime = System.nanoTime();
            float deltaTime = 0.016f;
            
            for (int i = 0; i < 5; i++) {
                // Update pulse phases
                pulsePhases[i] += deltaTime * 2.0f;
                
                // Update glow intensities with subtle breathing effect
                glowIntensities[i] = 0.6f + 0.2f * (float)Math.sin(pulsePhases[i] * 1.5f);
                
                // Update float offsets for 3D effect
                floatOffsets[i] = (float)Math.sin(pulsePhases[i] * 2) * 3;
            }
            
            repaint();
        });
        animationTimer.start();
    }
    
    public void loadDashboardData() {
        // Fetch Active Members
        activeMembers = getActiveMembersCount();
        
        // Fetch Active Staff
        activeStaff = getActiveStaffCount();
        
        // Fetch Pending Authorizations (from Payments table where status = 'Pending')
        pendingAuthorizations = getPendingAuthorizationsCount();
        
        // Fetch Total Services
        totalServices = getTotalServicesCount();
        
        // Fetch Total Transactions Amount
        totalTransactions = getTotalTransactionsAmount();
        
        repaint();
    }
    
    private int getActiveMembersCount() {
        String sql = "SELECT COUNT(*) FROM Members WHERE Membership_Status = 'Active'";
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active members: " + e.getMessage());
        }
        return 0;
    }
    
    private int getActiveStaffCount() {
        String sql = "SELECT COUNT(*) FROM Management m " +
                     "JOIN Users u ON m.Username = u.Username " +
                     "WHERE u.Members_Status = 'Active'";
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting active staff: " + e.getMessage());
        }
        return 0;
    }
    
    private int getPendingAuthorizationsCount() {
        // Count pending payments
        String sql = "SELECT COUNT(*) FROM Payments WHERE Payment_Status = 'Pending'";
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting pending authorizations: " + e.getMessage());
        }
        return 0;
    }
    
    private int getTotalServicesCount() {
        String sql = "SELECT COUNT(*) FROM Services";
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error counting services: " + e.getMessage());
        }
        return 0;
    }
    
    private double getTotalTransactionsAmount() {
        String sql = "SELECT SUM(Amount) FROM Transactions";
        try (Connection conn = Config.connect();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error summing transactions: " + e.getMessage());
        }
        return 0.0;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        
        // Enable anti-aliasing and rendering quality
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        
        // Draw dashboard title
        drawDashboardTitle(g2, panelWidth);
        
        // Draw metric cards (2 rows, 3 columns layout - with 5 cards, center the last one)
        int cardWidth = 160;
        int cardHeight = 140;
        int horizontalSpacing = 20;
        int verticalSpacing = 20;
        
        int startX = (panelWidth - (cardWidth * 3 + horizontalSpacing * 2)) / 2;
        int firstRowY = 80;
        int secondRowY = firstRowY + cardHeight + verticalSpacing;
        
        // Draw first row (3 cards)
        for (int i = 0; i < 3; i++) {
            int x = startX + (i * (cardWidth + horizontalSpacing));
            drawMetricCard(g2, x, firstRowY, cardWidth, cardHeight, i);
        }
        
        // Draw second row (2 cards, centered)
        int secondRowStartX = (panelWidth - (cardWidth * 2 + horizontalSpacing)) / 2;
        for (int i = 3; i < 5; i++) {
            int x = secondRowStartX + ((i - 3) * (cardWidth + horizontalSpacing));
            drawMetricCard(g2, x, secondRowY, cardWidth, cardHeight, i);
        }
        
        // Draw decorative elements
        drawDecorativeElements(g2, panelWidth, panelHeight);
        
        g2.dispose();
    }
        
    private void drawDashboardTitle(Graphics2D g2, int panelWidth) {
        String title = "DASHBOARD OVERVIEW";
        
        g2.setFont(new Font("Impact", Font.BOLD, 28));
        
        // Draw text shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, (panelWidth - g2.getFontMetrics().stringWidth(title)) / 2 + 2, 42);
        
        // Draw gradient text
        LinearGradientPaint titleGradient = new LinearGradientPaint(
            0, 0, panelWidth, 0,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(100, 200, 255),
                new Color(200, 100, 255),
                new Color(100, 255, 200)
            }
        );
        g2.setPaint(titleGradient);
        g2.drawString(title, (panelWidth - g2.getFontMetrics().stringWidth(title)) / 2, 40);
        
        // Draw underline
        int textWidth = g2.getFontMetrics().stringWidth(title);
        int startX = (panelWidth - textWidth) / 2;
        
        g2.setStroke(new BasicStroke(2.0f));
        LinearGradientPaint lineGradient = new LinearGradientPaint(
            startX, 45, startX + textWidth, 45,
            new float[]{0.0f, 0.5f, 1.0f},
            new Color[]{
                new Color(100, 200, 255, 150),
                new Color(200, 100, 255, 200),
                new Color(100, 255, 200, 150)
            }
        );
        g2.setPaint(lineGradient);
        g2.drawLine(startX, 45, startX + textWidth, 45);
    }
    
    private void drawMetricCard(Graphics2D g2, int x, int y, int width, int height, int index) {
        // Create card shape with rounded corners
        RoundRectangle2D.Float cardShape = new RoundRectangle2D.Float(x, y, width, height, 25, 25);
        
        // Draw outer glow
        drawCardGlow(g2, cardShape, index);
        
        // Draw card body with 3D gradient
        drawCardBody(g2, cardShape, index, width, height);
        
        // Draw icon
        drawCardIcon(g2, x + 15, y + 30, index);
        
        // Draw title
        drawCardTitle(g2, x + 55, y + 35, index);
        
        // Draw value
        drawCardValue(g2, x + 15, y + 85, width, index);
        
        // Draw corner accents
        drawCardCornerAccents(g2, x, y, width, height, index);
    }
    
    private void drawCardGlow(Graphics2D g2, RoundRectangle2D.Float shape, int index) {
        float glowIntensity = glowIntensities[index];
        Color glowColor = glowColors[index];
        
        g2.setClip(shape);
        
        // Draw multiple glow layers
        for (int i = 0; i < 4; i++) {
            float strokeWidth = 6.0f + i * 2.0f;
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            
            int alpha = (int)(80 * glowIntensity * (1 - i * 0.2f));
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                                  glowColor.getBlue(), alpha));
            g2.draw(shape);
        }
        
        g2.setClip(null);
    }
    
    private void drawCardBody(Graphics2D g2, RoundRectangle2D.Float shape, int index, int width, int height) {
        Color[] gradients = cardGradients[index];
        float offset = floatOffsets[index];
        
        // Create gradient with subtle floating effect
        Point2D start = new Point2D.Float(shape.x, shape.y - offset);
        Point2D end = new Point2D.Float(shape.x + width, shape.y + height + offset);
        
        float[] fractions = {0.0f, 0.3f, 0.6f, 1.0f};
        Color[] colors = new Color[4];
        
        for (int i = 0; i < 4; i++) {
            float brightness = 1.0f + 0.1f * (float)Math.sin(pulsePhases[index] * 2 + i);
            int r = (int)Math.min(255, gradients[i].getRed() * brightness);
            int g = (int)Math.min(255, gradients[i].getGreen() * brightness);
            int b = (int)Math.min(255, gradients[i].getBlue() * brightness);
            colors[i] = new Color(r, g, b, gradients[i].getAlpha());
        }
        
        LinearGradientPaint gradient = new LinearGradientPaint(start, end, fractions, colors);
        g2.setPaint(gradient);
        g2.fill(shape);
        
        // Add inner highlight
        g2.setClip(shape);
        GradientPaint highlight = new GradientPaint(
            shape.x, shape.y, new Color(255, 255, 255, 40),
            shape.x, shape.y + height/3, new Color(255, 255, 255, 0)
        );
        g2.setPaint(highlight);
        g2.fill(shape);
        
        // Add inner shadow at bottom
        GradientPaint shadow = new GradientPaint(
            shape.x, shape.y + height - 20, new Color(0, 0, 0, 0),
            shape.x, shape.y + height, new Color(0, 0, 0, 60)
        );
        g2.setPaint(shadow);
        g2.fill(shape);
        
        g2.setClip(null);
        
        // Draw border
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(new Color(255, 255, 255, 50));
        g2.draw(shape);
    }
    
    private void drawCardIcon(Graphics2D g2, int x, int y, int index) {
        String icon = cardIcons[index];
        float glowIntensity = glowIntensities[index];
        Color glowColor = glowColors[index];
        
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        
        // Draw icon glow
        for (int i = 1; i <= 3; i++) {
            int alpha = (int)(60 * glowIntensity / i);
            g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                                  glowColor.getBlue(), alpha));
            g2.drawString(icon, x - i, y - i);
            g2.drawString(icon, x + i, y + i);
        }
        
        // Draw main icon
        g2.setColor(Color.WHITE);
        g2.drawString(icon, x, y);
    }
    
    private void drawCardTitle(Graphics2D g2, int x, int y, int index) {
        String title = cardTitles[index];
        
        g2.setFont(new Font("Impact", Font.PLAIN, 14));
        
        // Draw text shadow
        g2.setColor(new Color(0, 0, 0, 100));
        g2.drawString(title, x + 1, y + 1);
        
        // Draw main text
        g2.setColor(new Color(255, 255, 255, 200));
        g2.drawString(title, x, y);
    }
    
    private void drawCardValue(Graphics2D g2, int x, int y, int cardWidth, int index) {
        g2.setFont(new Font("Impact", Font.BOLD, 36));
        
        String value;
        if (index == 4) { // Total Transactions - format as currency
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "PH"));
            value = currencyFormat.format(totalTransactions);
        } else {
            switch(index) {
                case 0: value = String.valueOf(activeMembers); break;
                case 1: value = String.valueOf(activeStaff); break;
                case 2: value = String.valueOf(pendingAuthorizations); break;
                case 3: value = String.valueOf(totalServices); break;
                default: value = "0";
            }
        }
        
        // Truncate if too long
        if (g2.getFontMetrics().stringWidth(value) > cardWidth - 30) {
            g2.setFont(new Font("Impact", Font.BOLD, 28));
        }
        
        int textWidth = g2.getFontMetrics().stringWidth(value);
        int textX = x + (cardWidth - 30 - textWidth) / 2;
        
        // Draw text shadow with glow effect
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(value, textX + 2, y + 2);
        
        // Draw main text with gradient
        LinearGradientPaint valueGradient = new LinearGradientPaint(
            textX, y - 10, textX + textWidth, y + 10,
            new float[]{0.0f, 1.0f},
            new Color[]{
                Color.WHITE,
                new Color(glowColors[index].getRed(), glowColors[index].getGreen(), 
                         glowColors[index].getBlue(), 255)
            }
        );
        g2.setPaint(valueGradient);
        g2.drawString(value, textX, y);
        
        // Add subtle glow pulse
        float pulse = 0.2f + 0.1f * (float)Math.sin(pulsePhases[index] * 3);
        g2.setColor(new Color(glowColors[index].getRed(), glowColors[index].getGreen(),
                              glowColors[index].getBlue(), (int)(50 * pulse)));
        g2.drawString(value, textX, y);
    }
    
    private void drawCardCornerAccents(Graphics2D g2, int x, int y, int width, int height, int index) {
        int accentSize = 15;
        Color glowColor = glowColors[index];
        float pulse = 0.5f + 0.5f * (float)Math.sin(pulsePhases[index] * 4);
        int alpha = (int)(150 * pulse);
        
        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), 
                              glowColor.getBlue(), alpha));
        
        // Top-left corner
        g2.drawLine(x + 5, y + 5, x + 5 + accentSize, y + 5);
        g2.drawLine(x + 5, y + 5, x + 5, y + 5 + accentSize);
        
        // Top-right corner
        g2.drawLine(x + width - 5 - accentSize, y + 5, x + width - 5, y + 5);
        g2.drawLine(x + width - 5, y + 5, x + width - 5, y + 5 + accentSize);
        
        // Bottom-left corner
        g2.drawLine(x + 5, y + height - 5 - accentSize, x + 5, y + height - 5);
        g2.drawLine(x + 5, y + height - 5, x + 5 + accentSize, y + height - 5);
        
        // Bottom-right corner
        g2.drawLine(x + width - 5 - accentSize, y + height - 5, x + width - 5, y + height - 5);
        g2.drawLine(x + width - 5, y + height - 5 - accentSize, x + width - 5, y + height - 5);
    }
    
    private void drawDecorativeElements(Graphics2D g2, int width, int height) {
        // Draw orbital rings in background
        g2.setStroke(new BasicStroke(1.0f));
        
        // Ring 1
        g2.setColor(new Color(100, 200, 255, 20));
        g2.drawOval(width/2 - 150, height/2 - 100, 300, 200);
        
        // Ring 2
        g2.setColor(new Color(200, 100, 255, 15));
        g2.drawOval(width/2 - 200, height/2 - 150, 400, 300);
        
        // Ring 3
        g2.setColor(new Color(100, 255, 150, 10));
        g2.drawOval(width/2 - 250, height/2 - 200, 500, 400);
        
        // Draw small particles
        g2.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 20; i++) {
            double angle = System.currentTimeMillis() * 0.001 + i * Math.PI * 2 / 20;
            int px = width/2 + (int)(180 * Math.cos(angle));
            int py = height/2 + (int)(120 * Math.sin(angle));
            g2.fillOval(px - 2, py - 2, 4, 4);
        }
    }
    
    // Method to refresh data
    public void refreshData() {
        loadDashboardData();
    }
}