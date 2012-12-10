import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.Math;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

@SuppressWarnings("unused")
public class Encoder {
	//static final
	private static final int INITIALOFFSET = 12;
	private static final int INTERNODEVALUE = -6666;
	
	//channels
	private double[] yChannel, uChannel, vChannel;		
	//DCT converted channels
	private double[] yCompressionChannel, uCompressionChannel, vCompressionChannel;
	
	//Huffman coded tables
	private int[] yEntropyCodedChannel, uEntropyCodedChannel, vEntropyCodedChannel;
	
	private String filename;
	
	//Huffman coded tables after run-length encoding.
	private Vector<Integer> yRunLengthCodedChannel = new Vector<Integer>();
	private Vector<Integer> uRunLengthCodedChannel = new Vector<Integer>();
	private Vector<Integer> vRunLengthCodedChannel = new Vector<Integer>();
	
	//Chroma channels with sub sampling 	
	private Vector<Double> uSubChannel = new Vector<Double>();
	private Vector<Double> vSubChannel = new Vector<Double>();
	
	//codeTable for Y, U and V
	SortedMap<Double, Integer> codeTableY = new TreeMap<Double, Integer>();
	SortedMap<Double, Integer> codeTableU = new TreeMap<Double, Integer>();
	SortedMap<Double, Integer> codeTableV = new TreeMap<Double, Integer>();
	
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
		//made a change
	}
	
	public MeanSquare encode(byte[] image, String filename, int horizontalPixel, int verticalPixel){	
		this.filename = filename;
		
		yCompressionChannel = new double[horizontalPixel * verticalPixel];
		uCompressionChannel = new double[(horizontalPixel / 2) * (verticalPixel / 2)];
		vCompressionChannel = new double[(horizontalPixel / 2) * (verticalPixel / 2)];

		yEntropyCodedChannel = new int[horizontalPixel * verticalPixel];
		uEntropyCodedChannel = new int[(horizontalPixel / 2) * (verticalPixel / 2)];
		vEntropyCodedChannel = new int[(horizontalPixel / 2) * (verticalPixel / 2)];
		
		convertRGBtoYUV(image, horizontalPixel, verticalPixel);
	    
		
		//double[] testing = transformWithDCT(test);
	    Image convertedImage = new Image(yChannel, uChannel , vChannel, verticalPixel, horizontalPixel);
	    chromaSubSample(convertedImage);
	    setImageChromaSubs(convertedImage, uSubChannel, vSubChannel);
	    convertAndTransformBlocks(convertedImage);	  
	    //entropyCodeChannels(convertedImage);
	    runLengthEncoding();
	    saveCompressedFile(convertedImage);
	    return (new MeanSquare(yChannel, uSubChannel, vSubChannel));
	}
	
	//set chroma sub samples 4:2:0 for the image
	private void setImageChromaSubs(Image convertedImage, Vector<Double> uSubChannel, Vector<Double> vSubChannel){
		convertedImage.setChromaSubSampled(uSubChannel, vSubChannel);
	}
	
	private void chromaSubSample(Image convertedImage){
		int columnSkip = 0;
		int evenSkip = 0;
		int oddSkip = 1;
		int uCounter = 0;
		int vCounter = 0;
		int vert = convertedImage.getHorizontalSize();
		for (int i = 0; i < (convertedImage.getVerticalSize()); i++){
			for (int j = 0; j < (convertedImage.getHorizontalSize()); j+=2){
				if (i % 2 == 0){
	    			uSubChannel.add(uChannel[uCounter]);
	    			 			
	    		}
	    		else{
	    			vSubChannel.add(vChannel[vCounter]);
	    				
	    		}	
				uCounter+=2;
				vCounter+=2;
			}
		}
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
				double U = convertToU(R, G, B);
				double V = convertToV(R, G, B);
				
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
	}
	
	private void runLengthEncoding(){
		runLengthEncodeChannels(yCompressionChannel, yRunLengthCodedChannel);
		runLengthEncodeChannels(uCompressionChannel, uRunLengthCodedChannel);
		runLengthEncodeChannels(vCompressionChannel, vRunLengthCodedChannel);
		/*runLengthEncodeChannels(yEntropyCodedChannel, yRunLengthCodedChannel);
		runLengthEncodeChannels(uEntropyCodedChannel, uRunLengthCodedChannel);
		runLengthEncodeChannels(vEntropyCodedChannel, vRunLengthCodedChannel);	*/
	}
	
	static String changeExtension(String originalName, String newExtension) {
	    int lastDot = originalName.lastIndexOf(".");
	    if (lastDot != -1) {
	        return originalName.substring(0, lastDot) + newExtension;
	    } else {
	        return originalName + newExtension;
	    }
	}

	private void saveCompressedFile(Image convertedImage) {
		try {			
			//filename.replace(".bmp", ".IM3");
			filename = changeExtension(filename, ".IM3");
	        FileOutputStream fos = new FileOutputStream(filename);
	        Writer out = new OutputStreamWriter(fos, "UTF-16BE");
	        //save the code tables     
	        out.write(uRunLengthCodedChannel.size());
	        out.write(vRunLengthCodedChannel.size());
	        out.write(convertedImage.getHorizontalSize());
	        out.write(convertedImage.getVerticalSize());
	        /*out.write(INITIALOFFSET);
	        out.write(codeTableY.size());
	        out.write(codeTableU.size());
	        out.write(codeTableV.size());       
	        
	        for (double key : codeTableY.keySet()){
	        	out.write(codeTableY.get(key));
	        	out.write((int)key);
	        }
	        for (double key : codeTableU.keySet()){
	        	out.write(codeTableU.get(key));
	        	out.write((int)key);
	        }	        
	        for (double key : codeTableV.keySet()){
	        	out.write(codeTableV.get(key));
	        	out.write((int)key);
	        }*/
	        
	        //save run-length coded channels of data and shift the values up by 128.		        
	        for (int i = 0; i < uRunLengthCodedChannel.size(); i++){
	        	int byteValue = uRunLengthCodedChannel.get(i) + 64;
	        	out.write(byteValue);
	        }
	        for (int i = 0; i < vRunLengthCodedChannel.size(); i++){
	        	int byteValue = vRunLengthCodedChannel.get(i) + 64;
	        	out.write(byteValue);
	        }	
	        for (int i = 0; i < yRunLengthCodedChannel.size(); i++){
	        	int byteValue = yRunLengthCodedChannel.get(i) + 64;
	        	out.write(byteValue);
	        }	       
	        out.close();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void runLengthEncodeChannels(double[] entropyCodedChannel, Vector<Integer> runLengthCodedChannel){
		int counter = 0;		
		while (counter < entropyCodedChannel.length){
			int encodeCounter = 1;
			while((counter < entropyCodedChannel.length - 1) && entropyCodedChannel[counter] == entropyCodedChannel[counter + 1]){
				encodeCounter++;
				counter++;
			}
			runLengthCodedChannel.add(encodeCounter);
			runLengthCodedChannel.add((int)entropyCodedChannel[counter]);
			counter++;
		}
	}

	private void entropyCodeChannels(Image convertedImage,
			double[] compressionChannel, SortedMap<Double, Integer> codeTable, 
			int[] entropyCodedChannel) {
		
		SortedMap<Double, Integer> probabiliyTable = new TreeMap<Double, Integer>();
		//Create the probability table to create our tree with
		if (compressionChannel.length > ((convertedImage.getHorizontalSize() / 2) * convertedImage.getVerticalSize()  / 2)){
			createProbabilityTableLuma(convertedImage, probabiliyTable, compressionChannel);
		} 
		else {
			createProbabilityTableChroma(convertedImage, probabiliyTable, compressionChannel);
		}
		
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
		createCodeTable(huffmanTree, 1, codeTable);	
		convertValuesIntoCodes(codeTable, compressionChannel, entropyCodedChannel);
	}
	
	private void convertValuesIntoCodes(SortedMap<Double, Integer> codeTable,
			double[] compresionChannel, int[] entropyCodedChannel){
		for(int i = 0; i < compresionChannel.length; i++){
			entropyCodedChannel[i] = codeTable.get(compresionChannel[i]);
		}
	}
	
	private void createCodeTable(Node huffmanTree, int code, SortedMap<Double, Integer> codeTable){
        if( huffmanTree.getLeft() != null ){        	
        	 if (!(huffmanTree.getValue() == INTERNODEVALUE)){
        		 this.createCodeTable(huffmanTree.getLeft(), code, codeTable);
             } 
        	 else{        		 
        		 this.createCodeTable(huffmanTree.getLeft(), code << 1, codeTable);
        	 }
        } 
        
        if( huffmanTree.getRight() != null ){
        	if (!(huffmanTree.getValue() == INTERNODEVALUE)){
        		this.createCodeTable(huffmanTree.getRight(), code, codeTable);
            }  
        	else{
        		this.createCodeTable(huffmanTree.getRight(),  (code << 1) | 1, codeTable);
       	 	}
        }
        
        if (huffmanTree.getRight() == null && huffmanTree.getLeft() == null){
        	codeTable.put(huffmanTree.getValue(), code);
        }
    }	

	private void createProbabilityTableLuma(Image convertedImage, 
			SortedMap<Double, Integer> probabiliyTable, double[] compressionChannel) {
		
		for (int i = 0; i < convertedImage.getVerticalSize(); i++){
			for (int j = 0; j < convertedImage.getHorizontalSize(); j++){
				double value = (compressionChannel[(i * convertedImage.getHorizontalSize() + j)]);
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
	
	private void createProbabilityTableChroma(Image convertedImage, 
			SortedMap<Double, Integer> probabiliyTable, double[] compressionChannel) {
		
		for (int i = 0; i < (convertedImage.getVerticalSize()  / 2); i++){
			for (int j = 0; j < (convertedImage.getHorizontalSize() / 2); j++){
				double value = (compressionChannel[(i * (convertedImage.getHorizontalSize() /2) + j)]);
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
				if (m >= 8){
					m = 0;
				}
				for (int j = 0; j < 8; j++){
					yDCTBlock[m * 8 + n] = convertedImage.getYAt(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				dctJCount = 0;
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
		
		while ((dctICount * 8 + dctJCount) <= ((convertedImage.getHorizontalSize() /2 ) * (convertedImage.getVerticalSize()  / 2)) - 1){
			for (int i = 0; i < 8; i++){
				if (m >= 8){
					m = 0;
				}			
				if ((dctICount * 8 + dctJCount) >= ((convertedImage.getHorizontalSize() /2 ) * (convertedImage.getVerticalSize()  / 2)) - 1){
					break;
				}
				for (int j = 0; j < 8; j++){
					uDCTBlock[m * 8 + n] = convertedImage.getSubUat(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				dctJCount = 0;
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
		
		while ((dctICount * 8 + dctJCount) <= ((convertedImage.getHorizontalSize() / 2) * convertedImage.getVerticalSize()  / 2) - 1){
			for (int i = 0; i < 8; i++){
				if (m >= 8){
					m = 0;
				}
				if ((dctICount * 8 + dctJCount) >= ((convertedImage.getHorizontalSize() /2 ) * (convertedImage.getVerticalSize()  / 2)) - 1){
					break;
				}
				for (int j = 0; j < 8; j++){
					vDCTBlock[m * 8 + n] = convertedImage.getSubVat(dctICount, dctJCount);
					dctJCount++;
					n++;
				}
				dctICount++;
				dctJCount = 0;
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
			for (int j = (int)(dctJCount); j < ((dctJCount) + 8); j++){
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
			for (int j = (int)(dctJCount); j < ((dctJCount) + 8); j++){
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
			for (int j = (int)(dctJCount); j < ((dctJCount) + 8); j++){
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
		int i, j, u, v;
		double[] out = new double[64];
		  double s;

		  for (i = 0; i < 8; i++)
		    for (j = 0; j < 8; j++)
		    {
		      s = 0;

		      for (u = 0; u < 8; u++)
		        for (v = 0; v < 8; v++)
		          s += DCTBlock[u * 8 + v] * Math.cos((2 * u + 1) * i * Math.PI / 16) *
		        		  Math.cos((2 * v + 1) * j * Math.PI / 16) *
		               ((i == 0) ? 1 / Math.sqrt(2) : 1) *
		               ((j == 0) ? 1 / Math.sqrt(2) : 1);

		      out[i * 8 + j] = Math.round(s / 4);
		    }
		  return out;
	}
	
	//Conversion methods for YUV
		private double convertToV(int R, int G, int B) {
			return ( 0.50000 * R - 0.41869 * G - 0.08131 * B);
		}

		private double convertToU(int R, int G, int B) {
			return (-0.16874 * R - 0.33126 * G + 0.50000 * B);
		}

		private double convertToY(int R, int G, int B) {
			return (0.299 * R + 0.587 * G + 0.114 * B);
		}
}
