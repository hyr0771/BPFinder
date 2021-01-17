package process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import org.ujmp.core.Matrix;
import org.ujmp.core.matrix.DenseMatrix;
import entity.CAppearNode;
import entity.Compound;
import entity.Path;
import entity.Point;
import entity.Reaction;

public class FileUtils {
	public static String MolFilePath = "/mol/";
	public static int AtomInfoStart = 3;
	public enum MatrixType{Adjecent,Transfer,EdgeIdentify} ;
	public static enum CompoundType{substrate,product};
	
    public static String readFileByLines(String fileName) {  
        File file = new File(fileName);  
        if(!file.exists())
		{
			return null;
		}
        BufferedReader reader = null;  
        String s = "";
        try {  
            reader = new BufferedReader(new FileReader(file));  
            String tempString = null;  
            while ((tempString = reader.readLine()) != null) {  
            	s =s+tempString+"\n";
            }  
            reader.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (reader != null) {  
                try {  
                    reader.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
        return s;
    }  
    
    public String readFileByLinesOnClass(String fileName)
    {
    	 InputStream in = this.getClass().getResourceAsStream(fileName);
    	 if(in == null)
    	 {
    		 return null;
    	 }
         BufferedReader reader = new BufferedReader(new InputStreamReader(in));
         String s = "";
         try {  
             String tempString = null;  
             while ((tempString = reader.readLine()) != null) {  
             	s =s+tempString+"\n";
             }  
             reader.close();  
         } catch (IOException e) {  
             e.printStackTrace();  
         } finally {  
             if (reader != null) {  
                 try {  
                     reader.close();  
                 } catch (IOException e1) {  
                 }  
             }  
         }  
         return s;
    }
    
    public static Matrix getAdjMatrix(String edgeInfo,int pointsLA,int pointsLB,MatrixType type,Compound c)
    {
    	if(edgeInfo==null)
    		System.err.println(c.CompoundID);
    	String[] edge = edgeInfo.split(",");
    	Matrix matrix = DenseMatrix.factory.zeros(pointsLA,pointsLB);
    	for(int i = 0; i < edge.length;i++)
    	{
    		String[] point = edge[i].split(" ");
    		int row = Integer.parseInt(point[0])-1;
    		int col = Integer.parseInt(point[1])-1;
    		if(row>200||col>200)
				System.err.println("wrong"+c.getCompoundID());
    		switch(type){
    			case Adjecent:
    				matrix.setAsInt(1, row,col);
            		matrix.setAsInt(1, col,row);
    				break;
    			case Transfer:
    				matrix.setAsInt(1, col+1,row+1);
    				break;
    			case EdgeIdentify:
    				matrix.setAsInt(i+1, row,col);
    	    		matrix.setAsInt(i+1, col,row);
    				break;
    		}
    	}
    	return matrix;
    }
   
    public static void addIfPoint(String Cname,CompoundType type,Path p,Reaction r,HashMap<String, Point> pointMap,HashMap<String,CAppearNode> appearMap)
    {
    	if(pointMap.get(Cname)!=null)
    	{
    		Point temp = pointMap.get(Cname);
    		if(!temp.getPaths().contains(p))
    		temp.getPaths().add(p);
    		switch(type)
    		{
			case substrate:
				if(!temp.getBehind().contains(r))
				temp.getBehind().add(r);
				break;
			case product:
				if(!temp.getPrecursor().contains(r))
				temp.getPrecursor().add(r);
				break;
    		}
    	}
    	else if(appearMap.get(Cname)!=null)
    	{
    		CAppearNode temp = appearMap.get(Cname);
    		temp.setAppearTimes(temp.getAppearTimes()+1);
    		if(!temp.getAppearPath().contains(p))
    			temp.getAppearPath().add(p);
    		switch(type)
    		{
			case substrate:
				if(!temp.getBehind().contains(r))
				temp.getBehind().add(r);
				break;
			case product:
				if(!temp.getPrecursor().contains(r))
				temp.getPrecursor().add(r);
				break;
    		}
    		if(temp.getAppearTimes()>=4)
    		{
    			Point point = new Point();
    			point.setPointId(Cname);
    			point.setPaths(temp.getAppearPath());
    			if(temp.getBehind()!=null)
    				point.setBehind(temp.getBehind());
    			if(temp.getPrecursor()!=null)
    				point.setPrecursor(temp.getPrecursor());
    			pointMap.put(Cname, point);
    		}
    	}
    	else if(appearMap.get(Cname)==null)
    	{
    		CAppearNode node = new CAppearNode();
    		node.getAppearPath().add(p);
    		node.setAppearTimes(1);
    		node.setName(Cname);
    		switch(type)
    		{
			case substrate:
				node.getBehind().add(r);
				break;
			case product:
				node.getPrecursor().add(r);
				break;
    		}
    		appearMap.put(Cname, node);
    	}
    }
    
    public  ArrayList<Point> getWaitPoints(HashMap<String, Point> map)
    {
    	ArrayList<Point> points = new ArrayList<Point>();
    	for(String key:map.keySet())
    	{
    		points.add(map.get(key));
    	}
    	return points;
    }
    
    public int getTransferEdgeCount(Matrix MA)
    {
    	int sum = 0;
    	for(int i = 0; i < MA.getRowCount(); i++){
            for(int j = 0; j <= i; j++)
            {
            	int value = MA.getAsInt(i,j);
                if(value>0)
                	{
                		sum++;
                	}
            }
        }
    	return sum;
    }
    
    public  void updateAtomReaction(Reaction reaction,Matrix MA,Path p)
    {
    	for(int i = 0; i < MA.getRowCount(); i++){
            for(int j = 0; j <= i; j++)
            {
            	int value = MA.getAsInt(i,j);
                if(value>0)
                	{
                		reaction.getEdge().set(value-1, 1);
                	}
            }
        }
    	reaction.getStart2transEdge().put(p, new ArrayList<Integer>(reaction.getEdge()));
    	reaction.getString2transEdge().put(p.getName()+"", new ArrayList<Integer>(reaction.getEdge()));
    	int size = reaction.getEdge().size();
    	reaction.getEdge().clear();
    	reaction.setEdge(new ArrayList<Integer>(Collections.nCopies(size, 0)));
    }
    
    public Path handleAtom(int minGroupTransfer,Path p,HashMap<String, Point> pointMap,HashMap<String,CAppearNode> appearMap)
    {
    	ArrayList<Reaction> path = p.getRs();
    	int pathL = path.size();
    	Matrix MA =null;
    	Matrix MB = null;
    	Matrix M = null;
    	Matrix MC = null;
    	ArrayList<Matrix> tempMA = new ArrayList<Matrix>();
    	Compound c_1 =null,c_2 =null;
    	String subtrate="",product ="";
    	for(int i =0;i<pathL;i++)
    	{
    		subtrate = path.get(i).getSubtrateId();
    		product = path.get(i).getProductId();
    		c_1 = getCompondInfo(subtrate);
    		c_2 = getCompondInfo(product);
    		if(c_1==null||c_2==null)
    			return null;
    		if(!checkIfMapCorrect(path.get(i), c_1, c_2))
    		{
    			System.out.println("transfer error:"+c_1.getCompoundID()+"-"+c_2.getCompoundID()+"-"+path.get(i).toString());
    			return null;
    		}
    		if(MA == null)
    		{
    			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(),MatrixType.Adjecent,c_2);
    		}
    		else
    		{
    			MB = MA;
    			MA = getAdjMatrix(c_2.getEdgeInfo(), c_2.getAtomNum(), c_2.getAtomNum(),MatrixType.Adjecent,c_2);
    		}
    		if(MB == null)
    		{
    			MB =  getAdjMatrix(c_1.getEdgeInfo(), c_1.getAtomNum(), c_1.getAtomNum(),MatrixType.EdgeIdentify,c_1);
    		}
        	M = getAdjMatrix(path.get(i).getAtomTransfer(), c_2.getAtomNum(), c_1.getAtomNum(),MatrixType.Transfer,c_1);
        	MC = M.mtimes((M.mtimes(MB)).transpose());
        	MA = MC.times(MA);
        	tempMA.add(MA);
    	}
    	if(MA.getAbsoluteValueSum()<1)
    		return null;
    	if(getTransferEdgeCount(MA)<minGroupTransfer)
    		return null;
    	else
    	{
    		for(int i = 0; i <pathL;i++)
    		{
    			subtrate = path.get(i).getSubtrateId();
        		product = path.get(i).getProductId();
        		updateAtomReaction(p.getRs().get(i),tempMA.get(i),p);
        		if(i==0)
        		{
        			addIfPoint(product, CompoundType.product, p, p.getRs().get(i), pointMap, appearMap);
        		}
        		else if(i==pathL-1)
        		{
        			addIfPoint(subtrate, CompoundType.substrate, p, p.getRs().get(i), pointMap, appearMap);
        		}
        		else if(i!=0&&i!=pathL-1)
        		{
        			addIfPoint(subtrate, CompoundType.substrate, p, p.getRs().get(i), pointMap, appearMap);
        			addIfPoint(product, CompoundType.product, p, p.getRs().get(i), pointMap, appearMap);
        		}
    		}
    		return p;
    	}
    }
    
    public boolean checkIfMapCorrect(Reaction r,Compound C1,Compound C2)
    {
    	boolean ifCorrect = true;
    	String transfer = r.getAtomTransfer();
    	String[] trans = transfer.split(",");
    	for(String e:trans)
    	{
    		String[] atom = e.split(" +");
    		String C1_idx = (Integer.parseInt(atom[0])+1)+"";
    		String C2_idx = (Integer.parseInt(atom[1])+1)+"";
    		if(!C1.getAtomMap().get(C1_idx).equals(C2.getAtomMap().get(C2_idx)))
    			return false;
    	}
    	return ifCorrect;
    }
    
    public Compound getCompondInfo(String ID)
    {
    	Compound c = new Compound();
    	String s = readFileByLinesOnClass(MolFilePath+ID+".mol");
    	if(s==null)
    		return null;
    	String[] lines = s.split("\n");
    	int atomNum = Integer.parseInt(lines[AtomInfoStart].trim().split(" +")[0]);
    	int edgeNum = Integer.parseInt(lines[AtomInfoStart].trim().split(" +")[1]);
    	int InfoStart = AtomInfoStart+atomNum+1;
    	int InfoEnd = AtomInfoStart+atomNum+edgeNum;
    	String Info = "";
    	for(int i = InfoStart;i<=InfoEnd;i++)
    	{
    		String temp = lines[i].trim().split(" +")[0]+" "+lines[i].trim().split(" +")[1]+",";
    		Info = Info+temp;
    	}
    	if(Info.length()==0)
    	{
    		System.out.println("无法获取化合物分子信息名称："+ID);
    		return null;
    	}
    	Info = Info.substring(0, Info.length()-1);
    	int atomStart = AtomInfoStart + 1;
    	int atomEnd = atomStart + atomNum;
    	HashMap<String, String> atomMap = new HashMap<String, String>();
    	int idx = 1;
    	for (int j = atomStart; j < atomEnd; j++) {
    		atomMap.put(idx + "", lines[j].trim().split(" +")[3]);
    		idx++;
    	}
    	c.setAtomNum(atomNum);
    	c.setCompoundID(ID);
    	c.setEdgeInfo(Info);
    	c.setEdgeNum(edgeNum);
    	c.setAtomMap(atomMap);
    	return c;
    }
}
