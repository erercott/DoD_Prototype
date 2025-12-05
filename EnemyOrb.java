import java.awt.*;

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

    public void draw(Graphics2D g2d) {
        // Outline circle
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawOval(x, y, size, size);

        // Triangle inside orb
        int cx = x + size / 2;
        int cy = y + size / 2;
        int s = size / 3;

        int[] xPoints = {cx, cx - s, cx - s};
        int[] yPoints = {cy, cy - s, cy + s};

        g2d.setColor(triangleColor);
        g2d.fillPolygon(xPoints, yPoints, 3);
    }
}
