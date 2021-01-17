package entity;
import java.util.ArrayList;
import java.util.HashMap;
import pic.GraphViz;
public class DrawThread extends Thread {
	ArrayList<BranchTree> branchTrees;
	ArrayList<Path> atomPath;
	boolean ifBranch = true;
	private String savePicPath = "";
	public String graPhVizPath = "D:\\graphviz\\bin\\dot.exe";
	private HashMap<String, ArrayList<String>> trees = new HashMap<String, ArrayList<String>>();
	private HashMap<String,String> compMap = new HashMap<String, String>();
	private String end = "";
	
	public DrawThread(ArrayList<BranchTree> branchTrees,
			ArrayList<Path> atomPath,String start,String end,String parameter,String savePicPath,String graPhVizPath,HashMap<String,String> compMap) {
		this.branchTrees = branchTrees;
		this.atomPath = atomPath;
		this.savePicPath = savePicPath+start+"_"+end+"\\"+parameter+"\\";
		this.graPhVizPath = graPhVizPath;
		this.compMap = compMap;
		this.end = end;
		drawPic();
	}
	
	public void run() {
		System.err.println("run");
		System.err.println(trees.size());
		try {
			synchronized (this) {
				for (String key : trees.keySet()) {					
					GraphViz gViz = new GraphViz(savePicPath,
							graPhVizPath);
					gViz.setParameter(key);
					gViz.setDotCodeFile(key);
					gViz.start_graph();
					for (String line : trees.get(key)) {
						gViz.addln(line);
					}
					gViz.end_graph();
					gViz.run();
					wait(500);
				}
			}
		} catch (Exception e) {
			System.err.println(e.toString());
			finish();
		}
		finish();
	}

	public void finish()
	{
		branchTrees = null;
		atomPath = null;
		trees = null;
	}
	
	public void drawPic() {
		int i = 0;
		for (BranchTree tree : branchTrees) {
			ArrayList<Path> paths = tree.getPaths();
			ArrayList<String> edges = new ArrayList<String>();
			ArrayList<String> nodes = new ArrayList<String>();
			for (Path p : paths) {
				for (Reaction r : p.getRs()) {
					String substrate = r.getSubtrateId();
					String product = r.getProductId();
					String s = "";
					 for(Integer num:r.getStart2transEdge().get(p))
					 { s=s+num; }
					String line = substrate + "->" + product + "[label="
							+ r.getReactionId() +"e"+ s +"];";
					if (edges.contains(line))
						continue;
					edges.add(line);
					if(!nodes.contains(substrate))
						nodes.add(substrate);
				}
				for(String n:nodes)
				{
					if(n.length()<3)
						continue;
					String l = n+"[label=\""+    compMap.get(n)+    "\"];";
					edges.add(l);
				}
				String t = end+"[label=\""+    compMap.get(end)+    "\"];";
				edges.add(t);
			}
			trees.put(i + "", edges);
			i++;
		}
		if (!ifBranch) {
			if (atomPath.size() == 0)
				return;
			for (int j = 0; j < atomPath.size(); j++) {
				if(j==5)
					break;
				ArrayList<String> edges = new ArrayList<String>();
				Path p = atomPath.get(j);
				if (p == null)
					break;
				for (Reaction r : p.getRs()) {
					String substrate = r.getSubtrateId();
					String product = r.getProductId();
					String s = "";
					int energy = (int) r.getFreeEnergy();
					if (energy < 0) {
						energy = -energy;
						s = s + "m" + energy;
					} else {
						s = energy + "";
					}
					String line = substrate + "->" + product + "[label="
							+ r.getReactionId() +s + "];";
					System.err.println(line);
					edges.add(line);
				}
				trees.put(j+"", edges);
			}
		}
	}

	public void drawNature(String content)
	{
		ArrayList<String> lineList = new ArrayList<String>();
		ArrayList<String> nodes = new ArrayList<String>();
		String[] lines = content.split("\n");
		String start = "";
		String end ="";
		for(int i = 0 ;i <lines.length;i++)
		{
			String line = lines[i];
			System.out.println(line);
			String[] value = line.split(",");
			start = value[0];
			end = value[1];
			int num = Integer.parseInt(value[2]);
			for(int j = 1 ; j <=num;j++)
			{
				String[] w = lines[j+i].split("->");
				String r = w[0].split(" ")[0];
				String c1 = w[0].split(" ")[1];
				String c2 = w[1].trim();
				if(r.contains(","))
					r = r.replaceAll(",","a");
				String l = c1 + "->" + c2 + "[label="
						+ r  + "];";
				if (lineList.contains(line))
					continue;
				lineList.add(l);
				if(!nodes.contains(c1))
					nodes.add(c1);
			}
			i = i + num+2;
		}
		for(String n:nodes)
		{
			if(n.length()<3)
				continue;
			String l = n+"[label=\""+    compMap.get(n)+    "\"];";
			lineList.add(l);
		}
		String t = end+"[label=\""+    compMap.get(end)+    "\"];";
		lineList.add(t);
		GraphViz gViz = new GraphViz(savePicPath,
				"D:\\graphviz\\bin\\dot.exe");
		gViz.setParameter("nature");
		gViz.setDotCodeFile("nature");
		gViz.start_graph();
		for (String line : lineList){
			gViz.addln(line);
		}
		gViz.end_graph();
		gViz.run();
	}
}
