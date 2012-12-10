import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Points extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int NORMAL = 0;
	private static final int HALVED = 1;
	private static final int GREY = 2;
	private static final int DITHER = 3;
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
	    
	    switch(type){
	    	case NORMAL:
	    		normalImage(g2d);
	    		break;
	    	case HALVED:
	    		halvedImage(g2d);
	    		break;
	    	case GREY:
	    		greyscaleImage(g2d);
	    		break;
	    	case DITHER:
	    		ditheredImage(g2d);
	    		break;
	    }
	}	
	private void ditheredImage(Graphics2D g2d){
		int[][] ditherMatrix = {
				{6, 12, 2 ,10}, {11, 9, 3, 1}, {7, 5, 15, 4}, {16, 8, 14, 13}
				};
		int ditherX = 0;
		int ditherY = 0;
		int offset = image[10];
		for (int i = verticalPixel; i > 0; i--){
			  for (int j = 0; j < horizontalPixel; j++){
				  int R = ((int)image[offset+2] & 0xFF);
				  int G = ((int)image[offset+1] & 0xFF);
				  int B = ((int)image[offset] & 0xFF);
				  
				  double Y = convertToY(R, G, B);
				  
				  int ditherValue = (int)((17 * Y) / 256);
				  if (ditherValue > ditherMatrix[ditherX][ditherY]){
					  R = 255;
					  G = 255;
					  B = 255;
				  }
				  else
				  {
					  R = 0;
					  G = 0;
					  B = 0;
				  }				  				  		  
				  g2d.setColor(new Color(R, G, B));
				  g2d.drawLine(j, i, j, i);
				  offset = offset + 3;
				  ditherX++;
				  if (ditherX == 4){
					  ditherX = 0;
				  }
			  }
			  ditherY++;
			  if (ditherY == 4){
				  ditherY = 0;
			  }
		  }
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

	//Print out a greyscaled image
	private void greyscaleImage(Graphics2D g2d) {
		int offset = image[10];
		for (int i = verticalPixel; i > 0; i--){
			  for (int j = 0; j < horizontalPixel; j++){
				  int R = ((int)image[offset+2] & 0xFF);
				  int G = ((int)image[offset+1] & 0xFF);
				  int B = ((int)image[offset] & 0xFF);
				  
				  double Y = convertToY(R, G, B);
				  double U = 0;
				  double V = 0;
				  	    		  
				  R = convertToR(Y, V);
				  G = convertToG(Y, U, V);
				  B = convertToB(Y, U);
				  
				  g2d.setColor(new Color(R, G, B));
				  g2d.drawLine(j, i, j, i);
				  offset = offset + 3;
			  }
		  }
	}

	//print out an image with U and V halved
	private void halvedImage(Graphics2D g2d) {
		int offset = image[10];
		for (int i = verticalPixel; i > 0; i--){
			  for (int j = 0; j < horizontalPixel; j++){
				  int R = ((int)image[offset+2] & 0xFF);
				  int G = ((int)image[offset+1] & 0xFF);
				  int B = ((int)image[offset] & 0xFF);
				  
				  double Y = convertToY(R, G, B);
				  double U = convertToU(B, Y);
				  double V = convertToV(R, Y);
				  	    		  
				  R = convertToR(Y, V);
				  G = convertToG(Y, U, V);
				  B = convertToB(Y, U);
				  
				  g2d.setColor(new Color(R, G, B));
				  g2d.drawLine(j, i, j, i);
				  offset = offset + 3;
			  }
		  }
	}

	//Conversion methods for YUV and RGB
	private int convertToB(double Y, double U) {
		return (int)(Y + 2.032 * U);
	}

	private int convertToG(double Y, double U, double V) {
		return (int)(Y - 0.39465 * U - 0.58060 * V);
	}

	private int convertToR(double Y, double V) {
		return (int)(Y + 1.13983 * V);
	}

	private double convertToV(int R, double Y) {
		return (0.877 * (R - Y))/2;
	}

	private double convertToU(int B, double Y) {
		return (0.492 * (B - Y))/2;
	}

	private double convertToY(int R, int G, int B) {
		return 0.299 * R + 0.587 * G + 0.114 * B;
	}

	
	
}