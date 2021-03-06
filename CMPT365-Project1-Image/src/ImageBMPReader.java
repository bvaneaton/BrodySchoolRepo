import java.io.*;

public class ImageBMPReader {
	 
	 public static void main(String args[]) throws Exception
	  {  
		 String fileName = "";
		 byte[] byteArray;
		 System.out.println("Input a file name: ");
		 
	      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	      try
	      {	    	  
	    	  fileName = br.readLine();	 	   	   	  
	      }
	      catch(IOException ioe) 
	      {	    	  
	    	  System.out.println("IO error trying to open file " + fileName);
	          System.exit(1);
	      }
	      
	      InputStream input = new FileInputStream(fileName);
    	  byteArray = new byte[input.available()];
    	  int read = input.read(byteArray);
    	  input.close();      	 
    	  
    	  int horizontalPixel = (((byteArray[18] & 0xFF) << 0)  | ((byteArray[19] & 0xFF) << 8) 
    			  | ((byteArray[20] & 0xFF) << 16)  | ((byteArray[21] & 0xFF) << 24) );
    	  
    	  int verticalPixel = (((byteArray[22] & 0xFF) << 0)  | ((byteArray[23] & 0xFF) << 8) 
    			  | ((byteArray[24] & 0xFF) << 16)  | ((byteArray[25] & 0xFF) << 24) );

    	  readImageData(byteArray, 0, horizontalPixel, verticalPixel);	    	  
	      continueOn(byteArray, br, 1, horizontalPixel, verticalPixel);	      
	      continueOn(byteArray, br, 2, horizontalPixel, verticalPixel);
	      continueOn(byteArray, br, 3, horizontalPixel, verticalPixel);	      
	      System.out.println("Press any key to quit");
	      try
	      {
	    	  br.readLine();
	    	  System.exit(1);
	      }
	      catch(IOException ioe){
	    	  System.out.println("Error");
	          System.exit(1);
	      }
	      
	  }

	private static void continueOn(byte[] byteArray, BufferedReader br, int type, 
			int horizontalPixel, int verticalPixel) {
		System.out.println("Press any key to continue");
	      try
	      {
	    	  br.readLine();
	    	  readImageData(byteArray, type, horizontalPixel, verticalPixel);	 
	      }
	      catch(IOException ioe){
	    	  System.out.println("Error");
	          System.exit(1);
	      }
	}
	 
	private static void readImageData(byte[] image, int type, int horizontalPixel, 
			int verticalPixel){
		 javax.swing.JFrame frame = new javax.swing.JFrame();
		 int frameWidth = horizontalPixel + 100; int frameHeight = verticalPixel + 100; 
		 frame.setSize(frameWidth, frameHeight);
		 frame.setVisible(true); 	 
		 frame.getContentPane().add(new Points(image, type, horizontalPixel, verticalPixel));	
	 }
}
