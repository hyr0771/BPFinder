package process;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CollectionUtils<T> {
	public ArrayList<String> transStringToArray(String[] original)
	{
		ArrayList<String> transf = new ArrayList<String>();
		for(String temp:original)
		{
			transf.add(temp);
		}
		return transf;
	}
	
	public boolean checkListIfEqual(ArrayList<T> A,ArrayList<T> B)
	{
		if(A.size()!=B.size())
			return false;
		for(T a:A)
		{
			if(!B.contains(a))
				return false;
		}
		return true;
	}

	public ArrayList<T> getUnion1(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.addAll(setB);
		ArrayList<T> temp = new ArrayList<T>(setA);
		return temp;
	}

	public Set<T> getUnion(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.addAll(setB);
		return setA;
	}

	public Set<T> getUnion(Set<T> A,Set<T> B)
	{
		A.addAll(B);
		return A;
	}

	public ArrayList<T> getIntersection1(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.retainAll(setB);
		ArrayList<T> temp = new ArrayList<T>(setA);
		return temp;
	}
	
	public Set<T> getIntersection(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.retainAll(setB);
		return setA;
	}
	
	public Set<T> getIntersection(ArrayList<T> A,Set<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		setA.retainAll(B);
		return setA;
	}

	public Set<T> getIntersection(Set<T> A,ArrayList<T> B)
	{
		Set<T> setB = new HashSet<T>(B);
		A.retainAll(setB);
		return A;
	}

	public Set<T> getDifference(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.removeAll(setB);
		return setA;
	}
	
	public Set<T> getDifference(ArrayList<T> A,Set<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		setA.removeAll(B);
		return setA;
	}
	
	public ArrayList<T> getDifference1(ArrayList<T> A,ArrayList<T> B)
	{
		Set<T> setA = new HashSet<T>(A);
		Set<T> setB = new HashSet<T>(B);
		setA.removeAll(setB);
		ArrayList<T> temp = new ArrayList<T>(setA);
		return temp;
	}
	
	public ArrayList<T> changeToList(Set<T> A)
	{
		ArrayList<T> list = new ArrayList<T>(A);
		return list;
	}

}
