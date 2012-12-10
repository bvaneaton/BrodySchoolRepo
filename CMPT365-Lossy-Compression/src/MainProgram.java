import java.io.*;

@SuppressWarnings("unused")
public class MainProgram {
	public static void main(String args[]) throws Exception
	  {  
		 String fileName = "";
		 Encoder encoder = new Encoder();
		 Decoder decoder = new Decoder();
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
  	  double read = input.read(byteArray);
  	  input.close();      	
  	  
  	  if (fileName.contains(".bmp")){ 	  
	  	  processBMPImage(fileName, encoder, decoder, byteArray, br, read);	  	
  	  	}
  	  else{
  		  processIM3Image(decoder, byteArray, br);
  	  }
	      
	  }

	private static void processIM3Image(Decoder decoder, byte[] byteArray,
			BufferedReader br) {
		System.out.println("Decoding...");
		decoder.decode(byteArray);		  	  
	  	try
	      {	  			
	    	br.readLine();	    	   		 
	      }
	      catch(IOException ioe){
	    	  System.out.println("Error");
	          System.exit(1);
	      }
	  	 System.exit(1);
	}

	private static void processBMPImage(String fileName, Encoder encoder,
			Decoder decoder, byte[] byteArray, BufferedReader br, double read)
			throws FileNotFoundException, IOException {
		int horizontalPixel = (((byteArray[18] & 0xFF) << 0)  | ((byteArray[19] & 0xFF) << 8) 
	  			  | ((byteArray[20] & 0xFF) << 16)  | ((byteArray[21] & 0xFF) << 24) );
	  	  
	  	  int verticalPixel = (((byteArray[22] & 0xFF) << 0)  | ((byteArray[23] & 0xFF) << 8) 
	  			  | ((byteArray[24] & 0xFF) << 16)  | ((byteArray[25] & 0xFF) << 24) );
	
	  	      readImageData(byteArray, 0, horizontalPixel, verticalPixel);	    	     
		      System.out.println("Encoding...");
		      MeanSquare MSE = encoder.encode(byteArray, fileName, horizontalPixel, verticalPixel);     
		      
		      fileName = changeExtension(fileName, ".IM3");
		      
		      try
		      {
		  		System.out.println("Press any key to continue.");	
		    	br.readLine();	    	   		 
		      }
		      catch(IOException ioe){
		    	  System.out.println("Error");
		          System.exit(1);
		      }
		      
			  InputStream inputStream = new FileInputStream(fileName);
		  	  byteArray = new byte[inputStream.available()];	  	  
		  	  double read1 = inputStream.read(byteArray);	  	  
		  	  inputStream.close();    	  
		  	
		  	decoder.setMSEInDecoder(MSE); 
		  	System.out.println("Compression ratio for this file is: " + (read / read1));		  	
		  	processIM3Image(decoder, byteArray, br);
	}
	
	static String changeExtension(String originalName, String newExtension) {
	    int lastDot = originalName.lastIndexOf(".");
	    if (lastDot != -1) {
	        return originalName.substring(0, lastDot) + newExtension;
	    } else {
	        return originalName + newExtension;
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


