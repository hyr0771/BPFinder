package entity;
import java.util.HashMap;

public class Compound {
	public String CompoundID;
	public int atomNum;
	public int edgeNum;
	public String edgeInfo;
	public HashMap<String,String> atomMap = new HashMap<String, String>();
	public String getCompoundID() {
		return CompoundID;
	}
	public void setCompoundID(String compoundID) {
		CompoundID = compoundID;
	}
	public int getAtomNum() {
		return atomNum;
	}
	public void setAtomNum(int atomNum) {
		this.atomNum = atomNum;
	}
	public int getEdgeNum() {
		return edgeNum;
	}
	public void setEdgeNum(int edgeNum) {
		this.edgeNum = edgeNum;
	}
	public String getEdgeInfo() {
		return edgeInfo;
	}
	public void setEdgeInfo(String edgeInfo) {
		this.edgeInfo = edgeInfo;
	}
	
	public HashMap<String, String> getAtomMap() {
		return atomMap;
	}
	public void setAtomMap(HashMap<String, String> atomMap) {
		this.atomMap = atomMap;
	}
	public String ToString()
	{
		return "CompoundID:"+this.CompoundID+"\n"+"atomNum:"+this.atomNum+"\n"+"edgeNum:"+this.edgeNum+"\n"+"edgeInfo:"+"\n"+this.edgeInfo+"\n"+"AtomMap:"+atomMap.toString();
	}
}
