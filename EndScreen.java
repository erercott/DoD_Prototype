import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class EndScreen extends JPanel {

    private final boolean gameOver; // true = Game Over, false = Victory
    private final String text;

    // Common
    private final Font font = new Font("Arial Black", Font.BOLD, 48);
    private BufferedImage colorImage, maskImage;

    // Victory
    private float hueOffset = 0f;
    private boolean finished = false;
    private final int maxCycles = 2;

    // Game Over
    private int frameIndex = 0;
    private final Color[] reds;

    public EndScreen(boolean gameOver) {
        this.gameOver = gameOver;
        this.text = gameOver ? "GAME OVER" : "CONGRATULATIONS";
        setPreferredSize(new Dimension(800, 400));
        setBackground(Color.BLACK);

        // Setup reds for Game Over
        reds = new Color[10];
        for (int i = 0; i < reds.length; i++) {
            int r = 150 + (int)(105 * Math.random());
            reds[i] = new Color(r, 0, 0);
        }

        // Timer for animation (set it up inside the constructor)
        new Timer(50, e -> {
            if (gameOver) {
                frameIndex++;
            } else if (!finished) {
                hueOffset += 0.05f;
                if (hueOffset >= maxCycles) finished = true;
            }
            repaint();  // Repaint after updating animation state
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2;

        if (gameOver) {
            Color color = reds[frameIndex % reds.length];
            g2d.setColor(color);
            g2d.drawString(text, x, y);
        } else {
            if (!finished) {
                int w = getWidth(), h = getHeight();

                // Color fountain
                colorImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D cg = colorImage.createGraphics();
                cg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int i = 0; i < h; i++) {
                    float hue = (hueOffset + i / 300f) % 1f;
                    cg.setColor(Color.getHSBColor(hue, 1f, 1f));
                    cg.drawLine(0, i, w, i);
                }
                cg.dispose();

                // Mask
                maskImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D mg = maskImage.createGraphics();
                mg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                mg.setFont(font);
                mg.setColor(Color.WHITE);
                mg.drawString(text, x, y);
                mg.dispose();

                // Apply mask
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        int maskPixel = maskImage.getRGB(i, j);
                        if ((maskPixel & 0x00FFFFFF) == 0) {
                            colorImage.setRGB(i, j, 0x00000000);
                        }
                    }
                }

                g2d.drawImage(colorImage, 0, 0, null);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.drawString(text, x, y);
            }
        }
    }
}
