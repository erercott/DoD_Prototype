import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DoD_Prototype extends JPanel implements KeyListener {

    private ArrayList<EnemyOrb> enemyOrbs;
    private int orientation = 0;
    private boolean inputLocked = false;
    private Color triangleColor = Color.RED;
    private boolean flickerVisible = true;
    private int flickerInterval = 30;
    private BufferedImage characterPortrait;

    private int portraitWidth = 220; 
    private int arenaHeight = 500;   
    private int playerBoxWidth = 50;  
    private int playerBoxHeight = 50; 
    private int triangleMargin = 4;   
	
	// - Combo Tracking
	private boolean[][] clearedOrientations = new boolean[3][4];
	private int[] completedCycles = new int [3];

    public DoD_Prototype() {
        setFocusable(true);
        addKeyListener(this);

        try {
            characterPortrait = ImageIO.read(new File("portrait.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        enemyOrbs = new ArrayList<>();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent e) { spawnOrbs(); }
            public void componentResized(java.awt.event.ComponentEvent e) { if (enemyOrbs.isEmpty()) spawnOrbs(); }
        });

        new javax.swing.Timer(16, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        int inset = 40;

        // --- Arena ---
        int arenaX = inset;
        int arenaY = inset;
        int arenaW = getWidth() - inset * 2 - portraitWidth - 20;
        int arenaH = arenaHeight;

        Stroke oldStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(5));
        g2d.setColor(new Color(0, 180, 255));
        g2d.drawRect(arenaX, arenaY, arenaW, arenaH);
        g2d.setStroke(oldStroke);

        // --- Player box ---
        int playerX = arenaX + arenaW / 2 - playerBoxWidth / 2;
        int playerY = arenaY + arenaH + 10;
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1)); 
        g2d.drawRect(playerX, playerY, playerBoxWidth, playerBoxHeight);

        // --- Central triangle inside player box ---
        if (flickerVisible) {
            AffineTransform old = g2d.getTransform();
            g2d.translate(playerX + playerBoxWidth / 2, playerY + playerBoxHeight / 2);
            g2d.rotate(Math.toRadians(orientation * 90));
            int triangleSize = (playerBoxWidth / 2) - triangleMargin; 
            int[] xPoints = {0, -triangleSize, -triangleSize};
            int[] yPoints = {0, -triangleSize, triangleSize};
            g2d.setColor(triangleColor);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setTransform(old);
        }

        // --- Character portrait ---
        if (characterPortrait != null) {
            int portraitX = getWidth() - portraitWidth - 20;
            int portraitY = arenaY;
            int portraitH = arenaH;
            g2d.drawImage(characterPortrait, portraitX, portraitY, portraitWidth, portraitH, null);

            g2d.setStroke(new BasicStroke(2)); 
            g2d.setColor(Color.BLACK);
            g2d.drawRect(portraitX, portraitY, portraitWidth, portraitH);
            g2d.setStroke(oldStroke);
        }

        // --- Enemy orbs inside arena ---
        for (EnemyOrb orb : enemyOrbs) orb.draw(g2d);
    }

    private void spawnOrbs() {
        enemyOrbs.clear();
        int numOrbs = 1 + (int)(Math.random() * 3);
        int orbSize = 50;

        int inset = 40;
        int arenaX = inset;
        int arenaY = inset;
        int arenaW = getWidth() - inset * 2 - portraitWidth - 20;
        int arenaH = arenaHeight;

        int slotWidth = arenaW / numOrbs;
        Color[] colors = {Color.RED, Color.GREEN, Color.BLUE};
        ArrayList<Color> orbColors = new ArrayList<>();
        for (int i = 0; i < numOrbs; i++) orbColors.add(colors[i % colors.length]);
        Collections.shuffle(orbColors);

        for (int i = 0; i < numOrbs; i++) {
            int orbX = arenaX + i * slotWidth + slotWidth / 2 - orbSize / 2;
            int orbY = arenaY + arenaH / 2 - orbSize / 2;
            int orbOrientation = (int)(Math.random() * 4);
            Color orbColor = orbColors.get(i);
            enemyOrbs.add(new EnemyOrb(orbX, orbY, orbSize, orbColor, orbOrientation));
        }
    }

    private void rotateRight() { orientation = (orientation + 1) % 4; repaint(); }
    private void rotateLeft() { orientation = (orientation + 3) % 4; repaint(); }

    private void startFlicker(Runnable afterFlicker) {
        flickerVisible = true;
        inputLocked = true;
        long startTime = System.currentTimeMillis();

        new javax.swing.Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                flickerVisible = (elapsed / flickerInterval % 2 == 0);
                repaint();
                if (elapsed >= 500) {
                    flickerVisible = true;
                    inputLocked = false;
                    ((Timer) e.getSource()).stop();
                    if (afterFlicker != null) afterFlicker.run();
                }
            }
        }).start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inputLocked) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT -> rotateRight();
            case KeyEvent.VK_LEFT -> rotateLeft();
            case KeyEvent.VK_DOWN -> {
                clearMatchingEnemies();
                startFlicker(() -> {
                    if (!enemyOrbs.isEmpty()) return;
                    spawnOrbs();
                });
            }
            case KeyEvent.VK_UP -> {
                if (triangleColor.equals(Color.RED)) triangleColor = Color.GREEN;
                else if (triangleColor.equals(Color.GREEN)) triangleColor = Color.BLUE;
                else triangleColor = Color.RED;
                repaint();
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    private void clearMatchingEnemies() {
        enemyOrbs.removeIf(orb -> orb.matchesPlayer(orientation, triangleColor));
		
		//color determination
		int colorIndex = switch (triangleColor) {
			case RED -> 0;
			case GREEN -> 1;
			case BLUE -> 2;
			default -> -1;
		};
		if (colorIndex == -1) return;
		
		clearedOrientations[colorIndex][orientation] = true;
		
		boolean allCleared = true;
		for(boolean cleared : clearedOrientations[colorIndex]){
			if (!cleared){
				allCleared = false;
				break;
				}
			}
		if (allCleared) {
			completedCycles[colorIndex]++;
			clearedOrientations[colorIndex] = new boolean[4];
		}
	

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
