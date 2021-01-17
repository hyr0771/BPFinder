package process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import model.Graph;
import model.VariableGraph;
import control.YenTopKShortestPathsAlg;
import entity.BranchTree;
import entity.CAppearNode;
import entity.CombTreeNode;
import entity.Compound;
import entity.DrawThread;
import entity.Path;
import entity.Point;
import entity.Reaction;

/**
 * 过程处理类
 * 
 * @author ys
 * 
 */
public class Process {
	/**
	 * 线性搜索查找到的路径，没有经过任何处理
	 */
	public ArrayList<Path> originalPath = null;
	/**
	 * 经过原子团处理的路径
	 */
	public ArrayList<Path> atomPath = null;
	/**
	 * 连接数据库的工具类
	 */
	public DBUtils dbUtils = null;
	/**
	 * 多种处理类
	 */
	public FileUtils fileUtils = null;
	/**
	 * 原子团处理类
	 */
	public TreeTools treeTools = null;
	/**
	 * 待嫁接点集合
	 */
	public HashMap<String, Point> pointMap = null;
	/**
	 * 化合物出现表
	 */
	public HashMap<String, CAppearNode> appearMap = null;
	/**
	 * 起始化合物
	 */
	public String start = "C01953";
	/**
	 * 目标化合物
	 */
	public String end = "C05498";
	/**
	 * 待嫁接点集合
	 */
	public ArrayList<Point> waitPoints = null;
	/**
	 * 合格的嫁接点集合
	 */
	public ArrayList<Point> points = null;
	/**
	 * 用于存放嫁接点的原子团组合
	 */
	ArrayList<ArrayList<CombTreeNode>> resultSet = null;
	/**
	 * 用于存储所有嫁接点出现的可能，比如有得到3个嫁接点PA，PB，PC，那么可能有{PA,Pb,Pc,Pa Pb,Pa Pc,....}
	 */
	ArrayList<ArrayList<Point>> resultPoint = null;
	/**
	 * 存放分支树
	 */
	ArrayList<BranchTree> branchTrees = null;
	/**
	 * 要比对的天然路径名称
	 */
	String rpwName = "rn00010";
	/**
	 * 选择搜寻路径数k
	 */
	int k = 2000;// 2000时有问题
	/**
	 * 最大运行嫁接点个数，如果超过12会引起堆栈GG
	 */
	int maxPointsSize = 8;
	/**
	 * 最大不相交原子团个数，超过10个组合连着前面会引起GG
	 */
	int maxCombSize = 12;
	/**
	 * 最大不所有原子团的最大不相交组合
	 */
	int maxCombination = 10000;
	/**
	 * 最小原子团转移边
	 */
	int minAtomGroupTransfer = 3;
	/**
	 * 最小路径长度
	 */
	int minPathLength = 2;
	/**
	 * 最大路径长度
	 */
	int maxPathLength = 15;
	/**
	 * 是否精确枚举所有分支树，耗时比不用的大 （2^嫁接点个数） 倍
	 */
	boolean ifPreciseFindBranch = true;
	/**
	 * 存放txt输出结果的文档路径
	 */
	String saveTxtPath = "D:\\" + start + "_" + end + "\\";
	/**
	 * 用于存放图片输出结果的路径
	 */
	String savePicPath = "F:\\";
	/**
	 * 最小原子团转换率
	 */
	double minAtomTransfRate = 0;
	/**
	 * 自然路径长度
	 */
	int natrueSize = 0;
	/**
	 * 是否展示分支树
	 */
	boolean ifBranch = true;
	/**
	 * 是否绘图
	 */
	boolean ifDraw = true;

	/**
	 * 绘图软件的所在安装位置
	 */
	String graPhVizPath = "D:\\graphviz\\bin\\dot.exe";
	/**
	 * 是否保存
	 */
	boolean ifSave = true;
	boolean ifInit = false;

	/**
	 * 判断算法是通过找原子团不相交，还是找原子团相交，不相交为false
	 */
	boolean ifInterSet = false;

	/**
	 * 是否允许路径中出现冗余代谢物
	 */
	boolean ifAbundant = false;
	
	public String YenPath = "/data/c1-c2-03.txt";

	// ----------------------database relative---------
	String host = "localhost";
	String port = "3306";
	String dbName = "fga";
	String user = "root";
	String password = "123456";
	
	//---------------------data relative--------------
	String yenPath = "";

	String[] abundant = { "C00001", "C00002", "C00003", "C00004", "C00005",
			"C00006", "C00007", "C00008", "C00009" };
	
/*	String[] abundant = { "C00001", "C00002", "C00003", "C00004", "C00005",
			"C00006", "C00007", "C00008", "C00009" ,"C00010","C00011","C00013","C00015","C00019","C00020","C00035","C00044","C00080","C00131","C01342"};*/

	//-------------------path relative--------
	/**
	 * path length
	 */
	double aLength = 0.5;
	/**
	 * sum free energy of a path
	 */
	double aFreeEnergy = 0.1;
	/**
	 * atomic group transfer
	 */
	double aTransf = 0.2;
	/**
	 * penalty of hub
	 */
	double hub = 0.2;
	//-------------------branch relative------
	double aPoints = 0.8;
	
	public HashMap<String,String> compMap = new HashMap<String, String>();
	
	public void setParameter(String start, String end, int k,
			int minAtomGroupTransfer, int minPathLength, int maxPathLength,
			boolean ifPreciseFindBranch, String saveTxtPath,
			String savePicPath, boolean ifInterset, boolean ifAbundant,
			boolean ifDraw, String graPhVizPath, String host, String port,
			String dbName, String user, String password,int zz,double aLength,double aFreeEnergy,double aTransf,double hub,double aPoints) {
		this.start = start;
		this.end = end;
		this.k = k;
		this.minAtomGroupTransfer = minAtomGroupTransfer;
		this.minPathLength = minPathLength;
		this.maxPathLength = maxPathLength;
		this.ifPreciseFindBranch = ifPreciseFindBranch;
		this.saveTxtPath = saveTxtPath;
		this.savePicPath = savePicPath;
		this.ifInterSet = ifInterset;
		this.ifAbundant = ifAbundant;
		this.ifDraw = ifDraw;
		this.graPhVizPath = graPhVizPath;
		this.host = host;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
		this.aLength =aLength;
		this.aFreeEnergy = aFreeEnergy;
		this.aTransf = aTransf;
		this.hub = hub;
		this.aPoints = aPoints;
	}
	
	public void showParameter() {
		String parameter = "起始化合物：" + start;
		parameter = parameter + "\r\n";
		parameter = parameter + "目标化合物：" + end;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立k值为：" + k;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立最小转移原子数为：" + minAtomGroupTransfer;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立最小路径数：" + minPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立最大路径数：" + maxPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立最大运行嫁接点个数：" + maxPointsSize;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立所有原子团的最大不相交组合：" + maxCombination;
		parameter = parameter + "\r\n";
		parameter = parameter + "设立嫁接点的最大不相交原子团个数：" + maxCombSize;
		parameter = parameter + "\r\n";
		parameter = parameter + "是否精确查找：" + ifPreciseFindBranch;
		parameter = parameter + "\r\n";
		parameter = parameter + "aLength：" + aLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "aFreeEnergy：" + aFreeEnergy;
		parameter = parameter + "\r\n";
		parameter = parameter + "aTransf：" + aTransf;
		parameter = parameter + "\r\n";
		parameter = parameter + "hub：" + hub;
		parameter = parameter + "\r\n";
		parameter = parameter + "aPoints：" + aPoints;
		parameter = parameter + "\r\n";
		System.out.println(parameter);
	}

	public void startBySingle() {
		if (ifInterSet)
			processInterSet();
		else
			process();
	}

	public void startByAll() {
		compMap = getCompNameMap();
		process();
		if (branchTrees.size() == 0) {
			finish();
			processInterSet();
		}
		if (branchTrees.size() == 0)
			return;
	}
	
	public void process() {
		showParameter();
		Init();
		Init init = new Init();
		HashMap<String, String> atommap = init.getAtomInfo();
		Compound c = fileUtils.getCompondInfo(start);
		if (c == null) {
			System.out.println("the start compound can't be found in database!"
					+ "-" + start);
			return;
		}
		int statNum = c.getEdgeNum();
		if (minAtomTransfRate != 0) {
			minAtomGroupTransfer = (int) Math
					.round(statNum * minAtomTransfRate);
			if (minAtomGroupTransfer < 3)
				minAtomGroupTransfer = 3;
		}
		
		if (minAtomGroupTransfer > statNum) {
			System.out
					.println("you can't choose the minAtomGroupTransfer greater than the StartCompound's edge lenghth!");
			return;
		}
		HashMap<String, ArrayList<Reaction>> rmap = init.getReactionMap(
				atommap, statNum,host, port,
				dbName,user, password);
		compMap = getCompNameMap();
		BufferedReader bf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(YenPath)));
		Graph graph = new VariableGraph(bf);
		YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(graph);
		int startidx = Integer.parseInt(Init.transferName2Num(start));
		int endidx = Integer.parseInt(Init.transferName2Num(end));
		List<model.Path> shortest_paths_list = alg.get_shortest_paths(
				graph.get_vertex(startidx), graph.get_vertex(endidx), k);
		for (int i = 0; i < shortest_paths_list.size(); i++) {
			int pathSize = shortest_paths_list.get(i).get_vertices().size();
			if (pathSize >= minPathLength && pathSize <= maxPathLength)
				originalPath.add(init.changePath(shortest_paths_list.get(i),
						rmap, i));
		}
		CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
		for (Path p : originalPath) {
			if (!ifAbundant) {
				if (collectionUtils.getIntersection1(p.getCompName1(),
						new ArrayList<String>(Arrays.asList(abundant))).size() > 0) {
					continue;
				}
			}
			Path p1 = fileUtils.handleAtom(minAtomGroupTransfer, p, pointMap,
					appearMap);
			if (p1 == null)
				continue;
			atomPath.add(p1);
		}
		waitPoints = fileUtils.getWaitPoints(pointMap);
		for (Point p : waitPoints) {
			ArrayList<Reaction> pre = p.getPrecursor();
			ArrayList<CombTreeNode> nodes = new ArrayList<CombTreeNode>();
			for (Reaction r : pre) {
				HashMap<Path, ArrayList<Integer>> edgeMap = r
						.getStart2transEdge();
				for (Path temp_p : edgeMap.keySet()) {
					CombTreeNode treeNode = new CombTreeNode();
					treeNode.setName(r.getReactionId() + "-" + temp_p.getName());
					treeNode.setEdge(edgeMap.get(temp_p));
					ArrayList<Reaction> templist = new ArrayList<Reaction>();
					templist.add(r);
					ArrayList<Path> Plist = new ArrayList<Path>();
					Plist.add(temp_p);
					treeNode.setRlist(templist);
					treeNode.setPaths(Plist);
					nodes.add(treeNode);
				}
			}
			ArrayList<CombTreeNode> nodesbehind = new ArrayList<CombTreeNode>();
			for (Reaction r : p.getBehind()) {
				HashMap<Path, ArrayList<Integer>> edgeMap = r
						.getStart2transEdge();
				for (Path temp_p : edgeMap.keySet()) {
					CombTreeNode treeNode = new CombTreeNode();
					treeNode.setName(r.getReactionId() + "-" + temp_p.getName());
					treeNode.setEdge(edgeMap.get(temp_p));
					ArrayList<Reaction> templist = new ArrayList<Reaction>();
					templist.add(r);
					ArrayList<Path> Plist = new ArrayList<Path>();
					Plist.add(temp_p);
					treeNode.setRlist(templist);
					treeNode.setPaths(Plist);
					nodesbehind.add(treeNode);
				}
			}
			nodes = treeTools.findBigComb(nodes);
			nodesbehind = treeTools.findBigComb(nodesbehind);
			if (nodes == null && nodesbehind == null)
				continue;
			if (nodes != null && nodes.size() > 0) {
				p.getCombTreeNodes().addAll(nodes);
				points.add(p);
				continue;
			}
			ArrayList<CombTreeNode> tempBehind = nodesbehind;
			if (nodesbehind != null && nodesbehind.size() > 0) {
				p.getCombTreeNodes().addAll(nodesbehind);
				points.add(p);
				nodesbehind = null;
			}
		}
		if (ifPreciseFindBranch)
			getPreciseBranchTree();
		else
			getBranchTree();
		System.out.println("branchTree size is:" + branchTrees.size());
		branchTrees = treeTools.cutBranchTree(branchTrees);
		System.out.println("cut branchTree size is:" + branchTrees.size());
		if (!ifBranch) {
			atomPath = randPaths(atomPath);
		} else {
			branchTrees = randBranchTrees(branchTrees);
		}
		if (ifDraw) {
			String para = "k-" + k + "atom-" + minAtomTransfRate + "-"
					+ minAtomGroupTransfer;
			DrawThread thread = new DrawThread(branchTrees, atomPath, start,
					end, para, savePicPath, graPhVizPath,compMap);
			thread.start();
		}
		if (ifSave) {
			ifInterSet = false;
			saveTestPath(saveTxtPath);
		}
	}
	

	public void processInterSet() {
		showParameter();
		Init();
		Init init = new Init();
		HashMap<String, String> atommap = init.getAtomInfo();
		Compound c = fileUtils.getCompondInfo(start);
		if (c == null) {
			System.out.println("the start compound can't be found in database!"
					+ "-" + start);
			return;
		}
		int statNum = c.getEdgeNum();
		if (minAtomTransfRate != 0) {
			minAtomGroupTransfer = (int) Math
					.round(statNum * minAtomTransfRate);
			if (minAtomGroupTransfer < 3)
				minAtomGroupTransfer = 3;
		}
		if (minAtomGroupTransfer > statNum) {
			System.out
					.println("you can't choose the minAtomGroupTransfer greater than the StartCompound's edge lenghth!");
			return;
		}
		HashMap<String, ArrayList<Reaction>> rmap = init.getReactionMap(
				atommap, statNum,host, port,
				dbName,user, password);

		compMap = getCompNameMap();
		BufferedReader bf = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(YenPath)));
		Graph graph = new VariableGraph(bf);
		YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(graph);
		int startidx = Integer.parseInt(Init.transferName2Num(start));
		int endidx = Integer.parseInt(Init.transferName2Num(end));
		List<model.Path> shortest_paths_list = alg.get_shortest_paths(
				graph.get_vertex(startidx), graph.get_vertex(endidx), k);
		for (int i = 0; i < shortest_paths_list.size(); i++) {
			int pathSize = shortest_paths_list.get(i).get_vertices().size();
			if (pathSize >= minPathLength && pathSize <= maxPathLength)
				originalPath.add(init.changePath(shortest_paths_list.get(i),
						rmap, i));
		}
		CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
		for (Path p : originalPath) {
			if (!ifAbundant) {
				if (collectionUtils.getIntersection1(p.getCompName1(),
						new ArrayList<String>(Arrays.asList(abundant))).size() > 0) {
					continue;
				}
			}
			Path p1 = fileUtils.handleAtom(minAtomGroupTransfer, p, pointMap,
					appearMap);
			if (p1 == null)
				continue;
			atomPath.add(p1);
		}
		waitPoints = fileUtils.getWaitPoints(pointMap);
		System.out.println("atomPath:"+atomPath.size());
		System.out.println("waitPointsNum:"+waitPoints.size());
		for (Point p : waitPoints) {
			ArrayList<Reaction> pre = p.getPrecursor();
			ArrayList<CombTreeNode> nodes = new ArrayList<CombTreeNode>();
			for (Reaction r : pre) {
				HashMap<Path, ArrayList<Integer>> edgeMap = r
						.getStart2transEdge();
				for (Path temp_p : edgeMap.keySet()) {
					CombTreeNode treeNode = new CombTreeNode();
					treeNode.setName(r.getReactionId() + "-" + temp_p.getName());
					treeNode.setEdge(edgeMap.get(temp_p));
					ArrayList<Reaction> templist = new ArrayList<Reaction>();
					templist.add(r);
					ArrayList<Path> Plist = new ArrayList<Path>();
					Plist.add(temp_p);
					treeNode.setRlist(templist);
					treeNode.setPaths(Plist);
					nodes.add(treeNode);
				}
			}
			ArrayList<CombTreeNode> nodesbehind = new ArrayList<CombTreeNode>();
			for (Reaction r : p.getBehind()) {
				HashMap<Path, ArrayList<Integer>> edgeMap = r
						.getStart2transEdge();
				for (Path temp_p : edgeMap.keySet()) {
					CombTreeNode treeNode = new CombTreeNode();
					treeNode.setName(r.getReactionId() + "-" + temp_p.getName());
					treeNode.setEdge(edgeMap.get(temp_p));
					ArrayList<Reaction> templist = new ArrayList<Reaction>();
					templist.add(r);
					ArrayList<Path> Plist = new ArrayList<Path>();
					Plist.add(temp_p);
					treeNode.setRlist(templist);
					treeNode.setPaths(Plist);
					nodesbehind.add(treeNode);
				}
			}			 
			nodes = treeTools.findBigCombWithIntersect(nodes);
			nodesbehind = treeTools.findBigCombWithIntersect(nodesbehind);
			if (nodes == null && nodesbehind == null)
				continue;
			if (nodes != null && nodes.size() > 0) {
				p.getCombTreeNodes().addAll(nodes);
				points.add(p);
				continue;
			}
			ArrayList<CombTreeNode> tempBehind = nodesbehind;
			if (nodesbehind != null && nodesbehind.size() > 0) {
				p.getCombTreeNodes().addAll(nodesbehind);
				points.add(p);
				nodesbehind = null;
			}
		}
		 System.out.println(points.size());
		if (ifPreciseFindBranch)
			getPreciseBranchTreeWithInterSect();
		else
			getBranchTreeWithInterSect();
		System.out.println("branchTree size is:" + branchTrees.size());
		branchTrees = treeTools.cutBranchTree(branchTrees);
		System.out.println("cut branchTree size is:" + branchTrees.size());
		if (!ifBranch) {
			atomPath = randPaths(atomPath);
		} else {
			branchTrees = randBranchTrees(branchTrees);
		}
		if (ifDraw) {
			String para = "k-" + k + "atom-" + minAtomTransfRate + "-"
					+ minAtomGroupTransfer;
			DrawThread thread = new DrawThread(branchTrees, atomPath, start,
					end, para, savePicPath, graPhVizPath,compMap);
			thread.start();
		}
		if (ifSave) {
			ifInterSet = true;
			saveTestPath(saveTxtPath);
		}
	}

	public void getPreciseBranchTree() {
		if (points.size() > maxPointsSize) {
			points = selectPoints(points);
		}
		ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
		ArrayList<Point> tempPoint = new ArrayList<Point>();
		treeTools.multyPoints(points, tempPoint, 0, resultPoint);
		for (ArrayList<Point> points : resultPoint) {
			ArrayList<ArrayList<CombTreeNode>> resultSet1 = new ArrayList<ArrayList<CombTreeNode>>();
			treeTools.multiWay(points, tempWay, 0, resultSet1);
			for (ArrayList<CombTreeNode> way : resultSet1) {
				BranchTree tree = treeTools.solveConflict(way, points,
						waitPoints);
				if (tree != null)
					branchTrees.add(tree);
			}
		}
	}

	public void getPreciseBranchTreeWithInterSect() {
		if (points.size() > maxPointsSize) {
			points = selectPoints(points);
		}
		ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
		ArrayList<Point> tempPoint = new ArrayList<Point>();
		treeTools.multyPoints(points, tempPoint, 0, resultPoint);
		for (ArrayList<Point> points : resultPoint) {
			ArrayList<ArrayList<CombTreeNode>> resultSet1 = new ArrayList<ArrayList<CombTreeNode>>();
			treeTools.multiWay(points, tempWay, 0, resultSet1);
			for (ArrayList<CombTreeNode> way : resultSet1) {
				BranchTree tree = treeTools.solveConflictWithIntersect(way,
						points, waitPoints);
				if (tree != null)
					branchTrees.add(tree);
			}
		}
	}

	public void getBranchTree() {
		if (points.size() < maxPointsSize) {
			points = selectPoints(points);
			ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
			treeTools.multiWay(points, tempWay, 0, resultSet);
		} else {
			points = selectPoints(points);
			ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
			treeTools.multiWay(points, tempWay, 0, resultSet);
		}
		for (ArrayList<CombTreeNode> way : resultSet) {
			BranchTree tree = treeTools.solveConflict(way, points, waitPoints);
			if (tree != null)
				branchTrees.add(tree);
		}
	}

	public void getBranchTreeWithInterSect() {
		if (points.size() < maxPointsSize) {
			points = selectPoints(points);
			ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
			treeTools.multiWay(points, tempWay, 0, resultSet);
		} else {
			points = selectPoints(points);
			ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
			treeTools.multiWay(points, tempWay, 0, resultSet);
		}
		for (ArrayList<CombTreeNode> way : resultSet) {
			BranchTree tree = treeTools.solveConflictWithIntersect(way, points,
					waitPoints);
			if (tree != null)
				branchTrees.add(tree);
		}
	}

	public void saveTestPath(String filePath) {
		if (branchTrees.size() == 0)
			return;
		File file = new File(filePath);
		filePath = filePath + start + "_" + end + ".txt";
		try {
			if (!file.exists() && !file.isDirectory()) {
				file.mkdir();
			}
			FileWriter fwriter = new FileWriter(filePath, true);
			String parameter = "start：" + start;
			parameter = parameter + "\r\n";
			parameter = parameter + "end：" + end;
			parameter = parameter + "\r\n";
			parameter = parameter + "k：" + k;
			parameter = parameter + "\r\n";
			parameter = parameter + "minAtomGroupTransfer："
					+ minAtomGroupTransfer;
			parameter = parameter + "\r\n";
			parameter = parameter + "minPathLength：" + minPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "maxPathLength：" + maxPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifPreciseFindBranch："
					+ ifPreciseFindBranch;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifInterSet：" + ifInterSet;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifAbundant：" + ifAbundant;
			parameter = parameter + "\r\n";
			parameter = parameter + "aLength：" + aLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "aFreeEnergy：" + aFreeEnergy;
			parameter = parameter + "\r\n";
			parameter = parameter + "aTransf：" + aTransf;
			parameter = parameter + "\r\n";
			parameter = parameter + "hub：" + hub;
			parameter = parameter + "\r\n";
			parameter = parameter + "aPoints：" + aPoints;
			parameter = parameter + "\r\n";
			fwriter.write(parameter);
			CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
			for (int i = 0; i < branchTrees.size(); i++) {
				BranchTree tree = branchTrees.get(i);
				ArrayList<String> lines = new ArrayList<String>();

				for (Path p : tree.getPaths()) {
					lines = collectionUtils.getUnion1(p.toTestArrayString(),
							lines);
				}
				String s = start + "," + end + "," + lines.size();
				s = s + "\r\n";
				for (String line : lines) {
					s = s + line;
					s = s + "\r\n";
				}
				fwriter.write(s);
			}
			fwriter.flush();
			fwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Point> selectPoints(ArrayList<Point> points) {
		ArrayList<Point> bigPoints = new ArrayList<Point>();
		Collections.sort(points, new Comparator<Point>() {

			public int compare(Point o1, Point o2) {
				return o2.getCombTreeNodes().size()
						- (o1.getCombTreeNodes().size());
			}
		});
		int k = 0;
		int sum = 1;
		for (Point p : points) {
			sum = sum * p.getCombTreeNodes().size();
			System.out.println(p.getCombTreeNodes().size());
			if (sum < maxCombination && k < maxPointsSize)
				bigPoints.add(p);
			else {
				break;
			}
			k++;
		}
		return bigPoints;
	}

	public void finish() {
		ifInit = true;
		originalPath = null;
		atomPath = null;
		dbUtils = null;
		fileUtils = null;
		treeTools = null;
		pointMap = null;
		appearMap = null;
		waitPoints = null;
		points = null;
		resultPoint = null;
		resultSet = null;
		branchTrees = null;
	}

	public void Init() {
		ifInit = false;
		originalPath = new ArrayList<Path>();
		atomPath = new ArrayList<Path>();
		dbUtils = new DBUtils();
		fileUtils = new FileUtils();
		treeTools = new TreeTools();
		pointMap = new HashMap<String, Point>();
		appearMap = appearMap = new HashMap<String, CAppearNode>();
		waitPoints = new ArrayList<Point>();
		points = new ArrayList<Point>();
		resultPoint = new ArrayList<ArrayList<Point>>();
		resultSet = new ArrayList<ArrayList<CombTreeNode>>();
		branchTrees = new ArrayList<BranchTree>();
	}

	public ArrayList<Path> randPaths(ArrayList<Path> atomPath) {
		Collections.sort(atomPath, new Comparator<Path>() {

			public int compare(Path o1, Path o2) {
				return (int) (calPathWeight(o2) - calPathWeight(o1));
			}
		});
		return new ArrayList<Path>(atomPath.subList(0, 10));
	}

	public ArrayList<BranchTree> randBranchTrees(
			ArrayList<BranchTree> branchTrees) {
		Collections.sort(branchTrees, new Comparator<BranchTree>() {

			public int compare(BranchTree o1, BranchTree o2) {
				return (int) (calTreeWeight(o2) - calTreeWeight(o1));
			}
		});
		return branchTrees;
	}

	public double calPathWeight(Path p) {

		ArrayList<String> comps = p.getCompName();
		for (String c : abundant) {
			if (comps.contains(c)) {
				hub = 0.2;
				break;
			} else
				hub = 0;
		}
		int length = p.getRs().size();
		ArrayList<Integer> transfEdge = p.getRs().get(length - 1)
				.getStart2transEdge().get(p);
		double size = Math.abs(natrueSize - length) / (natrueSize + length);
		double transSize = 0;
		for (int i : transfEdge) {
			if (i > 0)
				transSize++;
		}
		transSize = transSize / transfEdge.size();
		double sumfreeEnergy = 0;
		for (Reaction r : p.getRs()) {
			sumfreeEnergy = sumfreeEnergy + (3200 + r.getFreeEnergy()) / 10000;
		}
		sumfreeEnergy = sumfreeEnergy / length;
		double weight = 0;
		weight = aLength * -size - sumfreeEnergy * aFreeEnergy + transSize
				* aTransf - hub;
		p.setWeight(weight);
		return weight;
	}

	public double calTreeWeight(BranchTree tree) {		
		double weight = 0;
		int psize = 0;
		for (Path p : tree.getPaths()) {
			weight = weight + calPathWeight(p);
			psize++;
		}
		aPoints = aPoints / maxPointsSize;
		weight = weight / psize;
		weight = aPoints * tree.getPoints().size() + (1 - aPoints) * weight;
		return weight;
	}
	
	public HashMap<String, String> getCompNameMap() {
		System.out.println("come in ");
		DBUtils dbUtils = new DBUtils();
		String sql = "select * from cmpname where FirstName ='1';";
		ResultSet resultSet = dbUtils.excuteSql(sql);
		HashMap<String, String> compMap = new HashMap<String, String>();
		try {
			while (resultSet.next()) {
				String s = "";
				String ID = resultSet.getString("KEGGCompoundID");
				String CName = resultSet.getString("CompoundName");
				compMap.put(ID,CName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return compMap;
	}
}
