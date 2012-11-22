class Node 
{

  private Node left;
  private Node right;
  public char code;
  double value = -6666;
  double probability;

  public Node(double value, double probability) {
  this.value = value;
  this.probability = probability;
  }
  
  public Node() {
	  }
  
  public boolean isLeaf() {
      assert (left == null && right == null) || (left != null && right != null);
      return (left == null && right == null);
  }
  
  public void setLeft(Node left){
		this.left = left;
	}
	public void setRight(Node right){
		this.right = right;
	}
	public void setValue(double value){
		this.value = value;
	}
	public void setProbability(double probability){
		this.probability = probability;
	}

	public Node getLeft(){
		return left;
	}
	public Node getRight(){
		return right;
	}
	public double getValue(){
		return value;
	}
	public double getProbability(){
		return probability;
	}	
}