import java.util.Vector;


public class MeanSquare {
	double[] beforeYChannel;
	Vector<Double> beforeUChannel;
	Vector<Double> beforeVChannel;
	
	double[] afterYChannel;
	double[] afterUChannel;
	double[] afterVChannel;
	
	public MeanSquare(double[] beforeYChannel, Vector<Double> beforeUChannel, Vector<Double> beforeVChannel){
		this.beforeYChannel = beforeYChannel;
		this.beforeUChannel = beforeUChannel;
		this.beforeVChannel = beforeVChannel;
	}
	
	public void setAfterChannels(double[] afterYChannel, double[] afterUChannel, double[] afterVChannel){
		this.afterYChannel = afterYChannel;
		this.afterUChannel = afterUChannel;
		this.afterVChannel = afterVChannel;
	}
	
	public void computeAndReportPSNR(){
		long sumY = 0;
		long sumU = 0;
		long sumV = 0;
		
		double PSNRY = 0;
		double PSNRU = 0;
		double PSNRV = 0;
		
		for (int i = 0; i < beforeYChannel.length; i++){
			sumY += Math.pow((beforeYChannel[i] - afterYChannel[i]), 2);
		}
		for (int i = 0; i < beforeUChannel.size(); i++){
			sumU += Math.pow((beforeUChannel.get(i) - afterYChannel[i]), 2);
		}
		for (int i = 0; i < beforeVChannel.size(); i++){
			sumV += Math.pow((beforeVChannel.get(i) - afterYChannel[i]), 2);
		}
		double interValue = (1 / beforeUChannel.size());
		sumY = (long) (interValue * sumY);
		sumU = ((1 / beforeUChannel.size()) * sumU);
		sumV = ((1 / beforeVChannel.size()) * sumV);
		
		PSNRY = (20 * Math.log10(255)) - (10 * Math.log10(sumY));
		PSNRU = (20 * Math.log10(255)) - (10 * Math.log10(sumU));
		PSNRV = (20 * Math.log10(255)) - (10 * Math.log10(sumV));
		
		/*System.out.println("The MSE for the Y channel is: " + PSNRY);
		System.out.println("The MSE for the U channel is: " + PSNRU);
		System.out.println("The MSE for the V channel is: " + PSNRV);*/
	}
	
}
