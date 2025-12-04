import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class DoD_Prototype extends JPanel implements KeyListener {

    private int orientation = 3; // start facing up
    private boolean inputLocked = false; 
    private Color triangleColor = Color.RED; 

    private boolean flickerActive = false; 
    private long flickerStartTime;
    private int flickerInterval = 30; 

    // Arena parameters
    private int triangleSize = 50;          // half-width/height of triangle
    private int arenaMargin = 5;            // pixels around triangle
    private int arenaSize = triangleSize*2 + arenaMargin*2; // snug around triangle
    private int arenaBottomMargin = 30;     // space from bottom of window

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw snug arena bounding box, bottom-anchored
        g2d.setColor(Color.LIGHT_GRAY);
        int arenaX = getWidth()/2 - arenaSize/2;
        int arenaY = getHeight() - arenaSize - arenaBottomMargin;
        g2d.drawRect(arenaX, arenaY, arenaSize, arenaSize);

        boolean drawTriangle = true; 

        if(flickerActive){
            long elapsed = System.currentTimeMillis() - flickerStartTime; 

            if(elapsed >= 500){
                flickerActive = false; 
                inputLocked = false;
            } else {
                drawTriangle = (elapsed / flickerInterval) % 2 == 0; 
            }
        }

        if(drawTriangle) {
            AffineTransform old = g2d.getTransform();
            // Translate triangle to arena center
            g2d.translate(getWidth() / 2, arenaY + arenaSize/2);
            g2d.rotate(Math.toRadians(orientation*90));
            int[] xPoints = {0, -triangleSize, -triangleSize};
            int[] yPoints = {0, -triangleSize, triangleSize};
            g2d.setColor(triangleColor);
            g2d.fillPolygon(xPoints, yPoints,3);
            g2d.setTransform(old);
        }
    }

    private void rotateRight() {
        orientation = (orientation + 1) % 4;
        repaint();
    }

    private void rotateLeft() {
        orientation = (orientation + 3) % 4; // equivalent to -1 mod 4
        repaint();
    }

    public DoD_Prototype() {
        setFocusable(true);
        addKeyListener(this);

        new javax.swing.Timer(16, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                repaint();
            }
        }).start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (inputLocked) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                rotateRight();
                break;
            case KeyEvent.VK_LEFT:
                rotateLeft();
                break;
            case KeyEvent.VK_DOWN:
                startFlicker();
                break;
            case KeyEvent.VK_UP:
                if(!inputLocked){
                    if(triangleColor == Color.RED) triangleColor = Color.GREEN;
                    else if(triangleColor == Color.GREEN) triangleColor = Color.BLUE;
                    else triangleColor = Color.RED;
                    repaint();
                }
                break;
        }
    }

    private void startFlicker(){
        flickerActive = true;
        inputLocked = true; 
        flickerStartTime = System.currentTimeMillis();
        repaint(); 
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("DoD Prototype");
        DoD_Prototype panel = new DoD_Prototype();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(640, 600);
		frame.setSize(1226, 733);
        frame.add(panel);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}
