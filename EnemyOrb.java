import java.awt.*;
import java.awt.geom.AffineTransform;

public class EnemyOrb {
    int x, y, size;
    Color triangleColor;
    int orientation;

    public EnemyOrb(int x, int y, int size, Color triangleColor, int orientation) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.triangleColor = triangleColor;
        this.orientation = orientation;
    }

    // Check if the orb matches the player orientation and color
    public boolean matchesPlayer(int playerOrientation, Color playerColor) {
        return this.orientation == playerOrientation && this.triangleColor.equals(playerColor);
    }

    // --- Original draw method for normal gameplay ---
    public void draw(Graphics2D g2d, boolean active) {
        draw(g2d, active, this.x, this.y, this.size);
    }

    // --- New draw method for final boss with scaled coordinates ---
    public void draw(Graphics2D g2d, boolean active, int drawX, int drawY, int drawSize) {
        int cx = drawX + drawSize / 2;
        int cy = drawY + drawSize / 2;
        int s = drawSize / 3;

        int[] xPoints = {0, -s, -s};
        int[] yPoints = {0, -s, s};

        AffineTransform old = g2d.getTransform();

        // Draw triangle rotated based on orientation
        g2d.translate(cx, cy);
        g2d.rotate(Math.toRadians(orientation * 90));
        g2d.setColor(triangleColor);
        g2d.fillPolygon(xPoints, yPoints, 3);
        g2d.setTransform(old);

        // Draw active outline if orb is the “current target”
        if (active) {
            g2d.setColor(triangleColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(drawX - 6, drawY - 6, drawSize + 12, drawSize + 12);
        }

        // Draw orb outline
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(drawX, drawY, drawSize, drawSize);
    }
}
