import java.util.SortedMap;
import java.util.TreeMap;


@SuppressWarnings("unused")
public class Decoder {
	private static final int INITIALOFFSET = 8;
	
	//codeTable for Y, U and V
	SortedMap<Integer, Integer> codeTableY = new TreeMap<Integer, Integer>();
	SortedMap<Integer, Integer> codeTableU = new TreeMap<Integer, Integer>();
	SortedMap<Integer, Integer> codeTableV = new TreeMap<Integer, Integer>();	
	
	//run length compressed channels
	int[] yCompression;
	int[] uCompression;
	int[] vCompression;
	
	double[] yDeCompression;
	double[] uDeCompression;
	double[] vDeCompression;
	
	double[] yChannel;
	double[] uChannel;
	double[] vChannel;
	
	double[] rgbChannel;
	
	private int[] lumaQuantTable = {11, 11, 12, 15, 20, 27, 36, 47,
			11, 12, 15, 20, 27, 36, 47, 93,
			12, 15, 20, 27, 36, 47, 93, 185,
			15, 20, 27, 36, 47, 93, 185, 369,
			20, 27, 36, 47, 93, 185, 369, 737,
			27, 36, 47, 93, 185, 369, 737, 1473,
			36, 47, 93, 185, 369, 737, 1473, 2945,
			47, 93, 185, 369, 737, 1473, 2945, 5889};

		private int[] chromaQuantTable = {12, 15, 18, 26, 39, 69, 139, 279,
				15, 18, 26, 39, 69, 139, 279, 559,
				18, 26, 39, 69, 139, 279, 559, 1119,
				26, 39, 69, 139, 279, 559, 1119, 2239,
				39, 69, 139, 279, 559, 1119, 2239, 4479,
				69, 139, 279, 559, 1119, 2239, 4479, 8959,
				139, 279, 559, 1119, 2239, 4479, 8959, 17919,
				279, 559, 1119, 2239, 4479, 8959, 17919, 35839};
		
	private MeanSquare MSE;
	
	public Decoder(){
	}	
	
	public void setMSEInDecoder(MeanSquare MSE){
		this.MSE = MSE;
	}
		
	public void decode(byte[] byteArray){
		/*int codeTableYSize = ((byteArray[2] & 0xFF) << 8)  | ((byteArray[3] & 0xFF) << 0);
		for (int i = INITIALOFFSET; i < ((codeTableYSize * 4) + 4) + INITIALOFFSET; i+=4){
			codeTableY.put(((byteArray[i] & 0xFF) << 8)  | ((byteArray[i+1] & 0xFF) << 0),
					((int)(byteArray[i+3])));
		}
		int codeTableUSize = ((byteArray[4] & 0xFF) << 8)  | ((byteArray[5] & 0xFF) << 0);
		for (int i = INITIALOFFSET + (codeTableYSize * 4); i < (codeTableUSize * 4)  + INITIALOFFSET + (codeTableYSize * 4); i+=4){
			codeTableU.put(((byteArray[i] & 0xFF) << 8)  | ((byteArray[i+1] & 0xFF) << 0),
					((int)(byteArray[i+3])));
		}
		
		int codeTableVSize = ((byteArray[6] & 0xFF) << 8)  | ((byteArray[7] & 0xFF) << 0);
		for (int i = INITIALOFFSET + codeTableY.size() + codeTableU.size(); i < codeTableUSize + INITIALOFFSET + codeTableY.size(); i+=4){
			codeTableU.put(((byteArray[i] & 0xFF) << 8)  | ((byteArray[i+1] & 0xFF) << 0),
					((int)(byteArray[i+3])));
		}*/
		int uCompressionLength = (((byteArray[0] & 0xFF) << 8) | ((byteArray[1] & 0xFF) << 0)) * 2;
		int vCompressionLength = (((byteArray[2] & 0xFF) << 8) | ((byteArray[3] & 0xFF) << 0)) * 2;
		int horizontalLength = ((byteArray[4] & 0xFF) << 8) | ((byteArray[5] & 0xFF) << 0);
		int verticalLength = ((byteArray[6] & 0xFF) << 8) | ((byteArray[7] & 0xFF) << 0);
		int yCompressionLength = byteArray.length - vCompressionLength - uCompressionLength
				- 8;
		
		yCompression = new int[horizontalLength * verticalLength];
		uCompression = new int[(horizontalLength/2 * verticalLength)];
		vCompression = new int[(horizontalLength/2 * verticalLength)];
		
		yDeCompression = new double[horizontalLength * verticalLength];
		uDeCompression = new double[(horizontalLength/2 * verticalLength/2)];
		vDeCompression = new double[(horizontalLength/2 * verticalLength/2)];
		
		yChannel = new double[horizontalLength * verticalLength];
		uChannel = new double[(horizontalLength/2 * verticalLength/2)];
		vChannel = new double[(horizontalLength/2 * verticalLength/2)];
		
		rgbChannel = new double[(horizontalLength * 3) * (verticalLength * 3)];

		//reverse the runLength encoding and expand the data
		decompressRunLength(uCompression, uCompressionLength, INITIALOFFSET, byteArray);
		decompressRunLength(vCompression, vCompressionLength, INITIALOFFSET + uCompressionLength, byteArray);
		decompressRunLength(yCompression, yCompressionLength, INITIALOFFSET + uCompressionLength + vCompressionLength, byteArray);
		
		//reverse the quantization
		reverseQuantization(lumaQuantTable, yCompression, yDeCompression, horizontalLength, verticalLength);
		reverseQuantization(chromaQuantTable, uCompression, uDeCompression, horizontalLength, verticalLength);
		reverseQuantization(chromaQuantTable, vCompression, vDeCompression, horizontalLength, verticalLength);

		transformWithDCT(yDeCompression, yChannel, horizontalLength, verticalLength);
		transformWithDCT(uDeCompression, uChannel, horizontalLength, verticalLength);
		transformWithDCT(vDeCompression, vChannel, horizontalLength, verticalLength);
		
		convertYOUtoRGB(horizontalLength, verticalLength);
		if (!(MSE == null)){
			MSE.setAfterChannels(yChannel, uChannel, vChannel);
			MSE.computeAndReportPSNR();
		}
	}
	
	private void convertYOUtoRGB(int horizontalPixel,
			int verticalPixel) {

		int offset = 0;
		int counter = 0;
		int uCounter = 0;
		int vCounter = 0;
		int cCounter = 0;
		double Y;
		double U = 0;
		double V = 0;
		int m = 0;
	    for (int i = verticalPixel-1; i >= 0 ; i--){
	    	m = 0;
	    	for (int j = 0; j <= horizontalPixel-1; j++){
	    		
	    		Y = yChannel[counter];
	    		/*if (j % 2 == 0){
    				U = uChannel[cCounter];
	    			V = vChannel[cCounter];	
    			}
    			else{
    				U = uChannel[cCounter];
	    			V = vChannel[cCounter];	
	    			cCounter++;
    			}*/	
	    		
	    		if (i % 2 == 0){
	    			if (j % 2 == 0){
		    			U = (uChannel[uCounter]);
		    			V = 0; 			
	    			}
	    			else{
	    				uCounter++;
	    			}
	    		}
	    		else{
	    			if (j % 2 == 0){
		    			U = 0;
		    			V = (vChannel[vCounter]); 			
	    			}
	    			else{
						vCounter++;
	    			}    				
	    		}	
					
				
				double R = convertToR(Y, U, V);
				double G = convertToG(Y, U, V);
				double B = convertToB(Y, U, V);	
				
				if (R < 0){
					R = 0;
				}
				if (R > 255){
					R = 255;
				}
				if (G < 0){
					G = 0;
				}
				if (G > 255){
					G = 255;
				}
				if (B < 0){
					B = 0;
				}
				if (B > 255){
					B = 255;
				}
				
				rgbChannel[offset] = R;
				rgbChannel[offset+1] = G;
				rgbChannel[offset+2] = B;
				offset = offset + 3;
				counter++;
	    	}
	    }
	    readImageData(rgbChannel, 1, horizontalPixel, verticalPixel);
	}
	 
		private static void readImageData(double[] rgbChannel, int type, int horizontalPixel, 
				int verticalPixel){
			 javax.swing.JFrame frame = new javax.swing.JFrame();
			 int frameWidth = horizontalPixel + 100; int frameHeight = verticalPixel + 100; 
			 frame.setSize(frameWidth, frameHeight);
			 frame.setVisible(true); 	 
			 frame.getContentPane().add(new Points(rgbChannel, type, horizontalPixel, verticalPixel));	
		 }
	
	//DCT transform functions
		private void transformWithDCT(double[] DCTBlock, double[] channel, int horizontalLength, int verticalLength){
			int m = 0;
			int n = 0;
			int dctICount = 0;
			int dctJCount = 0;
			double[] tempDTValueBlock = new double[64];
			double[] interDCTBlock = new double[64];
			
			int resolutionVariant;
			
			if ((horizontalLength * verticalLength) > DCTBlock.length){
				resolutionVariant = 2;
			}
			else
			{
				resolutionVariant = 1;
			}
			
			while ((dctICount * 8 + dctJCount) <= ((((horizontalLength / resolutionVariant) * (verticalLength / resolutionVariant))) - 1)){
				for (int i = 0; i < 8; i++){
					if (m > 7){
						m = 0;
					}
					for (int j = 0; j < 8; j++){	
						if ((dctICount * 8 + dctJCount) >= ((((horizontalLength / resolutionVariant) * (verticalLength / resolutionVariant))) - 1)){
							break;
						}
						interDCTBlock[m * 8 + n] = DCTBlock[dctICount * 8 + dctJCount];
						dctJCount++;
						n++;
					}
					dctICount++;
					dctJCount = 0;
					n = 0;
					m++;
				}
				tempDTValueBlock = basisMatrixAndCalc(interDCTBlock);
				if ((dctICount * 8 + dctJCount) >= ((((horizontalLength / resolutionVariant) * (verticalLength / resolutionVariant))) - 1)){
					break;
				}
				else
				{
					addToValueTable(tempDTValueBlock, channel, dctICount, dctJCount);
				}
			}
		}
		
		//Add the new values into either the U, V or Y compression arrays	
		private void addToValueTable(double[] tempYDTValueBlock, double[] channel, double dctICount, double dctJCount){
			int m = 0;
			int n = 0;
			for (int i = (int)(dctICount - 8); i < ((dctICount - 8) + 8); i++){
				if (m >= 8){
					m = 0;
				}
				for (int j = (int)(dctJCount); j < ((dctJCount) + 8); j++){
					
					channel[i * 8 + j] = tempYDTValueBlock[m * 8 + n];
					n++;
				}
				m++;
				n = 0;
			}				
		}
		

		private double[] basisMatrixAndCalc(double[] DCTBlock) {		
			double[] tempDCTBlock = new double[64];	
			int i, j, u, v;
			double s;

			  for (i = 0; i < 8; i++)
			    for (j = 0; j < 8; j++)
			    {
			      s = 0;

			      for (u = 0; u < 8; u++)
			        for (v = 0; v < 8; v++)
			          s += DCTBlock[u * 8 +  v] * Math.cos((2 * i + 1) * u * Math.PI / 16) *
			        		  Math.cos((2 * j + 1) * v * Math.PI / 16) *
			               ((u == 0) ? 1 / Math.sqrt(2) : 1.) *
			               ((v == 0) ? 1 / Math.sqrt(2) : 1.);

			      tempDCTBlock[i * 8 + j] = Math.round(s / 4);
			    }
			  return tempDCTBlock;
		}
		
	
	private void reverseQuantization(int[] quantTable, int[] compressionChannel, double[] deCompression, 
			int horizontalLength, int verticalLength){
		double[] quantiziedBlock = new double[64];	
		int indexChannelI = 0;
		int indexChannelJ = 0;
		int resolutionVariant;
		
		if ((horizontalLength * verticalLength) > compressionChannel.length){
			resolutionVariant = 2;
		}
		else
		{
			resolutionVariant = 1;
		}	
		
		while ((indexChannelI * 8 + indexChannelJ) <= ((horizontalLength / resolutionVariant) * (verticalLength / resolutionVariant)) - 1){
			for (int i = 0; i < 8; i++){
				for (int j = 0; j < 8; j++){					
					quantiziedBlock[i * 8 + j] = Math.round((compressionChannel[indexChannelI * 8 + indexChannelJ] * quantTable[i * 8 + j]));
					indexChannelJ++;
				}
				indexChannelI++;
				indexChannelJ = 0;
			}
			if ((indexChannelI * 8 + indexChannelJ) >= ((horizontalLength / resolutionVariant) * (verticalLength / resolutionVariant)) - 1){
				break;
			}
			else{
				addToTable(deCompression, quantiziedBlock, indexChannelI, indexChannelJ);
			}
		}
	}
	
	//Add the new values into either the U, V or Y compression arrays	
		private void addToTable(double[] deCompression, double[] tempYDTValueBlock, double dctICount, double dctJCount){
			int m = 0;
			int n = 0;
			for (int i = (int)(dctICount - 8); i < ((dctICount - 8) + 8); i++){
				if (m >= 8){
					m = 0;
				}
				for (int j = (int)(dctJCount); j < ((dctJCount) + 8); j++){
					deCompression[i * 8 + j] = tempYDTValueBlock[m * 8 + n];
					n++;
				}
				m++;
				n = 0;
			}				
		}
		
	private void decompressRunLength(int[] runLengthCompression, int compressionLength, int offset, byte[] byteArray){
		int compressionIndex = 0;
		for (int i = offset; i < (compressionLength + offset) - 5; i+=4){
			compressionIndex = expandRun((((byteArray[i] & 0xFF) << 8) | ((byteArray[i+1] & 0xFF) << 0) - 64),
					(((byteArray[i+2] & 0xFF) << 8) | ((byteArray[i+3] & 0xFF)) - 64),
					runLengthCompression, compressionIndex);
		}
	}
	
	private int expandRun(int lengthOfRun, int value, int[] runLengthCompression, int compressionIndex){
		int i = compressionIndex;
		if (!(compressionIndex >= runLengthCompression.length)){
			for (; i < (compressionIndex + lengthOfRun); i++){
				runLengthCompression[i] = value;
			}
		}
		return i;
	}
	
	private double convertToB(double Y, double U, double V) {
		return (1.0 * Y + 1.772 * U  + 0 * V);
	}

	private double convertToG(double Y, double U, double V) {
		return ( 1.0 * Y - 0.344136 * U - 0.714136 * V);
	}

	private double convertToR(double Y, double U, double V) {
		return (1.0 * Y + 0 * U + 1.402 * V);
	}
}
