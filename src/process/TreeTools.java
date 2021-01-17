package process;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import entity.BranchTree;
import entity.CAppearNode;
import entity.CombTreeNode;
import entity.Path;
import entity.Point;
import entity.Reaction;

public class TreeTools {
	public CombTreeNode unionTreeNode(CombTreeNode A, CombTreeNode B) {
		CombTreeNode node = new CombTreeNode();
		node.leftChild = A;
		node.rightChild = B;
		node.name = A.name + "," + B.name;
		node.edge = new ArrayList<Integer>();
		for (int i = 0; i < A.edge.size(); i++) {
			node.edge.add(i, A.edge.get(i) + B.edge.get(i));
		}
		CollectionUtils<Reaction> utils = new CollectionUtils<Reaction>();
		node.setRlist(utils.getUnion1(A.getRlist(), B.getRlist()));
		CollectionUtils<Path> utils2 = new CollectionUtils<Path>();
		node.setPaths(utils2.getUnion1(A.getPaths(), B.getPaths()));
		return node;
	}

	public CombTreeNode unionTreeNodeWithOr(CombTreeNode A, CombTreeNode B) {
		CombTreeNode node = new CombTreeNode();
		node.leftChild = A;
		node.rightChild = B;
		node.name = A.name + "," + B.name;
		node.edge = new ArrayList<Integer>();
		for (int i = 0; i < A.edge.size(); i++) {
			node.edge.add(i, A.edge.get(i) | B.edge.get(i));
		}
		CollectionUtils<Reaction> utils = new CollectionUtils<Reaction>();
		node.setRlist(utils.getUnion1(A.getRlist(), B.getRlist()));
		CollectionUtils<Path> utils2 = new CollectionUtils<Path>();
		node.setPaths(utils2.getUnion1(A.getPaths(), B.getPaths()));
		return node;
	}

	public boolean ifIntersetWithAll(CombTreeNode A, CombTreeNode B) {
		int size = A.edge.size();
		ArrayList<Integer> temp = new ArrayList<Integer>(Collections.nCopies(
				size, 0));
		boolean intersect = true;
		for (int i = 0; i < A.edge.size(); i++) {
			temp.set(i, A.edge.get(i) | B.edge.get(i));
		}
		for (int i = 0; i < A.edge.size(); i++) {
			if (temp.get(i) + A.edge.get(i) == 1)
				intersect = false;
		}
		if (intersect == true)
			return true;
		intersect = true;
		for (int i = 0; i < B.edge.size(); i++) {
			if (temp.get(i) + B.edge.get(i) == 1)
				intersect = false;
		}
		if (intersect == true)
			return true;
		return false;
	}

	public boolean ifInterset(ArrayList<CombTreeNode> nodes) {
		int size = nodes.get(0).edge.size();
		ArrayList<Integer> sum = new ArrayList<Integer>(Collections.nCopies(
				size, 0));
		for (CombTreeNode n : nodes) {
			for (int i = 0; i < size; i++) {
				int value = sum.get(i) + n.edge.get(i);
				if (value > 1)
					return true;
				sum.set(i, value);
			}
		}
		return false;
	}

	public boolean ifInterset(CombTreeNode A, CombTreeNode B) {
		int total = 0;
		int sum = 0;
		for (int i = 0; i < A.edge.size(); i++) {
			sum = A.edge.get(i) + B.edge.get(i);
			total = total + sum;
			if (sum > 1) {
				return true;
			}
		}
		if (total == 0)
			return true;
		return false;
	}

	public boolean ifInterset(ArrayList<Integer> A, ArrayList<Integer> B) {
		int total = 0;
		int sum = 0;
		for (int i = 0; i < A.size(); i++) {
			sum = A.get(i) + B.get(i);
			total = total + sum;
			if (sum > 1) {
				return true;
			}
		}
		if (total == 0)
			return true;

		return false;
	}

	public boolean ifInterset(HashMap<String, ArrayList<Integer>> edges,
			int startEdgeNum) {

		ArrayList<Integer> edge = new ArrayList<Integer>(Collections.nCopies(
				startEdgeNum, 0));
		for (String key : edges.keySet()) {
			int j = 0;
			for (Integer i : edges.get(key)) {
				if (edge.get(j) + i > 1) {
					j++;
					return true;
				} else {
					edge.set(j, edge.get(j) + i);
					j++;
				}
			}
		}
		return false;
	}

	public boolean ifIntersetWithOr(HashMap<String, ArrayList<Integer>> edges,
			int startEdgeNum) {
		ArrayList<Integer> temp = new ArrayList<Integer>(Collections.nCopies(
				startEdgeNum, 0));
		boolean ifIntersect = true;
		for (String key : edges.keySet()) {
			ArrayList<Integer> currentEdge = edges.get(key);
			for (int j = 0; j < currentEdge.size(); j++) {
				temp.set(j, temp.get(j) | currentEdge.get(j));
			}
		}
		for (String key : edges.keySet()) {
			ArrayList<Integer> currentEdge = edges.get(key);
			for (int j = 0; j < currentEdge.size(); j++) {
				if (temp.get(j) != currentEdge.get(j)) {
					ifIntersect = false;
					break;
				} else {
					ifIntersect = true;
				}
			}
			if (ifIntersect)
				return true;
		}
		return false;
	}

	public ArrayList<CombTreeNode> findPreciseComb(
			ArrayList<CombTreeNode> transferList) {
		ArrayList<CombTreeNode> combList = new ArrayList<CombTreeNode>();
		ArrayList<ArrayList<CombTreeNode>> result = new ArrayList<ArrayList<CombTreeNode>>();
		mutyCombTreeNode(transferList, new ArrayList<CombTreeNode>(), 0, result);
		for (ArrayList<CombTreeNode> nodes : result) {
			if (nodes.size() == 1) {
				continue;
			}
			if (!ifInterset(nodes)) {
				CombTreeNode currentNode = unionTreeNode(nodes.get(0),
						nodes.get(1));
				for (int i = 2; i < nodes.size(); i++) {
					currentNode = unionTreeNode(currentNode, nodes.get(i));
				}
				combList.add(currentNode);
			}
		}
		return combList;
	}

	public ArrayList<CombTreeNode> findBigComb(
			ArrayList<CombTreeNode> transferList) {
		ArrayList<CombTreeNode> combList = new ArrayList<CombTreeNode>();
		combList = findComb(transferList);
		if (combList == null)
			return combList;
		Collections.sort(combList, new Comparator<CombTreeNode>() {
			public int compare(CombTreeNode o1, CombTreeNode o2) {
				return o1.name.split(",").length - o2.name.split(",").length;
			}
		});
		if (combList.size() < 10)
			return combList;
		return new ArrayList<CombTreeNode>(combList.subList(0, 10));
	}

	public ArrayList<CombTreeNode> findBigCombWithIntersect(
			ArrayList<CombTreeNode> transferList) {
		ArrayList<CombTreeNode> combList = new ArrayList<CombTreeNode>();
		combList = findCombWithIntersert(transferList);
		if (combList == null)
			return combList;
		Collections.sort(combList, new Comparator<CombTreeNode>() {
			public int compare(CombTreeNode o1, CombTreeNode o2) {
				return o1.name.split(",").length - o2.name.split(",").length;
			}
		});
		if (combList.size() < 10)
			return combList;
		return new ArrayList<CombTreeNode>(combList.subList(0, 10));
	}

	public ArrayList<CombTreeNode> findCombWithIntersert(
			ArrayList<CombTreeNode> transferList) {
		ArrayList<CombTreeNode> combList = new ArrayList<CombTreeNode>();
		int transferNum = transferList.size();
		while (transferList.size() > 0) {
			CombTreeNode currentNode = transferList.get(0);
			transferList.remove(0);
			int currentSize = transferList.size();
			ArrayList<CombTreeNode> tempRemove = new ArrayList<CombTreeNode>();
			for (int i = 0; i < currentSize; i++) {
				if (ifIntersetWithAll(currentNode, transferList.get(i))) {
					currentNode = unionTreeNodeWithOr(currentNode,
							transferList.get(i));
					tempRemove.add(transferList.get(i));
				}
			}
			for (int j = 0; j < tempRemove.size(); j++) {
				CombTreeNode value = tempRemove.get(j);
				transferList.remove(value);
			}
			combList.add(currentNode);
		}
		if (combList.size() == transferNum)
			return null;
		ArrayList<CombTreeNode> tempRemove = new ArrayList<CombTreeNode>();
		for (CombTreeNode node : combList) {
			if (node.leftChild == null && node.rightChild == null)
				tempRemove.add(node);
		}
		for (CombTreeNode node : tempRemove) {
			combList.remove(node);
		}
		return combList;
	}
	
	/**
	 * 严格控制不允许原子重叠-适用于非重叠规则
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
/*	public boolean checknotintersecttotally(CombTreeNode A, CombTreeNode B) {
		boolean notintersect = true;
		ArrayList<Integer> edges_A = A.getEdge();
		ArrayList<Integer> edges_B = B.getEdge();
		ArrayList<Integer> failPos = new ArrayList<Integer>();
		for (int i = 0; i < edges_A.size(); i++) {
			if (i == 0 && edges_A.get(i) == 1) {
				if (edges_A.get(i + 1) == 0) {
					if (!failPos.contains(i + 1))
						failPos.add(i + 1);
				}
			}
			if (i == edges_A.size() - 1 && edges_A.get(i) == 1) {
				if (edges_A.get(i - 1) == 0) {
					if (!failPos.contains(i - 1))
						failPos.add(i - 1);
				}
			}
			if (edges_A.get(i) == 1 && i != 0 && i != edges_A.size() - 1) {
				if (edges_A.get(i + 1) == 0) {
					if (!failPos.contains(i + 1))
						failPos.add(i + 1);
				}
				if (edges_A.get(i - 1) == 0) {
					if (!failPos.contains(i - 1))
						failPos.add(i - 1);
				}
			}
		}
		for (int i : failPos) {
			if (edges_B.get(i) == 1) {
				notintersect = false;
				break;
			}
		}
		return notintersect;
	}*/
	
	/**
	 * 严格控制不允许原子重叠-适用于非重叠规则
	 * 
	 * @param A
	 * @param B
	 * @return
	 */
	public boolean checknotintersecttotally(CombTreeNode A, CombTreeNode B) {
		boolean notintersect = true;
		ArrayList<Integer> edges_A = A.getEdge();
		ArrayList<Integer> edges_B = B.getEdge();
		ArrayList<Integer> failPos = new ArrayList<Integer>();
		for (int i = 0; i < edges_A.size(); i++) {
			if (i == 0 && edges_A.get(i) == 1) {
				if (edges_A.get(i + 1) == 0) {
					if (!failPos.contains(i + 1))
						failPos.add(i + 1);
				}
			}
			if (i == edges_A.size() - 1 && edges_A.get(i) == 1) {
				if (edges_A.get(i - 1) == 0) {
					if (!failPos.contains(i - 1))
						failPos.add(i - 1);
				}
			}
			if (edges_A.get(i) == 1 && i != 0 && i != edges_A.size() - 1) {
				if (edges_A.get(i + 1) == 0) {
					if (!failPos.contains(i + 1))
						failPos.add(i + 1);
				}
				if (edges_A.get(i - 1) == 0) {
					if (!failPos.contains(i - 1))
						failPos.add(i - 1);
				}
			}
		}
		for (int i : failPos) {
			if (edges_B.get(i) == 1) {
				notintersect = false;
				break;
			}
		}
		return notintersect;
	}

	public ArrayList<CombTreeNode> findComb(ArrayList<CombTreeNode> transferList) {
		ArrayList<CombTreeNode> combList = new ArrayList<CombTreeNode>();
		int transferNum = transferList.size();
		while (transferList.size() > 0) {
			CombTreeNode currentNode = transferList.get(0);
			transferList.remove(0);
			int currentSize = transferList.size();
			ArrayList<CombTreeNode> tempRemove = new ArrayList<CombTreeNode>();
			for (int i = 0; i < currentSize; i++) {
				if (!ifInterset(currentNode, transferList.get(i))) {
					currentNode = unionTreeNode(currentNode,
							transferList.get(i));
					tempRemove.add(transferList.get(i));
					/*
					 * if (checknotintersecttotally(currentNode,
					 * transferList.get(i))) { currentNode =
					 * unionTreeNode(currentNode, transferList.get(i));
					 * tempRemove.add(transferList.get(i)); }
					 */
				}
			}
			for (int j = 0; j < tempRemove.size(); j++) {
				CombTreeNode value = tempRemove.get(j);
				transferList.remove(value);
			}
			combList.add(currentNode);
		}
		if (combList.size() == transferNum)
			return null;
		ArrayList<CombTreeNode> tempRemove = new ArrayList<CombTreeNode>();
		for (CombTreeNode node : combList) {
			if (node.leftChild == null && node.rightChild == null)
				tempRemove.add(node);
		}
		for (CombTreeNode node : tempRemove) {
			combList.remove(node);
		}
		return combList;
	}


	public void multiWay(ArrayList<Point> points,
			ArrayList<CombTreeNode> tempWay, int index,
			ArrayList<ArrayList<CombTreeNode>> result) {
		if (index >= points.size())
			return;
		Point currentPoint = points.get(index);
		ArrayList<CombTreeNode> data = currentPoint.getCombTreeNodes();
		ArrayList<CombTreeNode> tmp = new ArrayList<CombTreeNode>(tempWay);
		for (int i = 0; i < data.size(); i++) {
			CombTreeNode current = data.get(i);
			if (i != 0)
				tempWay = new ArrayList<CombTreeNode>(tmp);

			if (index < points.size() - 1) {
				tempWay.add(current);
				multiWay(points, tempWay, index + 1, result);
			}

			if (index == points.size() - 1) {
				tempWay.add(current);
				result.add(new ArrayList<CombTreeNode>(tempWay));
				tempWay.clear();
			}
		}
		return;
	}

	public void multyPoints(ArrayList<Point> points, ArrayList<Point> tempWay,
			int index, ArrayList<ArrayList<Point>> result) {
		if (index >= points.size())
			return;
		Point currentPoint = points.get(index);
		ArrayList<Point> tmp = new ArrayList<Point>(tempWay);
		for (int i = 0; i < 2; i++) {
			tempWay = new ArrayList<Point>(tmp);
			if (i == 0) {
				multyPoints(points, tempWay, index + 1, result);
			} else {
				tempWay.add(currentPoint);
				multyPoints(points, tempWay, index + 1, result);
				result.add(new ArrayList<Point>(tempWay));
				tempWay.clear();
			}
		}
	}

	public void mutyCombTreeNode(ArrayList<CombTreeNode> nodes,
			ArrayList<CombTreeNode> tempWay, int index,
			ArrayList<ArrayList<CombTreeNode>> result) {
		if (index >= nodes.size())
			return;
		CombTreeNode currentNode = nodes.get(index);
		ArrayList<CombTreeNode> tmp = new ArrayList<CombTreeNode>(tempWay);
		for (int i = 0; i < 2; i++) {
			tempWay = new ArrayList<CombTreeNode>(tmp);
			if (i == 0) {
				mutyCombTreeNode(nodes, tempWay, index + 1, result);
			} else {
				tempWay.add(currentNode);
				mutyCombTreeNode(nodes, tempWay, index + 1, result);
				result.add(new ArrayList<CombTreeNode>(tempWay));
				tempWay.clear();
			}
		}
	}

	public boolean checkIfReactionSame(Reaction currentR, Reaction checkR,
			Path currentPath, Path checkPath) {
		CollectionUtils<String> utils = new CollectionUtils<String>();
		if (currentR.getReactionId() != checkR.getReactionId())
			return false;
		if (currentR.getSubtrateId() != checkR.getSubtrateId()
				|| currentR.getProductId() != checkR.getProductId())
			return true;
		ArrayList<Integer> currentEdge = currentR.getString2transEdge().get(
				currentPath.getName() + "");
		ArrayList<String> checekPathName = new ArrayList<String>(
				Arrays.asList(checkPath.getName().split(",")));
		ArrayList<String> name = utils.getIntersection1(checekPathName,
				new ArrayList(checkR.getString2transEdge().keySet()));
		ArrayList<Integer> checkEdge = checkR.getString2transEdge().get(
				name.get(0));
		if (checkEdge == null) {
			System.out
					.println(checkR.getString2transEdge().keySet().toString());
		}
		if (currentEdge.size() != checkEdge.size())
			return false;
		for (int i = 0; i < currentEdge.size(); i++) {
			if (currentEdge.get(i) != checkEdge.get(i))
				return false;
		}
		return true;
	}

	public ArrayList<Path> checkSameReactionConflict(ArrayList<Path> paths) {
		ArrayList<String> rName = new ArrayList<String>();
		CollectionUtils<String> utils = new CollectionUtils<String>();
		ArrayList<Path> okPath = new ArrayList<Path>();
		Path checkPath = paths.get(0);
		okPath.add(checkPath);
		for (int i = 1; i < paths.size(); i++) {
			boolean ifConflicrt = false;
			Path currentPath = paths.get(i);
			rName = utils.getIntersection1(checkPath.getRsName(),
					currentPath.getRsName());
			for (String name : rName) {
				Reaction currentR = currentPath.findReaction(name);
				Reaction checkR = checkPath.findReaction(name);
				if (!checkIfReactionSame(currentR, checkR, currentPath,
						checkPath)) {
					ifConflicrt = true;
					break;
				}
			}
			if (!ifConflicrt) {
				okPath.add(currentPath);
				Path newPath = new Path();
				ArrayList<Reaction> rs = new ArrayList<Reaction>(
						checkPath.getRs());
				rs.addAll(currentPath.getRs());
				newPath.setRs(rs);
				newPath.setName(currentPath.getName() + ","
						+ checkPath.getName());
				checkPath = newPath;
			}
		}
		return okPath;
	}

	public ArrayList<Path> checkCompConflict2Intersect(ArrayList<Path> paths) {
		ArrayList<Path> okPaths = new ArrayList<Path>();
		int edgeNum = paths.get(0).getRs().get(0).getEdge().size();
		FileUtils fileUtils = new FileUtils();
		HashMap<String, Point> pointMap = new HashMap<String, Point>();
		HashMap<String, CAppearNode> appearMap = new HashMap<String, CAppearNode>();
		HashMap<String, ArrayList<Integer>> tempedges = new HashMap<String, ArrayList<Integer>>();
		for (int j = 0; j < paths.size(); j++) {
			Path p = paths.get(j);
			boolean ifok = true;
			ArrayList<Reaction> rs = p.getRs();
			int pathL = rs.size();
			String subtrate = "";
			String product = "";
			HashMap<String, Point> tempPointMap = new HashMap<String, Point>(
					pointMap);
			HashMap<String, CAppearNode> tempAppearMap = new HashMap<String, CAppearNode>(
					appearMap);
			for (int i = 0; i < pathL; i++) {
				subtrate = rs.get(i).getSubtrateId();
				product = rs.get(i).getProductId();
				if (tempedges.get(rs.get(i).getReactionId()) == null)
					tempedges.put(rs.get(i).getReactionId(), rs.get(i)
							.getStart2transEdge().get(p));
				if (i == 0) {
					fileUtils.addIfPoint(product,
							FileUtils.CompoundType.product, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				} else if (i == pathL - 1) {
					fileUtils.addIfPoint(subtrate,
							FileUtils.CompoundType.substrate, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				} else if (i != 0 && i != pathL - 1) {
					fileUtils.addIfPoint(subtrate,
							FileUtils.CompoundType.substrate, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
					fileUtils.addIfPoint(product,
							FileUtils.CompoundType.product, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				}
			}
			for (String key : tempPointMap.keySet()) {
				ArrayList<Reaction> pre = tempPointMap.get(key).getPrecursor();
				ArrayList<Reaction> behind = tempPointMap.get(key).getBehind();
				HashMap<String, ArrayList<Integer>> preedge = new HashMap<String, ArrayList<Integer>>();
				HashMap<String, ArrayList<Integer>> behindedge = new HashMap<String, ArrayList<Integer>>();
				for (Reaction r : pre) {
					if (preedge.get(r.getReactionId()) == null)
						preedge.put(r.getReactionId(),
								tempedges.get(r.getReactionId()));
				}
				for (Reaction r : behind) {
					if (behindedge.get(r.getReactionId()) == null)
						behindedge.put(r.getReactionId(),
								tempedges.get(r.getReactionId()));
				}
				if (!ifIntersetWithOr(preedge, edgeNum)
						|| !ifIntersetWithOr(behindedge, edgeNum)) {
					ifok = false;
					break;
				}
			}
			if (ifok) {
				appearMap = new HashMap<String, CAppearNode>(tempAppearMap);
				pointMap = new HashMap<String, Point>(tempPointMap);
				okPaths.add(p);
			}
		}
		return okPaths;
	}

	public ArrayList<Path> checkCompConflict2(ArrayList<Path> paths) {
		ArrayList<Path> okPaths = new ArrayList<Path>();
		int edgeNum = paths.get(0).getRs().get(0).getEdge().size();
		FileUtils fileUtils = new FileUtils();
		HashMap<String, Point> pointMap = new HashMap<String, Point>();
		HashMap<String, CAppearNode> appearMap = new HashMap<String, CAppearNode>();
		HashMap<String, ArrayList<Integer>> tempedges = new HashMap<String, ArrayList<Integer>>();
		for (int j = 0; j < paths.size(); j++) {
			Path p = paths.get(j);
			boolean ifok = true;
			ArrayList<Reaction> rs = p.getRs();
			int pathL = rs.size();
			String subtrate = "";
			String product = "";
			HashMap<String, Point> tempPointMap = new HashMap<String, Point>(
					pointMap);
			HashMap<String, CAppearNode> tempAppearMap = new HashMap<String, CAppearNode>(
					appearMap);
			for (int i = 0; i < pathL; i++) {
				subtrate = rs.get(i).getSubtrateId();
				product = rs.get(i).getProductId();
				if (tempedges.get(rs.get(i).getReactionId()) == null)
					tempedges.put(rs.get(i).getReactionId(), rs.get(i)
							.getStart2transEdge().get(p));
				if (i == 0) {
					fileUtils.addIfPoint(product,
							FileUtils.CompoundType.product, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				} else if (i == pathL - 1) {
					fileUtils.addIfPoint(subtrate,
							FileUtils.CompoundType.substrate, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				} else if (i != 0 && i != pathL - 1) {
					fileUtils.addIfPoint(subtrate,
							FileUtils.CompoundType.substrate, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
					fileUtils.addIfPoint(product,
							FileUtils.CompoundType.product, p,
							p.getRs().get(i), tempPointMap, tempAppearMap);
				}
			}
			for (String key : tempPointMap.keySet()) {
				ArrayList<Reaction> pre = tempPointMap.get(key).getPrecursor();
				ArrayList<Reaction> behind = tempPointMap.get(key).getBehind();
				HashMap<String, ArrayList<Integer>> preedge = new HashMap<String, ArrayList<Integer>>();
				HashMap<String, ArrayList<Integer>> behindedge = new HashMap<String, ArrayList<Integer>>();
				for (Reaction r : pre) {
					if (preedge.get(r.getReactionId()) == null)
						preedge.put(r.getReactionId(),
								tempedges.get(r.getReactionId()));
				}
				for (Reaction r : behind) {
					if (behindedge.get(r.getReactionId()) == null)
						behindedge.put(r.getReactionId(),
								tempedges.get(r.getReactionId()));
				}
				if (ifInterset(preedge, edgeNum)
						|| ifInterset(behindedge, edgeNum)) {
					ifok = false;
					break;
				}
			}
			if (ifok) {
				appearMap = new HashMap<String, CAppearNode>(tempAppearMap);
				pointMap = new HashMap<String, Point>(tempPointMap);
				okPaths.add(p);
			}
		}
		return okPaths;
	}

	/**
	 * 判断两个字符是否都出现在一个字符串中
	 * 比如“01”都出现在"012"中
	 * @param newmerging
	 * @param minintersect
	 * @return
	 */
	public boolean ifminintersect(String newmerging,String minintersect)
	{
		char first = minintersect.charAt(0);
		char second = minintersect.charAt(1);
		int[] flag = new int[]{0,0};
		for(int i = 0; i<newmerging.length();i++)
		{
			if(newmerging.charAt(i)==first)
				flag[0] = 1;
			if(newmerging.charAt(i)==second)
				flag[1] = 1;
		}
		if(flag[0]==1&&flag[1]==1)
			return true;
		return false;
	}
	
	/**
	 * 检测路径是否可以粘连在一起
	 * @param allPaths
	 * @return
	 */
	public ArrayList<Path> checkNewBranch(ArrayList<Path> allPaths) {
		if(allPaths.size()!=2)
			return null;
		System.out.println("come in! "+allPaths.size());
		CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
		ArrayList<String> intersectMin = new ArrayList<String>();
		String mergingP = "0";
		for (int i = 0; i < allPaths.size(); i++) {
			Path p1 = allPaths.get(i);
			for (int j = i + 1; j < allPaths.size(); j++) {
				Path p2 = allPaths.get(j);
				if(collectionUtils.getIntersection1(p1.getCompName(), p2.getCompName()).size()==2)
				{
					String intersect = i+""+j;
					if(!intersectMin.contains(intersect))
						intersectMin.add(intersect);
				}
			}
		}

		for(String in:intersectMin)
			System.out.println(in);
		int[] flag = new int[allPaths.size()];
		//被用过的合并路径就不可以再使用了
		int[] intersetmin = new int[intersectMin.size()];
		flag[0] = 1;
		for (int i = 1; i < allPaths.size(); i++) {
			flag[i] = 0;
			String newmerging = mergingP+i;
			int j = 0;
			for(String intersect:intersectMin)
			{
				if(ifminintersect(newmerging,intersect)&&intersetmin[j]==0)
				{
					System.out.println(newmerging+"--"+intersect);
					mergingP = mergingP+i;
					System.out.println("mergingP-"+mergingP);
					flag[i] = 1;
					intersetmin[j] = 1;
					break;
				}
				j++;
			}
		}
		for(int i:flag)
		{
				System.out.println(i);
			if(i==0)
				return null;
		}
		return allPaths;
	}

	public ArrayList<Path> checkNewBranch2(ArrayList<Path> allPaths) {
		CollectionUtils<String> collectionUtils = new CollectionUtils<String>();
		ArrayList<Path> paths = new ArrayList<Path>();
		ArrayList<String> points = new ArrayList<String>();
		for (int i = 0; i < allPaths.size(); i++) {
			if (i == 0)
				points = collectionUtils.getIntersection1(allPaths.get(i)
						.getCompName(), allPaths.get(i + 1).getCompName());
			points = collectionUtils.getIntersection1(allPaths.get(i)
					.getCompName(), points);
		}
		for (Path p : allPaths) {
			if (collectionUtils.getIntersection1(p.getCompName(), points)
					.size() != 0)
				paths.add(p);
		}
		return paths;
	}

	public BranchTree solveConflict(ArrayList<CombTreeNode> treeNode,
			ArrayList<Point> points, ArrayList<Point> waitPoints) {
		BranchTree branchTree = new BranchTree();
		CollectionUtils<Path> collectionUtils = new CollectionUtils<Path>();
		ArrayList<Path> allPath = new ArrayList<Path>();
		int size = treeNode.size();
		boolean ifConflict = true;
		for (int i = 0; i < size; i++) {
			allPath = collectionUtils.getUnion1(allPath, treeNode.get(i)
					.getPaths());
		}
		allPath = checkSameReactionConflict(allPath);
		allPath = checkCompConflict2(allPath);
		ArrayList<Point> ps = new ArrayList<Point>();
		for (int i = 0; i < treeNode.size(); i++) {
			if (allPath.containsAll(treeNode.get(i).getPaths())) {
				ifConflict = false;
				ps.add(points.get(i));
			}
		}
		if (ifConflict)
			return null;
		//allPath = checkNewBranch(allPath);
		if (allPath==null||allPath.size() == 0)
			return null;
		branchTree.setPaths(allPath);
		branchTree.setPoints(ps);
		return branchTree;
	}

	public BranchTree solveConflictWithIntersect(
			ArrayList<CombTreeNode> treeNode, ArrayList<Point> points,
			ArrayList<Point> waitPoints) {
		BranchTree branchTree = new BranchTree();
		CollectionUtils<Path> collectionUtils = new CollectionUtils<Path>();
		ArrayList<Path> allPath = new ArrayList<Path>();
		int size = treeNode.size();
		boolean ifConflict = true;
		for (int i = 0; i < size; i++) {
			allPath = collectionUtils.getUnion1(allPath, treeNode.get(i)
					.getPaths());
		}
		allPath = checkSameReactionConflict(allPath);
		allPath = checkCompConflict2Intersect(allPath);
		ArrayList<Point> ps = new ArrayList<Point>();
		for (int i = 0; i < treeNode.size(); i++) {
			if (allPath.containsAll(treeNode.get(i).getPaths())) {
				ifConflict = false;
				ps.add(points.get(i));
			}
		}
		if (ifConflict)
			return null;
		allPath = checkNewBranch(allPath);
		if (allPath==null||allPath.size() == 0)
			return null;
		branchTree.setPaths(allPath);
		branchTree.setPoints(ps);
		return branchTree;
	}

	public ArrayList<BranchTree> cutBranchTree(ArrayList<BranchTree> branchTrees) {
		HashMap<String, BranchTree> appearTime = new HashMap<String, BranchTree>();
		ArrayList<BranchTree> trees = new ArrayList<BranchTree>();
		for (BranchTree tree : branchTrees) {
			String key = "";
			for (Path p : tree.getPaths()) {
				key = key + p.getCompName().toString();
			}
			if (appearTime.get(key) == null) {
				appearTime.put(key, tree);
			}
		}
		for (String key : appearTime.keySet()) {
			trees.add(appearTime.get(key));
		}
		return trees;
	}
}
