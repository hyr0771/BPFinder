package process;

import java.util.ArrayList;

public class MyMain {
	
	public static void readPara(String[] args)
	{
		int zz = 0;
		String fileName = args[0];
		String content = FileUtils.readFileByLines(fileName);
		String[] lines = content.split("\n");
		String host = "localhost";
		String port ="3306";
		String dbName = "fga";
		String user = "root";
		String password = "123456";
		String start = "";
		String end = "";
		int k = 2000;
		int minAtomGroupTransfer = 2;
		int minPathLength = 2;
		int maxPathLength = 15;
		boolean ifPreciseFindBranch = true;
		String saveTxtPath = "";
		String savePicPath = "";
		boolean ifStartBySingle = false;
		boolean ifInterset = true;
		boolean ifAbundant = false;
		boolean ifDraw = true;
		String graPhVizPath = "";
		double aLength = 0.5;
		double aFreeEnergy = 0.1;
		double aTransf = 0.2;
		double aSim = 0.1;
		double aPoints = 0.8;

		for (String line : lines) {
			String name = line.split(" +")[0].trim();
			String value = line.split(" +")[1].trim();
			System.out.println(line);
			/*if(name.equals("host"))
			{
				host = value;
			}
			else if(name.equals("port"))
			{
				port = value;
			}
			else if(name.equals("user"))
			{
				user = value;
			}
			else if(name.equals("password"))
			{
				password = value;
			}
			else if(name.equals("dbName"))
			{
				dbName = value;
			}
			else if (name.equals("aLength")) {
				aLength = Double.parseDouble(value);
			} */
			if (name.equals("aSf")) {
				aFreeEnergy = Double.parseDouble(value);
			} 
			else if (name.equals("aT")) {
				aTransf = Double.parseDouble(value);
			} 
			else if (name.equals("aS")) {
				aSim = Double.parseDouble(value);
			} 
			else if (name.equals("aP")) {
				aPoints = Double.parseDouble(value);
			} 
			else if (name.equals("k")) {
				k = Integer.parseInt(value);
			} else if (name.equals("sourceCompound")) {
				start = value;
			} else if (name.equals("targetCompound")) {
				end = value;
			} else if (name.equals("numberOfTheMinimalAtomGroups")) {
				minAtomGroupTransfer = Integer.parseInt(value);
			} /*else if (name.equals("minPathLength")) {
				minPathLength = Integer.parseInt(value);
			} else if (name.equals("maxPathLength")) {
				maxPathLength = Integer.parseInt(value);
			} else if (name.equals("ifPreciseFindBranch")) {
				ifPreciseFindBranch = Boolean.parseBoolean(value);
			}*/ else if (name.equals("resultDirectory")) {
				saveTxtPath = value;
				savePicPath = value;
			} /*else if (name.equals("outputPicPath")) {
			} */
			  else if (name.equals("mergingStrategy")) {
				if(value.equals("overlapping"))
				{
					ifInterset = true;
					ifStartBySingle = true;
				}
				else if(value.equals("non-overlapping"))
				{
					ifInterset = false;
					ifStartBySingle = true;
				}
				else
					ifStartBySingle = false;
			} /*else if (name.equals("ifAbundant")) {
				ifAbundant = Boolean.parseBoolean(value);
			}*/ /*else if (name.equals("whetherResultIsShownByGraph")) {
				ifDraw = Boolean.parseBoolean(value);
			} */else if (name.equals("graphVizDirectory")) {
				graPhVizPath = value;
			}
		}

		if (!ifStartBySingle) {
			long startTime = System.currentTimeMillis(); // 获取开始时间
			Process1 Process1 = new Process1();
			Process1.setParameter(start, end, k, minAtomGroupTransfer,
					minPathLength, maxPathLength, ifPreciseFindBranch,
					saveTxtPath, savePicPath, ifInterset, ifAbundant, ifDraw,
					graPhVizPath,host, port,
					dbName, user,password,zz,aLength,aFreeEnergy,aTransf,aSim,aPoints);
			Process1.startByAll();
			long endTime = System.currentTimeMillis(); // 获取结束时间
			System.out.println("Runing time：" + (endTime - startTime) + "ms");
		} else {
			long startTime = System.currentTimeMillis(); // 获取开始时间
			Process1 Process1 = new Process1();
			Process1.setParameter(start, end, k, minAtomGroupTransfer,
					minPathLength, maxPathLength, ifPreciseFindBranch,
					saveTxtPath, savePicPath, ifInterset, ifAbundant, ifDraw,
					graPhVizPath,host, port,
					dbName, user, password,zz,aLength,aFreeEnergy,aTransf,aSim,aPoints);
			Process1.startBySingle();
			long endTime = System.currentTimeMillis(); // 获取结束时间
			System.out.println("Runing time：" + (endTime - startTime) + "ms");
		}

	}
	
	public static void main(String[] args) {
		readPara(args);
	}
}
