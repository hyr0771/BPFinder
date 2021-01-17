package entity;
import java.util.ArrayList;
public class CAppearNode {
	public String name = "";
	public int appearTimes = 0;
	public ArrayList<Path> appearPath = new ArrayList<Path>();
	public ArrayList<Reaction> precursor = new ArrayList<Reaction>();
	public ArrayList<Reaction> behind = new ArrayList<Reaction>();
	public ArrayList<String> precursorStr = new ArrayList<String>();
	public ArrayList<String> behindStr = new ArrayList<String>();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAppearTimes() {
		return appearTimes;
	}
	public void setAppearTimes(int appearTimes) {
		this.appearTimes = appearTimes;
	}
	public ArrayList<Path> getAppearPath() {
		return appearPath;
	}
	public void setAppearPath(ArrayList<Path> appearPath) {
		this.appearPath = appearPath;
	}
	public String ToString()
	{
		String s = "";
		s = s + "化合物名称："+this.name;
		s = s+"  出现次数:"+this.appearTimes+"\n";
		return s;
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
}
