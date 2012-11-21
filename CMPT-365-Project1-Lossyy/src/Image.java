import java.util.Vector;


public class Image {
	/**
	This is an immutable object, only getters are in here
	*/
	
	//Converted RGB values in YUV
	protected double[] Y;
	protected double[] U;
	protected double[] V;
	
	protected Vector<Double> uSubSampled, vSubSampled;
	
	//Size of image
	protected int verticalSize;
	protected int horizontalSize;
	
	public Image(double[] Y, double[] U, double[] V, int verticalSize, int horizontalSize){
		this.Y = Y;
		this.U = U;
		this.V = V;
		this.verticalSize = verticalSize;
		this.horizontalSize = horizontalSize;		
	}
	
	public void setChromaSubSampled(Vector<Double> uSubSampled, Vector<Double> vSubSampled){
		this.uSubSampled = uSubSampled;
		this.vSubSampled = vSubSampled;
	}
	
	public double getSubUat(int x, int y){
		return (uSubSampled.get(x * 8 + y));
	}
	
	public double getSubVat(int x, int y){
		return (vSubSampled.get(x * 8 + y));
	}
	
	//Getters
	public int getVerticalSize(){
		return verticalSize;
	}
	
	public int getHorizontalSize(){
		return horizontalSize;
	}

	public double getYAt(int x, int y){
		return Y[x * 8 + y];
	}
	
	public double getUAt(int x, int y){
		return U[x * 8 + y];
	}
	
	public double getVAt(int x, int y){
		return V[x * 8 + y];
	}
	
}
