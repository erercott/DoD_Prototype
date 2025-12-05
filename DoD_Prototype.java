import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class DoD_Prototype extends JPanel implements KeyListener {

    private ArrayList<EnemyOrb> enemyOrbs;
    private int orientation = 0;
    private boolean inputLocked = false;
    private Color triangleColor = Color.RED;
    private boolean flickerActive = false;
    private long flickerStartTime;
    private int flickerInterval = 30;

    // Arena parameters
    private int triangleSize = 50;
    private int arenaMargin = 5;
    private int arenaSize = triangleSize * 2 + arenaMargin * 2;
    private int arenaBottomMargin = 30;

    public DoD_Prototype() {
        setFocusable(true);
        addKeyListener(this);

        enemyOrbs = new ArrayList<>();

        // Spawn orbs after panel is displayed (so getWidth/getHeight are valid)
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) {
                spawnOrbs();
            }
            public void componentResized(java.awt.event.ComponentEvent e) {
                if (enemyOrbs.isEmpty()) {
                    spawnOrbs();
                }
            }
        });

        new javax.swing.Timer(16, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Outer HUD border
        int inset = 40;
        int bottomInset = 150;
        int thickness = 5;
        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(thickness));
        g2d.setColor(new Color(0, 180, 255));
        g2d.drawRect(inset, inset, getWidth() - inset * 2, getHeight() - bottomInset - inset);
        g2d.setStroke(oldStroke);

        // Arena
        int arenaX = getWidth() / 2 - arenaSize / 2;
        int arenaY = getHeight() - arenaSize - arenaBottomMargin;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.drawRect(arenaX, arenaY, arenaSize, arenaSize);

        // Draw orbs
        for (EnemyOrb orb : enemyOrbs) {
            orb.draw(g2d);
        }

        // Draw central triangle
        boolean drawTriangle = true;
        if (flickerActive) {
            long elapsed = System.currentTimeMillis() - flickerStartTime;
            if (elapsed >= 500) {
                flickerActive = false;
                inputLocked = false;
            } else {
                drawTriangle = (elapsed / flickerInterval) % 2 == 0;
            }
        }

        if (drawTriangle) {
            AffineTransform old = g2d.getTransform();
            g2d.translate(getWidth() / 2, arenaY + arenaSize / 2);
            g2d.rotate(Math.toRadians(orientation * 90));

            int[] xPoints = {0, -triangleSize, -triangleSize};
            int[] yPoints = {0, -triangleSize, triangleSize};

            g2d.setColor(triangleColor);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setTransform(old);
        }
    }

    private void spawnOrbs() {
        enemyOrbs.clear();
        int numOrbs = 1 + (int)(Math.random() * 3); // 1-3 orbs
        int orbSize = 50;

        int inset = 40;
        int bottomInset = 150;
        int bigX = inset;
        int bigY = inset;
        int bigW = getWidth() - inset * 2;
        int bigH = getHeight() - bottomInset - inset;

        int slotWidth = bigW / numOrbs;
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};

        for (int i = 0; i < numOrbs; i++) {
            int slotStartX = bigX + i * slotWidth;
            int orbX = slotStartX + slotWidth / 2 - orbSize / 2;
            int orbY = bigY + bigH / 2 - orbSize / 2;

            int orbOrientation = (int)(Math.random() * 4); // 0-3 for triangle orientation
            Color orbColor = colors[i % colors.length]; // cycles through colors

            enemyOrbs.add(new EnemyOrb(orbX, orbY, orbSize, orbColor, orbOrientation));
        }
    }

    private void rotateRight() {
        orientation = (orientation + 1) % 4;
        repaint();
    }

    private void rotateLeft() {
        orientation = (orientation + 3) % 4;
        repaint();
    }

    private void startFlicker() {
        flickerActive = true;
        inputLocked = true;
        flickerStartTime = System.currentTimeMillis();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inputLocked) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> rotateRight();
            case KeyEvent.VK_LEFT -> rotateLeft();
            case KeyEvent.VK_DOWN -> startFlicker();
            case KeyEvent.VK_UP -> {
                if (!inputLocked) {
                    if (triangleColor == Color.RED) triangleColor = Color.GREEN;
                    else if (triangleColor == Color.GREEN) triangleColor = Color.BLUE;
                    else triangleColor = Color.RED;
                    repaint();
                }
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("DoD Prototype");
        DoD_Prototype panel = new DoD_Prototype();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1226, 733);
        frame.add(panel);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}
