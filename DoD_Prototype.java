import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;

public class DoD_Prototype extends JPanel implements KeyListener {

    // --- Intro ---
    private ArrayList<StoryFrame> introFrames = new ArrayList<>();
    private boolean inIntro = true;
    private int currentFrame = 0;
    private int lettersShown = 0;
    private long frameCompleteTime = 0;
    private final int lingerMillis = 7000;

    // --- Menu ---
    private boolean inMenu = false;
    private int menuOrientation = 0;
    private Color menuColor = Color.RED;

    // --- Gameplay ---
    private boolean gameStarted = false;
    private int strobeIndex = 0;

    // --- Arena/Player ---
    private final int arenaHeight = 500;
    private final int playerBoxWidth = 50;
    private final int playerBoxHeight = 50;

    private int orientation = 0;
    private Color playerColor = Color.RED;
    private int level = 0;
    private ArrayList<EnemyOrb> enemyOrbs = new ArrayList<>();

    public DoD_Prototype() {
        setFocusable(true);
        addKeyListener(this);

        try {
            introFrames.add(new StoryFrame(ImageIO.read(new File("scene00.png")),
                    "Once upon a time, there lived a Kingdom inside of a computer...",
                    "One day the computer was infected by an unknown virus."
            ));
            introFrames.add(new StoryFrame(ImageIO.read(new File("scene01.png")),
                    "The ruler of the kingdom held its only defense against the plague",
                    "but in his old age could not use it to defend the kingdom.",
                    "The king asks YOU! Could you bear the sacrifice of your own arm?"
            ));
            introFrames.add(new StoryFrame(ImageIO.read(new File("scene02.png")),
                    "This is the story of Random Access Memory"
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Intro typewriter
        new Timer(50, e -> {
            if (inIntro && currentFrame < introFrames.size()) {
                StoryFrame frame = introFrames.get(currentFrame);
                int totalLetters = Arrays.stream(frame.lines).mapToInt(String::length).sum();
                if (lettersShown < totalLetters) {
                    lettersShown++;
                } else {
                    if (frameCompleteTime == 0) frameCompleteTime = System.currentTimeMillis();
                    if (System.currentTimeMillis() - frameCompleteTime >= lingerMillis) {
                        lettersShown = 0;
                        currentFrame++;
                        frameCompleteTime = 0;
                        if (currentFrame >= introFrames.size()) {
                            inIntro = false;
                            inMenu = true;
                        }
                    }
                }
                repaint();
            }
        }).start();

        // Strobe animation
        new Timer(16, e -> {
            if (gameStarted) strobeIndex++;
            repaint();
        }).start();
    }

    private void spawnLevel(int lvl) {
        enemyOrbs.clear();
        switch (lvl) {
            case 0 -> enemyOrbs.add(new EnemyOrb(0, 0, 50, Color.RED, 0));
            case 1 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.RED,0));
            case 2 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.RED,0));
            case 3 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.BLUE,0));
            case 4 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.BLUE,1));
            case 5 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.BLUE,2));
            case 6 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.GREEN,0));
            case 7 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.GREEN,1));
            case 8 -> enemyOrbs.add(new EnemyOrb(0,0,50,Color.GREEN,2));
           
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // --- Intro ---
        if (inIntro && currentFrame < introFrames.size()) {
            StoryFrame frame = introFrames.get(currentFrame);
            int imgW = frame.image.getWidth();
            int imgH = frame.image.getHeight();
            int maxW = getWidth() / 2;
            int maxH = getHeight() / 2;
            double scale = Math.min((double) maxW / imgW, (double) maxH / imgH);
            int drawW = (int)(imgW * scale);
            int drawH = (int)(imgH * scale);
            int imgX = (getWidth() - drawW) / 2;
            int imgY = 50;
            g2d.drawImage(frame.image, imgX, imgY, drawW, drawH, null);

            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Times New Roman", Font.BOLD, 24));
            int lineHeight = 30;
            int y = getHeight() - frame.lines.length * lineHeight - 50;

            int lettersRemaining = lettersShown;
            for (String line : frame.lines) {
                int len = Math.min(line.length(), lettersRemaining);
                String substring = line.substring(0, len);
                int textWidth = g2d.getFontMetrics().stringWidth(substring);
                int x = (getWidth() - textWidth) / 2;
                g2d.drawString(substring, x, y);
                y += lineHeight;
                lettersRemaining -= len;
                if (lettersRemaining <= 0) break;
            }
            return;
        }

        // --- Menu ---
        if (inMenu) {
            int triSize = Math.min(getWidth(), getHeight()) / 3;
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int[] xPoints = {0, -triSize, -triSize};
            int[] yPoints = {0, -triSize, triSize};
            AffineTransform old = g2d.getTransform();
            g2d.translate(centerX, centerY);
            g2d.rotate(Math.toRadians(menuOrientation * 90));
            g2d.setColor(menuColor);
            g2d.fillPolygon(xPoints, yPoints, 3);
            g2d.setTransform(old);
            return;
        }

        // --- Gameplay ---
        if (gameStarted) {
            int arenaW = getWidth() - 200;
            int arenaH = arenaHeight;
            int arenaX = (getWidth() - arenaW) / 2;
            int arenaY = (getHeight() - arenaH) / 2;

            float hue = (strobeIndex % 360) / 360f;
            g2d.setColor(Color.getHSBColor(hue, 1f, 1f));
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(arenaX, arenaY, arenaW, arenaH);
			
			//Player box
            int playerX = arenaX + arenaW / 2 - playerBoxWidth / 2;
            int playerY = arenaY + arenaH + 10;
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRect(playerX, playerY, playerBoxWidth, playerBoxHeight);
			
			//Triangle inside player box
			int triSize = playerBoxWidth / 2 - 5;
			AffineTransform old = g2d.getTransform();
			g2d.translate(playerX + playerBoxWidth / 2, playerY + playerBoxHeight / 2);
			g2d.rotate(Math.toRadians(orientation *90));
			g2d.setColor(Color.RED);
			g2d.fillPolygon(
			new int[]{0, -triSize, -triSize},
			new int[]{0, -triSize, triSize},
			3
		);
			g2d.setTransform(old);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inIntro && e.getKeyCode() == KeyEvent.VK_SPACE) {
            inIntro = false;
            inMenu = true;
            repaint();
            return;
        } else if (gameStarted) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> orientation = (orientation + 1) % 4;
                case KeyEvent.VK_LEFT -> orientation = (orientation + 3) % 4;
                case KeyEvent.VK_UP -> {
                    if (playerColor.equals(Color.RED) && level >= 1) playerColor = Color.BLUE;
                    else if (playerColor.equals(Color.BLUE) && level >= 2) playerColor = Color.GREEN;
                    else if (playerColor.equals(Color.GREEN)) playerColor = Color.RED;
                }
                case KeyEvent.VK_DOWN -> {
                    if (!enemyOrbs.isEmpty()) {
                        EnemyOrb first = enemyOrbs.get(0);
                        if (first.orientation == orientation) {
                            enemyOrbs.remove(0);
                            if (enemyOrbs.isEmpty()) {
                                level++;
                                spawnLevel(level);
                            }
                        }
                    }
                }
            }
            repaint();
        } else if (inMenu) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_RIGHT -> menuOrientation = (menuOrientation + 1) % 4;
                case KeyEvent.VK_LEFT -> menuOrientation = (menuOrientation + 3) % 4;
                case KeyEvent.VK_UP -> {
                    if (menuColor.equals(Color.RED)) menuColor = Color.GREEN;
                    else if (menuColor.equals(Color.GREEN)) menuColor = Color.BLUE;
                    else menuColor = Color.RED;
                }
                case KeyEvent.VK_DOWN -> {
                    inMenu = false;
                    gameStarted = true;
                    spawnLevel(level);
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
        frame.setResizable(false);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }

    static class StoryFrame {
        BufferedImage image;
        String[] lines;
        StoryFrame(BufferedImage image, String... lines) { this.image = image; this.lines = lines; }
    }

    static class EnemyOrb {
        int x, y, size, orientation;
        Color color;
        EnemyOrb(int x, int y, int size, Color color, int orientation) {
            this.x = x; this.y = y; this.size = size; this.color = color; this.orientation = orientation;
        }
    }
}
