package entity;
import java.util.ArrayList;
import org.ujmp.core.Matrix;
import org.ujmp.core.matrix.DenseMatrix;
import java.util.HashMap;
public class BranchTree {
	public String name = "";
	public ArrayList<Point> points = new ArrayList<Point>();
	public ArrayList<Path> paths = new ArrayList<Path>();
	public Matrix adjMatrix = null;
	public ArrayList<String> comps = null;
	public HashMap<String,String> relationMap = null;
	public HashMap<String,Point> pointMap = null;
	public ArrayList<Reaction> rlist = null;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Point> getPoints() {
		return points;
	}
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}
	public ArrayList<Path> getPaths() {
		return paths;
	}
	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}
	public Matrix getAdjMatrix()
	{
		comps = new ArrayList<String>();
		relationMap = new HashMap<String,String>();
		rlist = new ArrayList<Reaction>();
		pointMap = new HashMap<String,Point>();
		String start = paths.get(0).getRs().get(0).getSubtrateId();
		String end = paths.get(0).getRs().get(paths.get(0).getRs().size()-1).getProductId();
		for(Path p:paths)
		{
			for(Reaction r:p.getRs())
			{
				String c1 = r.getSubtrateId();
				String c2 = r.getProductId();
				if(!comps.contains(c1))
					comps.add(c1);
				if(!comps.contains(c2))
					comps.add(c2);
				
				if(pointMap.get(c1)!=null)
				{
					Point po = pointMap.get(c1);
					if(!po.getBehind().contains(r))
					po.getBehind().add(r);
				}
				else
				{
					Point po = new Point();
					po.setPointId(c1);
					po.getBehind().add(r);
					pointMap.put(c1, po);
				}
				if(pointMap.get(c2)!=null)
				{
					Point po = pointMap.get(c2);
					if(!po.getPrecursor().contains(r))
					po.getPrecursor().add(r);
				}
				else
				{
					Point po = new Point();
					po.setPointId(c2);
					po.getPrecursor().add(r);
					pointMap.put(c2, po);
				}
				relationMap.put(c1+","+c2, r.getReactionId());
				if(!rlist.contains(r))
					rlist.add(r);
			}
		}
		ArrayList<String> remove = new ArrayList<String>();
		for(String key:pointMap.keySet())
		{
			if(pointMap.get(key).getBehind().size()>1||pointMap.get(key).getPrecursor().size()>1)
				continue;
			remove.add(key);
		}
		if(!remove.contains(start))
			remove.add(start);
		if(!remove.contains(end))
			remove.add(end);
		for(String key:remove)
		{
			pointMap.remove(key);
		}
		ArrayList<Point> add = new ArrayList<Point>();
		for(String key:pointMap.keySet())
		{
			for(Point p:points)
			{
				if(p.getPointId().equals(key))
					continue;
				add.add(p);
			}
		}
		points.addAll(add);	
		for(Point p:points)
		{
			ArrayList<Reaction> pre = new ArrayList<Reaction>();
			ArrayList<Reaction> behind = new ArrayList<Reaction>();
			for(Reaction r:rlist)
			{
				if(p.getPrecursor().contains(r))
					pre.add(r);
				if(p.getBehind().contains(r))
					behind.add(r);
			}
			p.setBehind(behind);
			p.setPrecursor(pre);
		}
		adjMatrix = DenseMatrix.factory.zeros(comps.size(),comps.size());
		for(String key:relationMap.keySet())
		{
			String c1 = key.split(",")[0];
			String c2 = key.split(",")[1];
			adjMatrix.setAsInt(1,comps.indexOf(c1),comps.indexOf(c2));
			adjMatrix.setAsInt(1,comps.indexOf(c2),comps.indexOf(c1));
		}
		return adjMatrix;
	}
}
