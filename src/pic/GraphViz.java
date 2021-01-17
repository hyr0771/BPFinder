package pic;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
public class GraphViz {
    private String runPath = "";
    private String dotPath = ""; 
    private String runOrder="";
    private String dotCodeFile="dotcode.txt";
    private String resultGif="dotGif";
    private StringBuilder graph = new StringBuilder();
    Runtime runtime=Runtime.getRuntime();
    public void run() {
        File file=new File(runPath);
        file.mkdirs();
        writeGraphToFile(graph.toString(), runPath);
        creatOrder();
        try {
            runtime.exec(runOrder);
            runtime.freeMemory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParameter(String resultGif)
    {
    	this.resultGif = resultGif;
    }
    public void creatOrder(){
        runOrder+=dotPath+" ";
        runOrder+=runPath;
        runOrder+="\\"+dotCodeFile+" ";
        runOrder+="-T jpg ";
        runOrder+="-o ";
        runOrder+=runPath;
        runOrder+="\\"+resultGif+".jpg";
        System.out.println(runOrder);
    }

    public void writeGraphToFile(String dotcode, String filename) {
        try {
            File file = new File(filename+"\\"+dotCodeFile);
            if(!file.exists()){
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(dotcode.getBytes());
            fos.close();
        } catch (java.io.IOException ioe) { 
            ioe.printStackTrace();
        }
     }  

    public GraphViz(String runPath,String dotPath) {
        this.runPath=runPath;
        this.dotPath=dotPath;
    }

    public void add(String line) {
        graph.append("\t"+line);
    }

    public void addln(String line) {
        graph.append("\t"+line + "\n");
    }

    public void addln() {
        graph.append('\n');
    }

    public void start_graph() {
        graph.append("digraph G {\n") ;
    }

    public void end_graph() {
        graph.append("}") ;
    } 
    
    public void setDotCodeFile(String fileName)
    {
    	fileName = fileName+".txt";
    	this.dotCodeFile = fileName;
    }

}
