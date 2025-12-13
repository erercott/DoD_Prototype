import java.awt.image.BufferedImage;

class StoryFrame {
	BufferedImage image; 
	String[] lines; 
	
	public StoryFrame(BufferedImage image, String...lines){
		this.image = image; 
		this.lines = lines;
	}
}