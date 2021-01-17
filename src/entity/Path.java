package entity;
import java.util.ArrayList;
public class Path {
	public ArrayList<Reaction> rs = new ArrayList<Reaction>();
	public ArrayList<String> rsName = new ArrayList<String>();
	public ArrayList<String> compName = new ArrayList<String>();
	public String name = "-1";
	public boolean initializeRsName = false;
	public boolean initializeCompName = false;
	public double weight = 0;
	public double getWeight() {
		return weight;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public ArrayList<String> getRsName()
	{
		if(initializeRsName)
		{
			return rsName;
		}
		for(int i = 0; i < rs.size();i++)
		{
			rsName.add(rs.get(i).getReactionId());
		}
		this.initializeRsName = true;
		return rsName;
	}
	public ArrayList<String> getCompName()
	{
		if(initializeCompName)
		{
			return compName;
		}
		for(int i = 0; i < rs.size();i++)
		{
			if(i==0)
				continue;
			compName.add(rs.get(i).getSubtrateId());
		}
		this.initializeCompName = true;
		return compName;
	}
	
	public ArrayList<String> getCompName1()
	{
		ArrayList<String> allName = new ArrayList<String>();
		for(int i = 0; i < rs.size();i++)
		{
			if(!allName.contains(rs.get(i).getSubtrateId()))
				allName.add(rs.get(i).getSubtrateId());
			if(!allName.contains(rs.get(i).getProductId()))
				allName.add(rs.get(i).getProductId());
		}
		return allName;
	}
	
	public Reaction findReaction(String name)
	{
		Reaction reaction=null;
		for(Reaction r: rs)
		{
			if(r.getReactionId().equals(name))
				reaction = r;
		}
		return reaction;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Reaction> getRs() {
		return rs;
	}

	public void setRs(ArrayList<Reaction> path) {
		this.rs = path;
	}
	
	public String toString()
	{
		String s = "";
		String r ="";
		String e = "";
		s= s + "路径名："+this.name+"-";
		for(int i = 0; i< this.rs.size();i++)
		{
			r = r+this.rs.get(i).getReactionId()+",";
			if(i==this.rs.size()-1)
				s = s + this.rs.get(i).subtrateId+","+this.rs.get(i).productId;
			else
				s = s+this.rs.get(i).subtrateId+",";
			e = e + rs.get(i).getStart2transEdge().get(this).toString();
		}
		s = s+"----" +r+"----"+e;
		return s;
	}
	
	public String toKEGGString()
	{
		String s = "";
		s= s + "路径名："+this.name+"-";
		for(int i = 0; i< this.rs.size();i++)
		{
			if(i==0)
			{
				s = s + this.rs.get(i).subtrateId+" green"+"\r\n";
				continue;
			}
			if(i==this.rs.size()-1){
				s = s + this.rs.get(i).subtrateId+"\r\n";
				s = s + this.rs.get(i).productId +" green"+"\r\n";
			}
			else
				s = s + this.rs.get(i).subtrateId+"\r\n";
		}
		return s;
	}
	
	public ArrayList<String> toTestArrayString()
	{
		ArrayList<String> lines = new ArrayList<String>();
		for(Reaction r:rs)
		{
			String c1 = r.getSubtrateId();
			String c2 = r.getProductId();
			String rname = r.getReactionId();
			lines.add(rname+" "+c1+" -> "+c2);
		}
		return lines;
	}
}
