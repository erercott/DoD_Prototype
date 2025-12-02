import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class DoD_Prototype extends JPanel implements KeyListener {

    private int orientation = 0; // 0=right, 1=down, 2=left, 3=up
	private boolean inputLocked = false; 
	
	private boolean flickerActive = false; 
	private long flickerStartTime;
	private int flickerInterval = 30; 
	
	

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
		
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
			g2d.translate(getWidth() /2, getHeight() /2);
			g2d.rotate(Math.toRadians(orientation*90));
			int size = 50;
			int[] xPoints = {0, -size, -size};
			int[] yPoints = {0, -size, size};
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
        frame.setSize(600, 600);
        frame.add(panel);
        frame.setVisible(true);
		panel.requestFocusInWindow();
    }
}
