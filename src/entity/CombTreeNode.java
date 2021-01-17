package entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
public class CombTreeNode {
	public String name = "";
	public ArrayList<Integer> edge = null;
	public ArrayList<Path> paths = new ArrayList<Path>();
	public HashMap<Reaction, ArrayList<Path>> rEdgeMap = new HashMap<Reaction, ArrayList<Path>>();
	public ArrayList<Reaction> rlist = new ArrayList<Reaction>();
	public HashMap<Reaction, ArrayList<Path>> getrEdgeMap() {
		return rEdgeMap;
	}
	public void setrEdgeMap(HashMap<Reaction, ArrayList<Path>> rEdgeMap) {
		this.rEdgeMap = rEdgeMap;
	}
	public ArrayList<Reaction> getRlist() {
		return rlist;
	}
	public void setRlist(ArrayList<Reaction> rlist) {
		this.rlist = rlist;
	}
	public CombTreeNode leftChild = null;  
	public CombTreeNode rightChild = null;
	public CombTreeNode parent = null;
	public CombTreeNode()
	{};
	public CombTreeNode(String name, ArrayList<Integer> edge,
			CombTreeNode leftChild, CombTreeNode rightChild, CombTreeNode parent) {
		super();
		this.name = name;
		this.edge = edge;
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.parent = parent;
	}
	public ArrayList<Path> getPaths() {
		return paths;
	}
	public void setPaths(ArrayList<Path> paths) {
		this.paths = paths;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<Integer> getEdge() {
		return edge;
	}
	public void setEdge(ArrayList<Integer> edge) {
		this.edge = edge;
	}
	public CombTreeNode getLeftChild() {
		return leftChild;
	}
	public void setLeftChild(CombTreeNode leftChild) {
		this.leftChild = leftChild;
	}
	public CombTreeNode getRightChild() {
		return rightChild;
	}
	public void setRightChild(CombTreeNode rightChild) {
		this.rightChild = rightChild;
	}
	public CombTreeNode getParent() {
		return parent;
	}
	public void setParent(CombTreeNode parent) {
		this.parent = parent;
	}
	public String ToString()
	{
		String s = "";
		Queue<CombTreeNode> q = new LinkedList<CombTreeNode>();  
        q.offer(this);  
        while (!q.isEmpty()) {  
        	CombTreeNode temp = q.poll();  
           // visitNode(temp);  
        	s = s+"当前节点为："+temp.name;
        	if (temp.leftChild!=null)
        		s = s+" 它的左子： "+temp.leftChild.name;
        	if (temp.rightChild!=null)
        		s = s+" 它的右子：： "+temp.rightChild.name;
            if (temp.leftChild != null) {  
                q.offer(temp.leftChild);  
            }  
            if (temp.rightChild != null) {  
                q.offer(temp.rightChild);  
            }  
        }  
        return s;
	}
}
