package process;
import control.YenTopKShortestPathsAlg;
import entity.BranchTree;
import entity.CAppearNode;
import entity.CombTreeNode;
import entity.Compound;
import entity.DrawThread;
import entity.Path;
import entity.Point;
import entity.Reaction;

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
import java.util.Iterator;
import java.util.List;

import model.VariableGraph;
public class Process1 {
	public ArrayList<Path> originalPath = null;


	  
	  public ArrayList<Path> atomPath = null;


	  
	  public DBUtils dbUtils = null;


	  
	  public FileUtils fileUtils = null;


	  
	  public TreeTools1 treeTools = null;


	  
	  public HashMap<String, Point> pointMap = null;


	  
	  public HashMap<String, CAppearNode> appearMap = null;


	  
	  public String start = "C01953";


	  
	  public String end = "C05498";


	  
	  public ArrayList<Point> waitPoints = null;


	  
	  public ArrayList<Point> points = null;


	  
	  ArrayList<ArrayList<CombTreeNode>> resultSet = null;


	  
	  ArrayList<ArrayList<Point>> resultPoint = null;


	  
	  ArrayList<BranchTree> branchTrees = null;


	  
	  String rpwName = "rn00010";


	  
	  int k = 2000;


	  
	  int maxPointsSize = 8;


	  
	  int maxCombSize = 12;


	  
	  int maxCombination = 10000;


	  
	  int minAtomGroupTransfer = 3;


	  
	  int minPathLength = 2;


	  
	  int maxPathLength = 15;


	  
	  boolean ifPreciseFindBranch = true;


	  
	  String saveTxtPath = "D:\\" + this.start + "_" + this.end + "\\";


	  
	  String savePicPath = "F:\\";


	  
	  double minAtomTransfRate = 0.0D;


	  
	  int natrueSize = 0;



	  
	  boolean ifBranch = true;


	  
	  boolean ifDraw = true;


	  
	  String graPhVizPath = "D:\\graphviz\\bin\\dot.exe";


	  
	  boolean ifSave = true;


	  
	  boolean ifInit = false;

	  
	  boolean ifInterSet = false;

	  
	  boolean ifAbundant = false;

	  
	  public String YenPath = "/data/c1-c2-01.txt";

	  //-----database mysql------
	  String host = "localhost";
	  String port = "3306";
	  String dbName = "fga";
	  String user = "root";
	  String password = "123456";
	  
	  
	  //String yenPath = "";
	  
	  String[] abundant = { "C00001", "C00002", "C00003", "C00004", "C00005", 
	      "C00006", "C00007", "C00008", "C00009","C00010","C00011","C00080" };
	  public HashMap<String, String> compMap = new HashMap();
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
		//-------select reaction----
		double aSim = 0.3;
	  
	  public void setParameter(String start, String end, int k,
				int minAtomGroupTransfer, int minPathLength, int maxPathLength,
				boolean ifPreciseFindBranch, String saveTxtPath,
				String savePicPath, boolean ifInterset, boolean ifAbundant,
				boolean ifDraw, String graPhVizPath, String host, String port,
				String dbName, String user, String password,int zz,double aLength,double aFreeEnergy,double aTransf,double aSim,double aPoints) {
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
			//this.hub = hub;
			this.aSim = aSim;
			this.aPoints = aPoints;
		}
		
		public void showParameter() {
			String parameter = "start:" + start;
			parameter = parameter + "\r\n";
			parameter = parameter + "target:" + end;
			parameter = parameter + "\r\n";
			parameter = parameter + "k:" + k;
			parameter = parameter + "\r\n";
			parameter = parameter + "minAtomGroupTransfer:" + minAtomGroupTransfer;
			parameter = parameter + "\r\n";
			parameter = parameter + "minPathLength:" + minPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "maxPathLength:" + maxPathLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "ifPreciseFindBranch:" + ifPreciseFindBranch;
			parameter = parameter + "\r\n";
			parameter = parameter + "aLength:" + aLength;
			parameter = parameter + "\r\n";
			parameter = parameter + "aFreeEnergy:" + aFreeEnergy;
			parameter = parameter + "\r\n";
			parameter = parameter + "aTransf:" + aTransf;
			parameter = parameter + "\r\n";
			parameter = parameter + "aSim:" + aSim;
			parameter = parameter + "\r\n";
			parameter = parameter + "aPoints:" + aPoints;
			parameter = parameter + "\r\n";
			//System.out.println(parameter);
		}
	  
	  public void startBySingle() {
	    if (this.ifInterSet) {
	      processInterSet();
	    } else {
	      process();
	    } 
	  }
	  public void startByAll() {
	    this.compMap = getCompNameMap();
	    process();
	    if (this.branchTrees.size() == 0) {
	      finish();
	      processInterSet();
	    } 
	    if (this.branchTrees.size() == 0)
	      return; 
	  }
	  
	  public void process() {
	    showParameter();
	    Init();
	    Init1 init = new Init1(aSim);
	    HashMap<String, String> atommap = init.getAtomInfo();
	    Compound c = this.fileUtils.getCompondInfo(this.start);
	    if (c == null) {
	      System.out.println("the start compound can't be found in database!-" + 
	          this.start);
	      return;
	    } 
	    int statNum = c.getEdgeNum();
	    if (this.minAtomTransfRate != 0.0D) {
	      this.minAtomGroupTransfer = 
	        (int)Math.round(statNum * this.minAtomTransfRate);
	      if (this.minAtomGroupTransfer < 3) {
	        this.minAtomGroupTransfer = 3;
	      }
	    } 
	    if (this.minAtomGroupTransfer > statNum) {
	      System.out
	        .println("you can't choose the minAtomGroupTransfer greater than the StartCompound's edge lenghth!");
	      return;
	    } 
	    HashMap<String, ArrayList<Reaction>> rmap = init.getReactionMap(
	        atommap, statNum, this.host, this.port, 
	        this.dbName, this.user, this.password);
	    this.compMap = getCompNameMap();
	    BufferedReader bf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(this.YenPath)));
	    VariableGraph variableGraph = new VariableGraph(bf);
	    YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(variableGraph);
	    int startidx = Integer.parseInt(Init.transferName2Num(this.start));
	    int endidx = Integer.parseInt(Init.transferName2Num(this.end));
	    List<model.Path> shortest_paths_list = alg.get_shortest_paths(
	        variableGraph.get_vertex(startidx), variableGraph.get_vertex(endidx), this.k);
	    for (int i = 0; i < shortest_paths_list.size(); i++) {
	      int pathSize = ((model.Path)shortest_paths_list.get(i)).get_vertices().size();
	      if (pathSize >= this.minPathLength && pathSize <= this.maxPathLength)
	        this.originalPath.add(init.changePath((model.Path)shortest_paths_list.get(i), 
	              rmap, i)); 
	    } 
	    CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
	    for (Path p : this.originalPath) {
	      if (!this.ifAbundant && 
	        collectionUtils.getIntersection1(p.getCompName1(), 
	          new ArrayList(Arrays.asList(this.abundant))).size() > 0) {
	        continue;
	      }
	      
	      Path p1 = this.fileUtils.handleAtom(this.minAtomGroupTransfer, p, this.pointMap, 
	          this.appearMap);
	      if (p1 == null)
	        continue; 
	      this.atomPath.add(p1);
	    } 
	    this.waitPoints = this.fileUtils.getWaitPoints(this.pointMap);
	    for (Point p : this.waitPoints) {
	      ArrayList<Reaction> pre = p.getPrecursor();
	      ArrayList<CombTreeNode> nodes = new ArrayList<CombTreeNode>();
	      for (Reaction r : pre) {
	        HashMap<Path, ArrayList<Integer>> edgeMap = r
	          .getStart2transEdge();
	        for (Path temp_p : edgeMap.keySet()) {
	          CombTreeNode treeNode = new CombTreeNode();
	          treeNode.setName(String.valueOf(r.getReactionId()) + "-" + temp_p.getName());
	          treeNode.setEdge((ArrayList)edgeMap.get(temp_p));
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
	          treeNode.setName(String.valueOf(r.getReactionId()) + "-" + temp_p.getName());
	          treeNode.setEdge((ArrayList)edgeMap.get(temp_p));
	          ArrayList<Reaction> templist = new ArrayList<Reaction>();
	          templist.add(r);
	          ArrayList<Path> Plist = new ArrayList<Path>();
	          Plist.add(temp_p);
	          treeNode.setRlist(templist);
	          treeNode.setPaths(Plist);
	          nodesbehind.add(treeNode);
	        } 
	      } 
	      nodes = this.treeTools.findBigComb(nodes);
	      nodesbehind = this.treeTools.findBigComb(nodesbehind);
	      if (nodes == null && nodesbehind == null)
	        continue; 
	      if (nodes != null && nodes.size() > 0) {
	        p.getCombTreeNodes().addAll(nodes);
	        this.points.add(p);
	        continue;
	      } 
	      ArrayList<CombTreeNode> tempBehind = nodesbehind;
	      if (nodesbehind != null && nodesbehind.size() > 0) {
	        p.getCombTreeNodes().addAll(nodesbehind);
	        this.points.add(p);
	        nodesbehind = null;
	      } 
	    } 
	    if (this.ifPreciseFindBranch) {
	      getPreciseBranchTree();
	    } else {
	      getBranchTree();
	    }  System.out.println("branchTree size is:" + this.branchTrees.size());
	    this.branchTrees = this.treeTools.cutBranchTree(this.branchTrees);
	    System.out.println("cut branchTree size is:" + this.branchTrees.size());
	    if (!this.ifBranch) {
	      this.atomPath = randPaths(this.atomPath);
	    } else {
	      this.branchTrees = randBranchTrees(this.branchTrees);
	    } 
	    if (this.ifDraw) {
	      String para = "k-" + this.k + "atom-" + this.minAtomTransfRate + "-" + 
	        this.minAtomGroupTransfer;
	      DrawThread thread = new DrawThread(this.branchTrees, this.atomPath, this.start, 
	          this.end, para, this.savePicPath, this.graPhVizPath, this.compMap);
	      thread.start();
	    } 
	    if (this.ifSave) {
	      this.ifInterSet = false;
	      saveTestPath(this.saveTxtPath);
	    } 
	  }

	  
	  public void processInterSet() {
	    showParameter();
	    Init();
	    Init1 init = new Init1(aSim);
	    HashMap<String, String> atommap = init.getAtomInfo();
	    Compound c = this.fileUtils.getCompondInfo(this.start);
	    if (c == null) {
	      System.out.println("the start compound can't be found in database!-" + 
	          this.start);
	      return;
	    } 
	    int statNum = c.getEdgeNum();
	    if (this.minAtomTransfRate != 0.0D) {
	      this.minAtomGroupTransfer = 
	        (int)Math.round(statNum * this.minAtomTransfRate);
	      if (this.minAtomGroupTransfer < 3)
	        this.minAtomGroupTransfer = 3; 
	    } 
	    if (this.minAtomGroupTransfer > statNum) {
	      System.out
	        .println("you can't choose the minAtomGroupTransfer greater than the StartCompound's edge lenghth!");
	      return;
	    } 
	    HashMap<String, ArrayList<Reaction>> rmap = init.getReactionMap(
	        atommap, statNum, this.host, this.port, 
	        this.dbName, this.user, this.password);
	    
	    this.compMap = getCompNameMap();
	    BufferedReader bf = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(this.YenPath)));
	    VariableGraph variableGraph = new VariableGraph(bf);
	    YenTopKShortestPathsAlg alg = new YenTopKShortestPathsAlg(variableGraph);
	    int startidx = Integer.parseInt(Init.transferName2Num(this.start));
	    int endidx = Integer.parseInt(Init.transferName2Num(this.end));
	    List<model.Path> shortest_paths_list = alg.get_shortest_paths(
	        variableGraph.get_vertex(startidx), variableGraph.get_vertex(endidx), this.k);
	    for (int i = 0; i < shortest_paths_list.size(); i++) {
	      int pathSize = ((model.Path)shortest_paths_list.get(i)).get_vertices().size();
	      if (pathSize >= this.minPathLength && pathSize <= this.maxPathLength)
	        this.originalPath.add(init.changePath((model.Path)shortest_paths_list.get(i), 
	              rmap, i)); 
	    } 
	    CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
	    for (Path p : this.originalPath) {
	      if (!this.ifAbundant && 
	        collectionUtils.getIntersection1(p.getCompName1(), 
	          new ArrayList(Arrays.asList(this.abundant))).size() > 0) {
	        continue;
	      }
	      
	      Path p1 = this.fileUtils.handleAtom(this.minAtomGroupTransfer, p, this.pointMap, 
	          this.appearMap);
	      if (p1 == null)
	        continue; 
	      this.atomPath.add(p1);
	    } 
	    this.waitPoints = this.fileUtils.getWaitPoints(this.pointMap);
	    for (Point p : this.waitPoints) {
	      ArrayList<Reaction> pre = p.getPrecursor();
	      ArrayList<CombTreeNode> nodes = new ArrayList<CombTreeNode>();
	      for (Reaction r : pre) {
	        HashMap<Path, ArrayList<Integer>> edgeMap = r
	          .getStart2transEdge();
	        for (Path temp_p : edgeMap.keySet()) {
	          CombTreeNode treeNode = new CombTreeNode();
	          treeNode.setName(String.valueOf(r.getReactionId()) + "-" + temp_p.getName());
	          treeNode.setEdge((ArrayList)edgeMap.get(temp_p));
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
	          treeNode.setName(String.valueOf(r.getReactionId()) + "-" + temp_p.getName());
	          treeNode.setEdge((ArrayList)edgeMap.get(temp_p));
	          ArrayList<Reaction> templist = new ArrayList<Reaction>();
	          templist.add(r);
	          ArrayList<Path> Plist = new ArrayList<Path>();
	          Plist.add(temp_p);
	          treeNode.setRlist(templist);
	          treeNode.setPaths(Plist);
	          nodesbehind.add(treeNode);
	        } 
	      } 
	      nodes = this.treeTools.findBigCombWithIntersect(nodes);
	      nodesbehind = this.treeTools.findBigCombWithIntersect(nodesbehind);
	      if (nodes == null && nodesbehind == null)
	        continue; 
	      if (nodes != null && nodes.size() > 0) {
	        p.getCombTreeNodes().addAll(nodes);
	        this.points.add(p);
	        continue;
	      } 
	      ArrayList<CombTreeNode> tempBehind = nodesbehind;
	      if (nodesbehind != null && nodesbehind.size() > 0) {
	        p.getCombTreeNodes().addAll(nodesbehind);
	        this.points.add(p);
	        nodesbehind = null;
	      } 
	    } 
	    System.out.println(this.points.size());
	    if (this.ifPreciseFindBranch) {
	      getPreciseBranchTreeWithInterSect();
	    } else {
	      getBranchTreeWithInterSect();
	    }  System.out.println("branchTree size is:" + this.branchTrees.size());
	    this.branchTrees = this.treeTools.cutBranchTree(this.branchTrees);
	    System.out.println("cut branchTree size is:" + this.branchTrees.size());
	    if (!this.ifBranch) {
	      this.atomPath = randPaths(this.atomPath);
	    } else {
	      this.branchTrees = randBranchTrees(this.branchTrees);
	    } 
	    if (this.ifDraw) {
	      String para = "k-" + this.k + "atom-" + this.minAtomTransfRate + "-" + 
	        this.minAtomGroupTransfer;
	      DrawThread thread = new DrawThread(this.branchTrees, this.atomPath, this.start, 
	          this.end, para, this.savePicPath, this.graPhVizPath, this.compMap);
	      thread.start();
	    } 
	    if (this.ifSave) {
	      this.ifInterSet = true;
	      saveTestPath(this.saveTxtPath);
	    } 
	  }
	  
	  public void getPreciseBranchTree() {
	    if (this.points.size() > this.maxPointsSize) {
	      this.points = selectPoints(this.points);
	    }
	    ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	    ArrayList<Point> tempPoint = new ArrayList<Point>();
	    this.treeTools.multyPoints(this.points, tempPoint, 0, this.resultPoint);
	    for (ArrayList<Point> points : this.resultPoint) {
	      ArrayList<ArrayList<CombTreeNode>> resultSet1 = new ArrayList<ArrayList<CombTreeNode>>();
	      this.treeTools.multiWay(points, tempWay, 0, resultSet1);
	      for (ArrayList<CombTreeNode> way : resultSet1) {
	        BranchTree tree = this.treeTools.solveConflict(way, points, 
	            this.waitPoints);
	        if (tree != null)
	          this.branchTrees.add(tree); 
	      } 
	    } 
	  }
	  
	  public void getPreciseBranchTreeWithInterSect() {
	    if (this.points.size() > this.maxPointsSize) {
	      this.points = selectPoints(this.points);
	    }
	    ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	    ArrayList<Point> tempPoint = new ArrayList<Point>();
	    this.treeTools.multyPoints(this.points, tempPoint, 0, this.resultPoint);
	    for (ArrayList<Point> points : this.resultPoint) {
	      ArrayList<ArrayList<CombTreeNode>> resultSet1 = new ArrayList<ArrayList<CombTreeNode>>();
	      this.treeTools.multiWay(points, tempWay, 0, resultSet1);
	      for (ArrayList<CombTreeNode> way : resultSet1) {
	        BranchTree tree = this.treeTools.solveConflictWithIntersect(way, 
	            points, this.waitPoints);
	        if (tree != null)
	          this.branchTrees.add(tree); 
	      } 
	    } 
	  }
	  
	  public void getBranchTree() {
	    if (this.points.size() < this.maxPointsSize) {
	      this.points = selectPoints(this.points);
	      ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	      this.treeTools.multiWay(this.points, tempWay, 0, this.resultSet);
	    } else {
	      this.points = selectPoints(this.points);
	      ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	      this.treeTools.multiWay(this.points, tempWay, 0, this.resultSet);
	    } 
	    for (ArrayList<CombTreeNode> way : this.resultSet) {
	      BranchTree tree = this.treeTools.solveConflict(way, this.points, this.waitPoints);
	      if (tree != null)
	        this.branchTrees.add(tree); 
	    } 
	  }
	  
	  public void getBranchTreeWithInterSect() {
	    if (this.points.size() < this.maxPointsSize) {
	      this.points = selectPoints(this.points);
	      ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	      this.treeTools.multiWay(this.points, tempWay, 0, this.resultSet);
	    } else {
	      this.points = selectPoints(this.points);
	      ArrayList<CombTreeNode> tempWay = new ArrayList<CombTreeNode>();
	      this.treeTools.multiWay(this.points, tempWay, 0, this.resultSet);
	    } 
	    for (ArrayList<CombTreeNode> way : this.resultSet) {
	      BranchTree tree = this.treeTools.solveConflictWithIntersect(way, this.points, 
	          this.waitPoints);
	      if (tree != null)
	        this.branchTrees.add(tree); 
	    } 
	  }
	  
	  public void saveTestPath(String filePath) {
	    if (this.branchTrees.size() == 0)
	      return; 
	    File file = new File(filePath);
	    filePath = String.valueOf(filePath) + this.start + "_" + this.end + ".txt";
	    try {
	      if (!file.exists() && !file.isDirectory()) {
	        file.mkdir();
	      }
	      FileWriter fwriter = new FileWriter(filePath, true);
	      String parameter = "start:" + this.start;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "end:" + this.end;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "k:" + this.k;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "minAtomGroupTransfer:" + 
	        this.minAtomGroupTransfer;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "minPathLength:" + this.minPathLength;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "maxPathLength:" + this.maxPathLength;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "ifPreciseFindBranch:" + 
	        this.ifPreciseFindBranch;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "ifInterSet:" + this.ifInterSet;
	      parameter = String.valueOf(parameter) + "\r\n";
	      parameter = String.valueOf(parameter) + "ifAbundant:" + this.ifAbundant;
	      parameter = String.valueOf(parameter) + "\r\n";
	      fwriter.write(parameter);
	      CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
	      for (int i = 0; i < this.branchTrees.size(); i++) {
	        BranchTree tree = (BranchTree)this.branchTrees.get(i);
	        ArrayList<String> lines = new ArrayList<String>();
	        
	        for (Path p : tree.getPaths()) {
	          lines = collectionUtils.getUnion1(p.toTestArrayString(), 
	              lines);
	        }
	        String s = String.valueOf(this.start) + "," + this.end + "," + lines.size();
	        s = String.valueOf(s) + "\r\n";
	        for (String line : lines) {
	          s = String.valueOf(s) + line;
	          s = String.valueOf(s) + "\r\n";
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
	    Collections.sort(points, new Comparator<Point>()
	        {
	          public int compare(Point o1, Point o2) {
	            return o2.getCombTreeNodes().size() - 
	              o1.getCombTreeNodes().size();
	          }
	        });
	    int k = 0;
	    int sum = 1;
	    for (Point p : points) {
	      sum *= p.getCombTreeNodes().size();
	      System.out.println(p.getCombTreeNodes().size());
	      if (sum < this.maxCombination && k < this.maxPointsSize) {
	        bigPoints.add(p);


	        
	        k++; continue;
	      }  break;
	    }  return bigPoints;
	  }
	  
	  public void finish() {
	    this.ifInit = true;
	    this.originalPath = null;
	    this.atomPath = null;
	    this.dbUtils = null;
	    this.fileUtils = null;
	    this.treeTools = null;
	    this.pointMap = null;
	    this.appearMap = null;
	    this.waitPoints = null;
	    this.points = null;
	    this.resultPoint = null;
	    this.resultSet = null;
	    this.branchTrees = null;
	  }
	  
	  public void Init() {
	    this.ifInit = false;
	    this.originalPath = new ArrayList();
	    this.atomPath = new ArrayList();
	    this.dbUtils = new DBUtils();
	    this.fileUtils = new FileUtils();
	    this.treeTools = new TreeTools1();
	    this.pointMap = new HashMap();
	    this.appearMap = new HashMap();
	    this.waitPoints = new ArrayList();
	    this.points = new ArrayList();
	    this.resultPoint = new ArrayList();
	    this.resultSet = new ArrayList();
	    this.branchTrees = new ArrayList();
	    if(aSim==0.1)
	    {
	    	YenPath = "/data/c1-c2-01.txt";
	    }
	    if(aSim==0.2)
	    {
	    	YenPath = "/data/c1-c2-02.txt";
	    }
	    if(aSim==0.3)
	    {
	    	YenPath = "/data/c1-c2-03.txt";
	    }
	    if(aSim==0.4)
	    {
	    	YenPath = "/data/c1-c2-04.txt";
	    }
	    if(aSim==0.5)
	    {
	    	YenPath = "/data/c1-c2-05.txt";
	    }
	    if(aSim==0.6)
	    {
	    	YenPath = "/data/c1-c2-06.txt";
	    }
	    if(aSim==0.7)
	    {
	    	YenPath = "/data/c1-c2-07.txt";
	    }
	    if(aSim==0.8)
	    {
	    	YenPath = "/data/c1-c2-08.txt";
	    }
	    if(aSim==0.9)
	    {
	    	YenPath = "/data/c1-c2-09.txt";
	    }
	  }
	  
	  public ArrayList<Path> randPaths(ArrayList<Path> atomPath) {
	    Collections.sort(atomPath, new Comparator<Path>()
	        {
	          public int compare(Path o1, Path o2) {
	            return (int)(Process1.this.calPathWeight(o2) - Process1.this.calPathWeight(o1));
	          }
	        });
	    return new ArrayList(atomPath.subList(0, 10));
	  }

	  
	  public ArrayList<BranchTree> randBranchTrees(ArrayList<BranchTree> branchTrees) {
	    Collections.sort(branchTrees, new Comparator<BranchTree>()
	        {
	          public int compare(BranchTree o1, BranchTree o2) {
	            return (int)(Process1.this.calTreeWeight(o2) - Process1.this.calTreeWeight(o1));
	          }
	        });
	    return branchTrees;
	  }
	  
	  public double calPathWeight1(Path p) {
	   /* double aLength = 0.5D;
	    double aFreeEnergy = 0.1D;
	    double aTransf = 0.2D;
	    double hub = 0.2D;
	    */
	    ArrayList<String> comps = p.getCompName(); byte b; int j; String[] arrayOfString;
	    for (String c : abundant) {
			if (comps.contains(c)) {
				hub = 0.2;
				break;
			} else
				hub = 0;
		}
	    
	    int length = p.getRs().size();
	    ArrayList<Integer> transfEdge = (ArrayList)((Reaction)p.getRs().get(length - 1))
	      .getStart2transEdge().get(p);
	    double size = (Math.abs(this.natrueSize - length) / (this.natrueSize + length));
	    double transSize = 0.0D;
	    for (Iterator iterator = transfEdge.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
	      if (i > 0)
	        transSize++;  }
	    
	    transSize /= transfEdge.size();
	    double sumfreeEnergy = 0.0D;
	    for (Reaction r : p.getRs()) {
	      sumfreeEnergy += (3200.0D + r.getFreeEnergy()) / 10000.0D;
	    }
	    sumfreeEnergy /= length;
	    double weight = 0.0D;
	    weight = aLength * -size - sumfreeEnergy * aFreeEnergy + transSize * 
	      aTransf - hub;
	    p.setWeight(weight);
	    return weight;
	  }
	  
	  
	  public double calPathWeight(Path p) {
		    int length = p.getRs().size();
		    ArrayList<Integer> transfEdge = (ArrayList)((Reaction)p.getRs().get(length - 1))
		      .getStart2transEdge().get(p);
		    double size = (Math.abs(this.natrueSize - length) / (this.natrueSize + length));
		    double transSize = 0.0D;
		    for (Iterator iterator = transfEdge.iterator(); iterator.hasNext(); ) { int i = ((Integer)iterator.next()).intValue();
		      if (i > 0)
		        transSize++;  }
		    
		    transSize /= transfEdge.size();
		    double sumfreeEnergy = 0.0D;
		    double sumSim = 0.0;
		    for (Reaction r : p.getRs()) {
		      sumfreeEnergy += (3200.0D + r.getFreeEnergy()) / 10000.0D;
		      sumSim = r.getSimilarity();
		    }
		    sumfreeEnergy /= length;
		    double weight = 0.0D;
		    weight = aSim *sumSim - sumfreeEnergy * aFreeEnergy + transSize * 
		      aTransf;
		    p.setWeight(weight);
		    return weight;
		  }
	  
	  
	  public double calTreeWeight(BranchTree tree) {
	    //double aPoints = 0.8D;
	    double weight = 0.0D;
	    int psize = 0;
	    for (Path p : tree.getPaths()) {
	      weight += calPathWeight(p);
	      psize++;
	    } 
	    aPoints /= this.maxPointsSize;
	    weight /= psize;
	    return aPoints * tree.getPoints().size() + (1.0D - aPoints) * weight;
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
	        compMap.put(ID, CName);
	      } 
	    } catch (SQLException e) {
	      e.printStackTrace();
	    } 
	    return compMap;
	  }
}
