import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class Points extends JPanel {
	private static final long serialVersionUID = 1L;
	byte[] image;
	int type;
	int horizontalPixel, verticalPixel;
	
	public Points(byte[] image, int type, int horizontalPixel, int verticalPixel){
		this.image = image;
		this.type = type;
		this.horizontalPixel = horizontalPixel;
		this.verticalPixel = verticalPixel;
	}
	
	public void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;	   
	    normalImage(g2d);

	}	
	//Print out a normal image with no modifications
	private void normalImage(Graphics2D g2d) {
		int offset = image[10];
	    for (int i = verticalPixel; i > 0; i--){
	    	for (int j = 0; j < horizontalPixel; j++){
	    		
	    		int R = ((int)image[offset+2] & 0xFF);
				int G = ((int)image[offset+1] & 0xFF);
				int B = ((int)image[offset] & 0xFF);
				  
	    		g2d.setColor(new Color(R, G, B));
	    		g2d.drawLine(j, i, j, i);
	    		offset = offset + 3;
	    	}
	    }
	}

	

	
	
}