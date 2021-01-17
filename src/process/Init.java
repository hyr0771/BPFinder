package process;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import model.abstracts.BaseVertex;
import entity.Path;
import entity.Reaction;

public class Init {
	public String reactionPath = "data/c1-c2-03.txt";
	public String atomPath = "/data/reaction-atom.txt";
	double a = 0.1;
	
	public static String transferName2Num(String name)
	{
		String s ="";
		String temp[] = name.split("C");
		if(name.length()!=6||temp[0].length() == name.length())
			return null;
		s = Integer.parseInt(temp[1])+"";
		return s;
	}
	
	public static double calculateWeight(double similarity,double freeEnergy,double a)
	{
		double weight = 1.0;
		if(freeEnergy==2300)
			return 1-similarity;
		weight = a*(1-similarity)+(1-a)*(3200+freeEnergy)/10000;
		return weight;
	}
	
	public HashMap<String,String> getAtomInfo()
	{
		HashMap<String , String> map = new HashMap<String, String>();
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(atomPath)));
			String line = "";
			while((line = bf.readLine())!=null)
			{
					String[] temp = line.split(" +");
					line = line.replaceFirst(temp[0], "").trim();
					map.put(temp[0], line);
			}
			bf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public static String transferNum2Name(String Num)
	{
		String s = "";
		int nLength = Num.length();
		switch(nLength)
		{
		case 1:
			s = "C0000"+Num;
			break;
		case 2:
			s = "C000"+Num;
			break;
		case 3:
			s = "C00"+Num;
			break;
		case 4:
			s = "C0"+Num;
			break;
		case 5:
			s = "C"+Num;
			break;
		}
		return s;
	}
	
	public HashMap<String, ArrayList<Reaction>> getReactionMap(HashMap<String, String> atomMap,int startEdgeNum,String host, String port,
			String dbName, String user, String password)
	{
		long startTime = System.currentTimeMillis();   
		String sql = "select distinct Reaction,Right_Left,C_1,C_2,C_FreeEnergy,C_Similarity,C_1_Name,C_2_Name from r_c_2";
		DBUtils dbUtils = new DBUtils(user, password, host, port, dbName);
		HashMap<String, ArrayList<Reaction>> map = new HashMap<String, ArrayList<Reaction>>();
		ResultSet resultSet = dbUtils.excuteSql(sql);
		int count = 0;
		try {
			while (resultSet.next()) {
				String c_1 = resultSet.getString("C_1");
				String c_2 = resultSet.getString("C_2");
				String reaction = resultSet.getString("Reaction");
				String freeEnergy = resultSet.getString("C_FreeEnergy");
				String similarity = resultSet.getString("C_Similarity");
				String right_left = resultSet.getString("Right_Left");
				if(freeEnergy==null)
					freeEnergy = "2300";
				String c_1name = resultSet.getString("C_1_Name");
				String c_2name = resultSet.getString("C_2_Name");
				if(Integer.parseInt(right_left)==2)
				{
					String temp = c_2;
					c_2 = c_1;
					c_1 = temp;
				}
				String key = c_1+","+c_2;
				String atomInfo = atomMap.get(key+","+reaction);
				double weight = calculateWeight(Double.parseDouble(similarity),Double.parseDouble(freeEnergy),a);
				Reaction r = new Reaction(reaction, c_1, c_2, c_1name, c_2name, Double.parseDouble(similarity), Double.parseDouble(freeEnergy), atomInfo, new ArrayList<Integer>(Collections.nCopies(startEdgeNum, 0)),weight);
				ArrayList<Reaction> rlist = map.get(key);
				if(rlist==null)
				{
					rlist = new ArrayList<Reaction>();
					rlist.add(r);
					map.put(key, rlist);
				}
				else
				{
					rlist.add(r);
				}
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis(); 
		return map;
	}	
	
	public HashMap<String, ArrayList<Reaction>> getReactionMap(HashMap<String, String> atomMap,int startEdgeNum)
	{
		long startTime = System.currentTimeMillis(); 
		String sql = "select distinct Reaction,Right_Left,C_1,C_2,C_FreeEnergy,C_Similarity,C_1_Name,C_2_Name from r_c_2";
		DBUtils dbUtils = new DBUtils();
		HashMap<String, ArrayList<Reaction>> map = new HashMap<String, ArrayList<Reaction>>();
		ResultSet resultSet = dbUtils.excuteSql(sql);
		int count = 0;
		try {
			while (resultSet.next()) {
				String c_1 = resultSet.getString("C_1");
				String c_2 = resultSet.getString("C_2");
				String reaction = resultSet.getString("Reaction");
				String freeEnergy = resultSet.getString("C_FreeEnergy");
				String similarity = resultSet.getString("C_Similarity");
				String right_left = resultSet.getString("Right_Left");
				if(freeEnergy==null)
					freeEnergy = "2300";
				String c_1name = resultSet.getString("C_1_Name");
				String c_2name = resultSet.getString("C_2_Name");
				if(Integer.parseInt(right_left)==2)
				{
					String temp = c_2;
					c_2 = c_1;
					c_1 = temp;
				}
				String key = c_1+","+c_2;
				String atomInfo = atomMap.get(key+","+reaction);
				double weight = calculateWeight(Double.parseDouble(similarity),Double.parseDouble(freeEnergy),a);
				Reaction r = new Reaction(reaction, c_1, c_2, c_1name, c_2name, Double.parseDouble(similarity), Double.parseDouble(freeEnergy), atomInfo, new ArrayList<Integer>(Collections.nCopies(startEdgeNum, 0)),weight);
				ArrayList<Reaction> rlist = map.get(key);
				if(rlist==null)
				{
					rlist = new ArrayList<Reaction>();
					rlist.add(r);
					map.put(key, rlist);
				}
				else
				{
					rlist.add(r);
				}
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis(); 
		return map;
	}	
	
	public  Reaction selectReactinWithBig(ArrayList<Reaction> rlist)
	{
		if(rlist.size()==1)
			return rlist.get(0);
		Reaction max = rlist.get(0);
		for(int i=1;i<rlist.size();i++)
		{
			Reaction temp = rlist.get(i);
			if(temp.getWeight()>max.getWeight())
				max = temp;
		}
		return max;
	}
	
	public Path changePath(model.Path p,HashMap<String, ArrayList<Reaction>> rmap,int j)
	{
		List<BaseVertex> vlist = p.get_vertices();
		Path path = new Path();
		path.setName(j+"");
		for(int i =0;i<vlist.size();i++)
		{
			if(i+1!=vlist.size())
			{
				String c_1 = vlist.get(i)+"";
				String c_2 = vlist.get(i+1)+"";
				c_1 = transferNum2Name(c_1);
				c_2 = transferNum2Name(c_2);
				String key = c_1+","+c_2;
				Reaction r = selectReactinWithBig(rmap.get(key));
				path.getRs().add(r);
			}
		}
		return path;
	}
	
}
