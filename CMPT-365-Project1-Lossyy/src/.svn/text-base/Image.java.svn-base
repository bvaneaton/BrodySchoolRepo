
public class Image {
	/**
	This is an immutable object, only getters are in here
	*/
	
	//Converted RGB values in YUV
	protected double[] Y;
	protected double[] U;
	protected double[] V;
	
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
