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
	
	double[] coEffcients;
	
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
		
	public Decoder(){
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
		uCompression = new int[(horizontalLength * verticalLength)/4];
		vCompression = new int[(horizontalLength * verticalLength)/4];
		
		yDeCompression = new double[horizontalLength * verticalLength];
		uDeCompression = new double[(horizontalLength * verticalLength)/4];
		vDeCompression = new double[(horizontalLength * verticalLength)/4];
		
		coEffcients = new double[8];
		
		//reverse the runLength encoding and expand the data
		decompressRunLength(uCompression, uCompressionLength, INITIALOFFSET, byteArray);
		decompressRunLength(vCompression, vCompressionLength, INITIALOFFSET + uCompressionLength, byteArray);
		decompressRunLength(yCompression, yCompressionLength, INITIALOFFSET + uCompressionLength + vCompressionLength, byteArray);
		
		//reverse the quantization
		reverseQuantization(lumaQuantTable, yCompression, yDeCompression, horizontalLength, verticalLength);
		reverseQuantization(chromaQuantTable, uCompression, uDeCompression, horizontalLength, verticalLength);
		reverseQuantization(chromaQuantTable, vCompression, vDeCompression, horizontalLength, verticalLength);
		initializeCoefficients();
		transformWithDCT(yDeCompression);
		transformWithDCT(uDeCompression);
		transformWithDCT(vDeCompression);
	}
	
	private void initializeCoefficients() {
        for (int i = 1; i < 8; i++) {
        	coEffcients[i] = 1;
        }
        coEffcients[0] = 1/Math.sqrt(2.0);
    }
	
	//DCT transform functions
		private void transformWithDCT(double[] DCTBlock){
			double[] tempDCTBlock = new double[64];			
			double result = 0;

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
		}

		private double basisMatrixAndCalc(double[] DCTBlock, double result, int u, int v, int i, int j) {		
			result += DCTBlock[u * 8 + v] * Math.cos((2 * i + 1) * u * Math.PI / 16) *
					Math.cos((2 * j + 1) * v * Math.PI / 16) *
         ((u == 0) ? 1 / Math.sqrt(2) : 1.) *
         ((v == 0) ? 1 / Math.sqrt(2) : 1.);
			
			return result / 4;
		}
		
	
	private void reverseQuantization(int[] quantTable, int[] compressionChannel, double[] deCompression, 
			int horizontalLength, int verticalLength){
		double[] quantiziedBlock = new double[64];	
		int indexChannelI = 0;
		int indexChannelJ = 0;
		int resolutionVariant;
		
		if ((horizontalLength * verticalLength) > compressionChannel.length){
			resolutionVariant = 4;
		}
		else
		{
			resolutionVariant = 1;
		}	
		
		while ((indexChannelI * 8 + indexChannelJ) <= ((horizontalLength * verticalLength) / resolutionVariant) - 1){
			for (int i = 0; i < 8; i++){
				for (int j = 0; j < 8; j++){
					quantiziedBlock[i * 8 + j] = Math.round((compressionChannel[i * 8 + j] * quantTable[i * 8 + j]));
					indexChannelJ++;
				}
				indexChannelI++;
			}
			addToTable(deCompression, quantiziedBlock, indexChannelI, indexChannelJ);
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
				for (int j = (int)(dctJCount - 64); j < ((dctJCount - 64) + 8); j++){
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
			compressionIndex = expandRun((((byteArray[i] & 0xFF) << 8) | ((byteArray[i+1] & 0xFF) << 0) - 128),
					(((byteArray[i+2] & 0xFF) << 8) | ((byteArray[i+3] & 0xFF)) - 128),
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
}
