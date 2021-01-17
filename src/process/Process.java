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
 * ���̴�����
 * 
 * @author ys
 * 
 */
public class Process {
	/**
	 * �����������ҵ���·����û�о����κδ���
	 */
	public ArrayList<Path> originalPath = null;
	/**
	 * ����ԭ���Ŵ����·��
	 */
	public ArrayList<Path> atomPath = null;
	/**
	 * �������ݿ�Ĺ�����
	 */
	public DBUtils dbUtils = null;
	/**
	 * ���ִ�����
	 */
	public FileUtils fileUtils = null;
	/**
	 * ԭ���Ŵ�����
	 */
	public TreeTools treeTools = null;
	/**
	 * ���޽ӵ㼯��
	 */
	public HashMap<String, Point> pointMap = null;
	/**
	 * ��������ֱ�
	 */
	public HashMap<String, CAppearNode> appearMap = null;
	/**
	 * ��ʼ������
	 */
	public String start = "C01953";
	/**
	 * Ŀ�껯����
	 */
	public String end = "C05498";
	/**
	 * ���޽ӵ㼯��
	 */
	public ArrayList<Point> waitPoints = null;
	/**
	 * �ϸ�ļ޽ӵ㼯��
	 */
	public ArrayList<Point> points = null;
	/**
	 * ���ڴ�ż޽ӵ��ԭ�������
	 */
	ArrayList<ArrayList<CombTreeNode>> resultSet = null;
	/**
	 * ���ڴ洢���м޽ӵ���ֵĿ��ܣ������еõ�3���޽ӵ�PA��PB��PC����ô������{PA,Pb,Pc,Pa Pb,Pa Pc,....}
	 */
	ArrayList<ArrayList<Point>> resultPoint = null;
	/**
	 * ��ŷ�֧��
	 */
	ArrayList<BranchTree> branchTrees = null;
	/**
	 * Ҫ�ȶԵ���Ȼ·������
	 */
	String rpwName = "rn00010";
	/**
	 * ѡ����Ѱ·����k
	 */
	int k = 2000;// 2000ʱ������
	/**
	 * ������м޽ӵ�������������12�������ջGG
	 */
	int maxPointsSize = 8;
	/**
	 * ����ཻԭ���Ÿ���������10���������ǰ�������GG
	 */
	int maxCombSize = 12;
	/**
	 * �������ԭ���ŵ�����ཻ���
	 */
	int maxCombination = 10000;
	/**
	 * ��Сԭ����ת�Ʊ�
	 */
	int minAtomGroupTransfer = 3;
	/**
	 * ��С·������
	 */
	int minPathLength = 2;
	/**
	 * ���·������
	 */
	int maxPathLength = 15;
	/**
	 * �Ƿ�ȷö�����з�֧������ʱ�Ȳ��õĴ� ��2^�޽ӵ������ ��
	 */
	boolean ifPreciseFindBranch = true;
	/**
	 * ���txt���������ĵ�·��
	 */
	String saveTxtPath = "D:\\" + start + "_" + end + "\\";
	/**
	 * ���ڴ��ͼƬ��������·��
	 */
	String savePicPath = "F:\\";
	/**
	 * ��Сԭ����ת����
	 */
	double minAtomTransfRate = 0;
	/**
	 * ��Ȼ·������
	 */
	int natrueSize = 0;
	/**
	 * �Ƿ�չʾ��֧��
	 */
	boolean ifBranch = true;
	/**
	 * �Ƿ��ͼ
	 */
	boolean ifDraw = true;

	/**
	 * ��ͼ��������ڰ�װλ��
	 */
	String graPhVizPath = "D:\\graphviz\\bin\\dot.exe";
	/**
	 * �Ƿ񱣴�
	 */
	boolean ifSave = true;
	boolean ifInit = false;

	/**
	 * �ж��㷨��ͨ����ԭ���Ų��ཻ��������ԭ�����ཻ�����ཻΪfalse
	 */
	boolean ifInterSet = false;

	/**
	 * �Ƿ�����·���г��������л��
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
		String parameter = "��ʼ�����" + start;
		parameter = parameter + "\r\n";
		parameter = parameter + "Ŀ�껯���" + end;
		parameter = parameter + "\r\n";
		parameter = parameter + "����kֵΪ��" + k;
		parameter = parameter + "\r\n";
		parameter = parameter + "������Сת��ԭ����Ϊ��" + minAtomGroupTransfer;
		parameter = parameter + "\r\n";
		parameter = parameter + "������С·������" + minPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "�������·������" + maxPathLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "����������м޽ӵ������" + maxPointsSize;
		parameter = parameter + "\r\n";
		parameter = parameter + "��������ԭ���ŵ�����ཻ��ϣ�" + maxCombination;
		parameter = parameter + "\r\n";
		parameter = parameter + "�����޽ӵ������ཻԭ���Ÿ�����" + maxCombSize;
		parameter = parameter + "\r\n";
		parameter = parameter + "�Ƿ�ȷ���ң�" + ifPreciseFindBranch;
		parameter = parameter + "\r\n";
		parameter = parameter + "aLength��" + aLength;
		parameter = parameter + "\r\n";
		parameter = parameter + "aFreeEnergy��" + aFreeEnergy;
		parameter = parameter + "\r\n";
		parameter = parameter + "aTransf��" + aTransf;
		parameter = parameter + "\r\n";
		parameter = parameter + "hub��" + hub;
		parameter = parameter + "\r\n";
		parameter = parameter + "aPoints��" + aPoints;
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
			String parameter = "start��" + start;
			parameter = parameter + "\r\n";
			parameter = parameter + "end��" + end;
			parameter = parameter + "\r\n";
			parameter = parameter + "k��" + k;
			parameter = parameter + "\r\n";
			parameter = parameter + "minAtomGroupTransfer��"
					+ minAtomGroupTransfer;
			parameter = parameter + "\r\n";
			parameter = parameter + "minPathLength��" + minPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "maxPathLength��" + maxPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifPreciseFindBranch��"
					+ ifPreciseFindBranch;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifInterSet��" + ifInterSet;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifAbundant��" + ifAbundant;
			parameter = parameter + "\r\n";
			parameter = parameter + "aLength��" + aLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "aFreeEnergy��" + aFreeEnergy;
			parameter = parameter + "\r\n";
			parameter = parameter + "aTransf��" + aTransf;
			parameter = parameter + "\r\n";
			parameter = parameter + "hub��" + hub;
			parameter = parameter + "\r\n";
			parameter = parameter + "aPoints��" + aPoints;
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
