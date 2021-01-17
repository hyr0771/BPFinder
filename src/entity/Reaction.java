package entity;
import java.util.ArrayList;
import java.util.HashMap;

public class Reaction {
	public String subtrateId;
	public String productId;
	public String subtrateName;
	public String productName;
	public double similarity;
	public double freeEnergy;
	public String atomTransfer;
	public ArrayList<Integer> edge = null;
	public HashMap<Path,ArrayList<Integer>> start2transEdge = new HashMap<Path, ArrayList<Integer>>();
	public HashMap<String,ArrayList<Integer>> string2transEdge = new HashMap<String, ArrayList<Integer>>();
	public double weight = -1;
	public HashMap<String, ArrayList<Integer>> getString2transEdge() {
		return string2transEdge;
	}
	public void setString2transEdge(
			HashMap<String, ArrayList<Integer>> string2transEdge) {
		this.string2transEdge = string2transEdge;
	}
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public HashMap<Path, ArrayList<Integer>> getStart2transEdge() {
		return start2transEdge;
	}
	public void setStart2transEdge(HashMap<Path, ArrayList<Integer>> start2transEdge) {
		this.start2transEdge = start2transEdge;
	}
	public String reactionId;
	public Reaction()
	{
	}
	public Reaction(String reactionId, String subtrateId, String productId,
			String subtrateName, String productName, double similarity,
			double freeEnergy, String atomTransfer, ArrayList<Integer> edge,double weight) {
		super();
		this.reactionId = reactionId;
		this.subtrateId = subtrateId;
		this.productId = productId;
		this.subtrateName = subtrateName;
		this.productName = productName;
		this.similarity = similarity;
		this.freeEnergy = freeEnergy;
		this.atomTransfer = atomTransfer;
		this.edge = edge;
		this.weight = weight;
	}
	public String getReactionId() {
		return reactionId;
	}
	public void setReactionId(String reactionId) {
		this.reactionId = reactionId;
	}
	public String getSubtrateId() {
		return subtrateId;
	}
	public void setSubtrateId(String subtrateId) {
		this.subtrateId = subtrateId;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getSubtrateName() {
		return subtrateName;
	}
	public void setSubtrateName(String subtrateName) {
		this.subtrateName = subtrateName;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
	public double getFreeEnergy() {
		return freeEnergy;
	}
	public void setFreeEnergy(double freeEnergy) {
		this.freeEnergy = freeEnergy;
	}
	public String getAtomTransfer() {
		return atomTransfer;
	}
	public void setAtomTransfer(String atomTransfer) {
		this.atomTransfer = atomTransfer;
	}
	public ArrayList<Integer> getEdge() {
		return edge;
	}
	public void setEdge(ArrayList<Integer> edge) {
		this.edge = edge;
	}
	public String toString()
	{
		String s = "";
		s = "name:"+reactionId+",atomInfo:"+atomTransfer;
		return s;
	}
}
