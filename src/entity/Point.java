package entity;
import java.util.ArrayList;

public class Point {
	public String PointId;
	public ArrayList<Path> paths = null; 
	public ArrayList<Reaction> precursor = new ArrayList<Reaction>();
	public ArrayList<Reaction> behind = new ArrayList<Reaction>();
	public ArrayList<String> precursorStr = new ArrayList<String>();
	public ArrayList<String>behindStr = new ArrayList<String>();
	public ArrayList<CombTreeNode> combTreeNodes = new ArrayList<CombTreeNode>();
	public ArrayList<CombTreeNode> getCombTreeNodes() {
		return combTreeNodes;
	}
	public void setCombTreeNodes(ArrayList<CombTreeNode> combTreeNodes) {
		this.combTreeNodes = combTreeNodes;
	}
	public String getPointId() {
		return PointId;
	}
	public void setPointId(String pointId) {
		PointId = pointId;
	}
	public ArrayList<Path> getPaths() {
		return paths;
	}
	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}
	public ArrayList<Reaction> getPrecursor() {
		return precursor;
	}
	public void setPrecursor(ArrayList<Reaction> precursor) {
		this.precursor = precursor;
	}
	public ArrayList<Reaction> getBehind() {
		return behind;
	}
	public void setBehind(ArrayList<Reaction> behind) {
		this.behind = behind;
	}
	public ArrayList<String> getPrecursorStr() {
		return precursorStr;
	}
	public void setPrecursorStr(ArrayList<String> precursorStr) {
		this.precursorStr = precursorStr;
	}
	public ArrayList<String> getBehindStr() {
		return behindStr;
	}
	public void setBehindStr(ArrayList<String> behindStr) {
		this.behindStr = behindStr;
	}
	public String ToString()
	{
		String s = "";
		s = s + this.PointId;
		s = s +"\n经过路径为：";
		for(Path p:this.paths)
		{
			s = s+p.name+"-";
		}
		s = s + "\n前驱反应为：";
		for(Reaction r :this.precursor)
		{
			s = s + r.getReactionId() +"-";
		}
		s = s + "\n后驱反应为：";
		for(Reaction r :this.behind)
		{
			s = s + r.getReactionId() +"-";
		}
		return s ;
	}
}
