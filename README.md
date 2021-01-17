# BPFinder
**BPFinder**(**B**ranched **P**athway **F**inder) is a method for finding metabolic branched pathways between a source compound and a target compound.
**BPFinder** consists of three main steps: **First**, BPFinder finds linear pathways from source to target by tracking the movement of atom groups through metabolic networks. **Second**, BPFinder determines the branched compounds in the linear pathways based on the structure of the conserved atom groups from the source compound, and merges these linear pathways into branched metabolic pathways by the branched compounds. **Finally**, BPFinder sorts the resulting branched pathways by the combined information of reaction thermodynamics, compound similarity and the conserved atom groups to pick out more biochemically feasible branched metabolic pathways for the user.

# Requirements and installation
1. BPFinder was written and tested on Java with version "1.8.0_201". **Java with version "1.8.0_201"(or higher)** need to be installed to work with BPFinder.
2. The data required for BPFinder program to find branched pathways are preserved in MySQL Database with version "8.0.14". To work with BPFinder, **MySQL Database with version "8.0.14"(or higher)** need to be installed. 
3. To output resulting branched pathways by directed graph, the results can be visualized using the graph visualization software graphViz with version "2.38.0". User need to  install **graphViz with version "2.38.0"(or higher)** to obtain the resulting branched pathways by directed graph.

# Download data and program
BPFinder program is packaged as a JAR bundle called BPFinder.jar. To provide ease of use, user can download **BPFinder.jar** to run BPFinder with command line(see detail in <a  href="#1">Usage</a>).  The data required for running BPFinder is packaged as a SQL file called fga.sql, fga.sql is compressed as fga.zip. And you need to decompress fga.zip to fga.sql, and add the data in fga.sql to MySQL Database(see detail in <a href="#2">Dataset Preparation</a>). The sample configure file **"config.txt"** is a sample for adjusting the running parameters of BPFinder(see detail in <a  href="#3">Running parameters</a>).

# Dataset Preparation
<a name="2">Assume that in MySQL, user stores data in database ‘fga’, the user name is “root”, password is “123456”, the directory of config.txt is "D:\\config.txt", the followings are the command and operation on Microsoft Windows 6.1.7601</a> for adding data to MySQL Database.
1. Download fga.zip and decompress fga.zip to fga.sql.
2. Enter mysql with command line ``` mysql -u -root -p ``` and input the password "123456"
3. Create database "fga" with command line ```create database fga;```
4. Enter database "fga" with command line ```use fga;```
5. Import the data "fga.sql" with command line ```source D:\\fga.sql;```

# Usage
<a name="1">User can run BPFinder by one command line as follows:</a>

```java -jar (the directory of BPFinder.jar) (the directory of configure file) ```

**the directory of BPFinder.jar** is the directory of "BPFinder.jar".

**the directory of configure file** is the directory of the configure file.

For example: ```java -jar D:\\BPFinder.jar D:\\configure.txt ```
And the Search results are in "resultDirectory" which is specified by user in "config.txt".

# Running parameters
<a name="3">User can use configure file to adjust the running patameters of BPFinder, and the following table is the specific contents of "config.txt"</a>
| Option | Description | Default value |
| -----  | ------| ----|
| sourceCompound | Source compound in KEGG format | Required |
| targetCompound | Target compound in KEGG format | Required |
| k | Number of the candidate linear pathways | 2000 |
| numberOfTheMinimalAtomGroups | Number of the minimal atom groups transferred from source to target compound | 2 |
| mergingStrategy | For this parameter，three merging rules for branched compound can be chosen:
**overlapping
  non-overlapping
  default**. The user can choose one of these three parameters to search pathways. The default merging strategy means BPFinder will first search the branched pathways by non-overlapping rule, and then BPFinder will search the branched pathways by overlapping rule in the case of no branched pathways are returned by non-overlapping rule.| default |
| aSf | aSf is a weight parameter for BPFinder, which is used to adjust the relative weights of Gibbs free energy of reaction when ranking the resulting pathways. | 0.1 |
| aT | aT is a weight parameter for BPFinder, which is used to adjust the relative weights of conserved atom groups when ranking the resulting pathways. | 0.2 |
| aS | aS is a weight parameter for BPFinder, which is used to adjust the relative weights of compound similarity when ranking the resulting pathways. | 0.1 |
| aP | aP is a weight parameter for BPFinder, which is used to adjust the relative weights of the branched compounds when ranking the resulting pathways.| 0.8 |
| graphVizDirectory | The directory of graphViz "dot.exe" file, users should install the graphViz.| D:\\graphviz\\bin\\dot.exe |
| resultDirectory | The directory of searching results, users can find the running results of the program in this directory. | D:\\results\\ |





