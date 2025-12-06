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
		
	public boolean matchesPlayer(int playerOrientation, Color playerColor) {
		return this.orientation == playerOrientation && this.triangleColor.equals(playerColor);
    }

    public void draw(Graphics2D g2d) {
        // Outline circle
		int cx = x + size / 2;
		int cy = y + size / 2;
		int s = size / 3;
		
        int[] xPoints = {0, -s, -s};
        int[] yPoints = {0, -s, s};
		
		AffineTransform old = g2d.getTransform();
		
		g2d.translate(cx, cy);
		g2d.rotate(Math.toRadians(orientation * 90));
		
		g2d.setColor(triangleColor);
		g2d.fillPolygon(xPoints, yPoints, 3);
		
		g2d.setTransform(old);
    // Draw orb outline
    g2d.setColor(Color.BLACK);
    g2d.setStroke(new BasicStroke(3));
    g2d.drawOval(x, y, size, size);
	}
}