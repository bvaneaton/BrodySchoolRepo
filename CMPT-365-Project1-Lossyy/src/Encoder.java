import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Math;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;


public class Encoder {
	//static final
	private static final int DCTSIZE = 8;
	private static final int INTERNODEVALUE = -6666;
	
	//channels
	private double[] yChannel, uChannel, vChannel;		
	//DCT converted channels
	private double[] yCompressionChannel, uCompressionChannel, vCompressionChannel;
	
	//Huffman coded tables
	private String[] yEntropyCodedChannel, uEntropyCodedChannel, vEntropyCodedChannel;
	
	//Huffman coded tables after run-length encoding.
	private byte[] yRunLengthCodedChannel, uRunLengthCodedChannel, vRunLengthCodedChannel;
	
	//codeTable for Y, U and V
	SortedMap<Double, String> codeTableY = new TreeMap<Double, String>();
	SortedMap<Double, String> codeTableU = new TreeMap<Double, String>();
	SortedMap<Double, String> codeTableV = new TreeMap<Double, String>();
	
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
	
	public Encoder(){
	}
	
	public void encode(byte[] image, int horizontalPixel, int verticalPixel){				
		yCompressionChannel = new double[horizontalPixel * verticalPixel];
		uCompressionChannel = new double[horizontalPixel * verticalPixel];
		vCompressionChannel = new double[horizontalPixel * verticalPixel];

		yEntropyCodedChannel = new String[horizontalPixel * verticalPixel];
		uEntropyCodedChannel = new String[horizontalPixel * verticalPixel];
		vEntropyCodedChannel = new String[horizontalPixel * verticalPixel];
		
		yRunLengthCodedChannel = new byte[horizontalPixel * verticalPixel];
		uRunLengthCodedChannel = new byte[horizontalPixel * verticalPixel];
		vRunLengthCodedChannel = new byte[horizontalPixel * verticalPixel];
		
		convertRGBtoYUV(image, horizontalPixel, verticalPixel);
	    
	    Image convertedImage = new Image(yChannel, uChannel , vChannel, verticalPixel, horizontalPixel);
	    convertAndTransformBlocks(convertedImage);	  
	    entropyCodeChannels(convertedImage);
	}

	private void convertRGBtoYUV(byte[] image, int horizontalPixel,
			int verticalPixel) {
		yChannel = new double[horizontalPixel * verticalPixel];
		uChannel = new double[horizontalPixel * verticalPixel];
		vChannel = new double[horizontalPixel * verticalPixel];

		int inputIndex = 0;
		int offset = image[10];
	    for (int i = verticalPixel-1; i >= 0; i--){
	    	for (int j = 0; j <= horizontalPixel-1; j++){
	    		
	    		int R = ((int)image[offset+2] & 0xFF);
				int G = ((int)image[offset+1] & 0xFF);
				int B = ((int)image[offset] & 0xFF);				
				
				double Y = convertToY(R, G, B);
				double U = convertToU(B, Y);
				double V = convertToV(R, Y);
				
				yChannel[inputIndex] = Y;
				uChannel[inputIndex] = U;
				vChannel[inputIndex] = V;					
				
				inputIndex++;
	    		offset = offset + 3;
	    	}
	    }
	}
	
	private void entropyCodeChannels(Image convertedImage){
				
		entropyCodeChannels(convertedImage, yCompressionChannel, codeTableY, yEntropyCodedChannel);
		entropyCodeChannels(convertedImage, uCompressionChannel, codeTableU, uEntropyCodedChannel);
		entropyCodeChannels(convertedImage, vCompressionChannel, codeTableV, vEntropyCodedChannel);
		System.out.print("");
		runLengthEncodeChannels(yEntropyCodedChannel, yRunLengthCodedChannel);
		/*try {
	        FileOutputStream fos = new FileOutputStream("test.IM3");
	        Writer out = new OutputStreamWriter(fos, "UTF8");
	        for (int i = 0; i < yEntropyCodedChannel.length; i++){
	        	out.write(yEntropyCodedChannel[i]);
	        }
	        for (int i = 0; i < uEntropyCodedChannel.length; i++){
	        	out.write(uEntropyCodedChannel[i]);
	        }
	        for (int i = 0; i < uEntropyCodedChannel.length; i++){
	        	out.write(vEntropyCodedChannel[i]);
	        }
	        out.close();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }*/
	}
	
	private void runLengthEncodeChannels(String[] entropyCodedChannel, byte[] runLengthCodedChannel){
		runLengthCodedChannel = entropyCodedChannel[0].getBytes();
	}

	private void entropyCodeChannels(Image convertedImage,
			double[] compressionChannel, SortedMap<Double, String> codeTable, 
			String[] entropyCodedChannel) {
		
		SortedMap<Double, Integer> probabiliyTable = new TreeMap<Double, Integer>();
		//Create the probability table to create our tree with
		createProbabilityTable(convertedImage, probabiliyTable, compressionChannel);
		
		Vector<Node> dataNodes = new Vector<Node>();
		int nodecounter = 0;
		//convert the probability table into nodes with a value / probability
		nodecounter = convertTablesToNodes(probabiliyTable, dataNodes,
				nodecounter);
		
		//Sort the nodes via probability
		bubbleSort(dataNodes);		
		
		//create the huffman tree
		createHuffmanTree(dataNodes);
		
		Node huffmanTree = dataNodes.get(0);
		
		//create the codeTable
		createCodeTable(huffmanTree, "", codeTable);	
		convertValuesIntoCodes(codeTable, compressionChannel, entropyCodedChannel);
	}
	
	private void convertValuesIntoCodes(SortedMap<Double, String> codeTable,
			double[] compresionChannel, String[] entropyCodedChannel){
		for(int i = 0; i < compresionChannel.length; i++){
			entropyCodedChannel[i] = codeTable.get(compresionChannel[i]);
		}
	}
	
	private void createCodeTable(Node huffmanTree, String code, SortedMap<Double, String> codeTable){
        if( huffmanTree.getLeft() != null ){
        	 if (!(huffmanTree.getValue() == INTERNODEVALUE)){
        		 this.createCodeTable(huffmanTree.getLeft(), code, codeTable);
             } 
        	 else{
        		 this.createCodeTable(huffmanTree.getLeft(), code + '0', codeTable);
        	 }
        }   
        
        if( huffmanTree.getRight() != null ){
        	if (!(huffmanTree.getValue() == INTERNODEVALUE)){
        		this.createCodeTable(huffmanTree.getRight(), code, codeTable);
            }  
        	else{
        		this.createCodeTable(huffmanTree.getRight(), code + '1', codeTable);
       	 	}
        }
        
        if (huffmanTree.getRight() == null && huffmanTree.getLeft() == null){
        	codeTable.put(huffmanTree.getValue(), code);
        }
    }	

	private void createProbabilityTable(Image convertedImage, 
			SortedMap<Double, Integer> probabiliyTable, double[] compressionChannel) {
		for (int i = 0; i < convertedImage.getVerticalSize(); i++){
			for (int j = 0; j < convertedImage.getHorizontalSize(); j++){
				double value = (compressionChannel[(i * convertedImage.getVerticalSize() + j)]);
				if (probabiliyTable.containsKey(value)){
					int probValue = probabiliyTable.get(value);
					probValue++;
					probabiliyTable.remove(value);
					probabiliyTable.put(value, probValue);	
				}
				else{					
					probabiliyTable.put(value, 1);
				}				
			}
		}
	}

	private int convertTablesToNodes(
			SortedMap<Double, Integer> probabiliyTable, Vector<Node> dataNodes,
			int nodecounter) {
		while(!(probabiliyTable.isEmpty())){
			double value = probabiliyTable.lastKey();
			double probability = probabiliyTable.get(value);
			Node newNode = new Node(value, probability);
			dataNodes.add(nodecounter, newNode);
			nodecounter++;
			probabiliyTable.remove(value);
		}
		return nodecounter;
	}

	private void createHuffmanTree(Vector<Node> dataNodes) {
		while(dataNodes.size()!= 1){
			Node newRoot = new Node();
			newRoot.probability = (dataNodes.get(0).probability) + (dataNodes.get(1).probability);
			newRoot.setLeft(dataNodes.get(0));
			newRoot.setRight(dataNodes.get(1));
			dataNodes.set(0, newRoot);
			dataNodes.remove(1);			
			bubbleSort(dataNodes);	
		}
	}
	
	private static void bubbleSort(Vector<Node> dataNodes){
	int i, j;
	Node tempNode;
	for(i = 0; i < dataNodes.size(); i++){
		for(j = 1; j < (dataNodes.size()-i); j++){
			if(dataNodes.get(j-1).probability > dataNodes.get(j).probability){
				tempNode = dataNodes.get(j-1);
				dataNodes.set(j-1, dataNodes.get(j));
				dataNodes.set(j, tempNode);
			}
		  }
		}
	}
	
	
	private void convertAndTransformBlocks(Image convertedImage){		
		convertAndTransformY(convertedImage);
		convertAndTransformU(convertedImage);
		convertAndTransformV(convertedImage);	
	}

	private void convertAndTransformY(Image convertedImage) {
		int m = 0;
		int n = 0;
		int dctICount = 0;
		int dctJCount = 0;
		double[] tempYDTValueBlock = new double[64];
		double[] yDCTBlock = new double[64];
		
		while ((dctICount * 8 + dctJCount) <= ((convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1)){
			for (int i = 0; i < 8; i++){
				if (m > 7){
					m = 0;
				}
				for (int j = 0; j < 8; j++){
					//weird case for 248 x 200 image causing crashing, by-passed the while loops check, so this is here to
					//prevent the issue, it's sloppy but a quick fix, TODO: fix this.
					if (((dctICount * 8 + dctJCount) >= ((convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1))){
						break;
					}
					yDCTBlock[m * 8 + n] = convertedImage.getYAt(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				n = 0;
				m++;
			}
			tempYDTValueBlock = transformWithDCT(yDCTBlock);
			tempYDTValueBlock = quantizationLuma(tempYDTValueBlock);
			addToYTable(tempYDTValueBlock, dctICount, dctJCount);
		}
	}
	
	private void convertAndTransformU(Image convertedImage) {
		int m = 0;
		int n = 0;
		int dctICount = 0;
		int dctJCount = 0;
		double[] tempUDTValueBlock = new double[64];
		double[] uDCTBlock = new double[64];
		
		while ((dctICount * 8 + dctJCount) <= (convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1){
			for (int i = 0; i < 8; i++){
				if (m > 7){
					m = 0;
				}				
				for (int j = 0; j < 8; j++){
					//weird case for 248 x 200 image causing crashing, by-passed the while loops check, so this is here to
					//prevent the issue, it's sloppy but a quick fix, TODO: fix this.
					if (((dctICount * 8 + dctJCount) >= ((convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1))){
						break;
					}
					uDCTBlock[m * 8 + n] = convertedImage.getUAt(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				n = 0;
				m++;
			}
			tempUDTValueBlock = transformWithDCT(uDCTBlock);
			tempUDTValueBlock = quantizationChroma(tempUDTValueBlock);
			addToUTable(tempUDTValueBlock, dctICount, dctJCount);
		}
	}
	
	private void convertAndTransformV(Image convertedImage) {
		int m = 0;
		int n = 0;
		int dctICount = 0;
		int dctJCount = 0;
		double[] tempVDTValueBlock = new double[64];
		double[] vDCTBlock = new double[64];
		
		while ((dctICount * 8 + dctJCount) <= (convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1){
			for (int i = 0; i < 8; i++){
				if (m > 7){
					m = 0;
				}
				for (int j = 0; j < 8; j++){
					//weird case for 248 x 200 image causing crashing, by-passed the while loops check, so this is here to
					//prevent the issue, it's sloppy but a quick fix, TODO: fix this.
					if (((dctICount * 8 + dctJCount) >= ((convertedImage.getHorizontalSize() * convertedImage.getVerticalSize()) - 1))){
						break;
					}
					vDCTBlock[m * 8 + n] = convertedImage.getVAt(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				n = 0;
				m++;
			}
			tempVDTValueBlock = transformWithDCT(vDCTBlock);
			tempVDTValueBlock = quantizationChroma(tempVDTValueBlock);
			addToVTable(tempVDTValueBlock, dctICount, dctJCount);
		}
	}
	
	
	
	//Add the new values into either the U, V or Y compression arrays	
	private void addToYTable(double[] tempYDTValueBlock, double dctICount, double dctJCount){
		int m = 0;
		int n = 0;
		for (int i = (int)(dctICount - 8); i < ((dctICount - 8) + 8); i++){
			if (m >= 8){
				m = 0;
			}
			for (int j = (int)(dctJCount - 64); j < ((dctJCount - 64) + 8); j++){
				yCompressionChannel[i * 8 + j] = tempYDTValueBlock[m * 8 + n];
				n++;
			}
			m++;
			n = 0;
		}
				
	}
	
	private void addToUTable(double[] tempUDTValueBlock, double dctICount, double dctJCount){
		int m = 0;
		int n = 0;
		for (int i = (int)(dctICount - 8); i < ((dctICount - 8) + 8); i++){
			if (m >= 8){
				m = 0;
			}
			for (int j = (int)(dctJCount - 64); j < ((dctJCount - 64) + 8); j++){
				uCompressionChannel[i * 8 + j] = tempUDTValueBlock[m * 8 + n];
				n++;
			}
			m++;
			n = 0;
		}				
	}
	
	private void addToVTable(double[] tempvDTValueBlock, double dctICount, double dctJCount){
		int m = 0;
		int n = 0;
		for (int i = (int)(dctICount - 8); i < ((dctICount - 8) + 8); i++){
			if (m >= 8){
				m = 0;
			}
			for (int j = (int)(dctJCount - 64); j < ((dctJCount - 64) + 8); j++){
				vCompressionChannel[i * 8 + j] = tempvDTValueBlock[m * 8 + n];
				n++;
			}
			m++;
			n = 0;
		}				
	}
	
	//Quantization functions for chroma and luma
	private double[] quantizationChroma(double[] tempChromaDTValueBlock){
		double[] quantiziedBlock = new double[64];
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				quantiziedBlock[i * 8 + j] = Math.round((tempChromaDTValueBlock[i * 8 + j] / chromaQuantTable[i * 8 + j]));
			}
		}
		return quantiziedBlock;
	}
	
	private double[] quantizationLuma(double[] tempLumaDTValueBlock){
		double[] quantiziedBlock = new double[64];
		
		for (int i = 0; i < 8; i++){
			for (int j = 0; j < 8; j++){
				quantiziedBlock[i * 8 + j] = Math.round((tempLumaDTValueBlock[i * 8 + j] / lumaQuantTable[i * 8 + j]));
			}
		}
		return quantiziedBlock;
	}
	
	//DCT transform functions
	private double[] transformWithDCT(double[] DCTBlock){
		double[] tempDCTBlock = new double[64];
		
		double result = 0;

		for(int u = 0; u < 8; u++) 
		{
			for(int v = 0; v < 8; v++)
			{
				result = 0; // reset summed results to 0
				for(int i = 0; i < 8; i++)
				{
					for(int j = 0; j < 8; j++)
					{
						result = basisMatrixAndCalc(DCTBlock, result, u, v, i, j);
					}
				}
				tempDCTBlock[u * 8 + v] = Math.round(result); //store the results
			}
		}		
		return tempDCTBlock;
	}

	private double basisMatrixAndCalc(double[] DCTBlock, double result, int u, int v, int i, int j) {
		double x = 1;
		double y = 0;
		if (i == 0)
		{
		    x = 1/Math.sqrt(2.0);
		}
		if (j == 0)
		{
		    y = 1/Math.sqrt(2.0);
		}
		
		result = result + ( x * y *
			Math.cos((( Math.PI * u ) / ( 2 * 8 )) * ( 2 * i + 1)) *
			Math.cos((( Math.PI * v ) / ( 2 * 8 )) * ( 2 * j + 1 )) *
			DCTBlock[i * 8 + j]);
		return result;
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
