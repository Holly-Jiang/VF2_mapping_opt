package wip.VF2.linux;

import wip.VF2.core.VF2;
import wip.VF2.graph.Graph;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DealData {
    public static void main(String[] args) throws FileNotFoundException {

        Path graphPath = Paths.get("/root/graph/new/VF2_mapping_opt/data/graphDB", "mygraphdb3.data");
        Path queryPath = Paths.get("/root/graph/new/VF2_mapping_opt/data/graphDB", "Ex2.my");
        Path outPath = Paths.get("/root/graph/new/VF2_mapping_opt/data/graphDB", "res_Ex2.my");
        Path outIniPath = Paths.get("/root/graph/new/VF2_mapping_opt/data/graphDB", "ini_map_GQL.my");

        Path mappingPath = Paths.get("/root/graph/new/SubgraphComparing/build/matching", "res.dat");
        if (args.length == 0) {
            printUsage();
            System.out.println();
            System.out.println("Warning: no arguments given, using default arguments");
            System.out.println();
        }

        for (int i = 0; i < args.length; i++){
            if (args[i].equals("-t")) {
                graphPath = Paths.get(args[i+1]);
                i++;
            } else if (args[i].equals("-q")) {
                queryPath = Paths.get(args[i+1]);
                i++;
            } else if (args[i].equals("-o")) {
                outPath = Paths.get(args[i+1]);
                i++;
            }  else if (args[i].equals("-i")) {
                outIniPath = Paths.get(args[i+1]);
                i++;
            }else {
                printUsage();
                System.exit(1);
            }
        }

        System.out.println("Target Graph Path: " + graphPath.toString());
        System.out.println("Query Graph Path: " + queryPath.toString());
        System.out.println("Output Path: " + outPath.toString());
        System.out.println("OutIniput Path: " + outIniPath.toString());
        System.out.println();
        PrintWriter iniWriter = new PrintWriter(outIniPath.toFile());

        ArrayList<Graph> graphSet = loadGraphSetFromFile(graphPath, "Graph ");
        ArrayList<Graph> querySet = loadGraphSetFromFile(queryPath, "Query ");
        List<List<Integer>> mapping=loadDataSetFromFile(mappingPath,"mapping");

        int maxMappingCount=0;
        Map<List<Integer>,Integer> mappingResult=new HashMap<>();
        for (int i=0;i<mapping.size();i++){
            int m=0;
            for (int j=0;j<mapping.get(i).size();j++){
                if (mapping.get(i).get(j)-99999!=0){
                    m++;
                }
            }
            mappingResult.put(mapping.get(i),m);
            if (m-maxMappingCount>0){
                maxMappingCount=m;
            }
        }
        VF2 vf2= new VF2();

        System.out.println("Loading Done!");
        System.out.println();
        for (Graph queryGraph : querySet){
            List<List<Integer>> stateSet = vf2.delData(graphSet, queryGraph,mappingResult);

            for (int i=0;i<stateSet.size();i++){
                int m=0;
                System.out.println("t "+i);
                iniWriter.write("t "+i);
                iniWriter.write("\n");
                for (int j=0;j<stateSet.get(i).size();j++){
                    System.out.println(stateSet.get(i).get(j)+" "+j);
                    iniWriter.write(stateSet.get(i).get(j)+" "+j);
                    iniWriter.write("\n");
                }
            }
        }

        iniWriter.write("t # -1");
        iniWriter.flush();
        iniWriter.close();
    }

    /**
     * Load graph set from file
     * @param inpath	Input path
     * @param namePrefix	The prefix of the names of graphs
     * @return	Graph Set
     * @throws FileNotFoundException
     */
    private static ArrayList<Graph> loadGraphSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
        ArrayList<Graph> graphSet = new ArrayList<Graph>();
        Scanner scanner = new Scanner(inpath.toFile());
        Graph graph = null;
        while (scanner.hasNextLine()){
            String line = scanner.nextLine().trim();
            if (line.equals("")){
                continue;
            } else if (line.startsWith("t")) {
                String graphId = line.split(" ")[2];
                if (graph != null){
                    graphSet.add(graph);
                }
                graph = new Graph(namePrefix + graphId);
            } else if (line.startsWith("v")) {
                String[] lineSplit = line.split(" ");
                int nodeId = Integer.parseInt(lineSplit[1]);
                int nodeLabel = Integer.parseInt(lineSplit[2]);
                graph.addNode(nodeId, nodeLabel);
            } else if (line.startsWith("e")) {
                String[] lineSplit = line.split(" ");
                int sourceId = Integer.parseInt(lineSplit[1]);
                int targetId = Integer.parseInt(lineSplit[2]);
                //int edgeLabel = Integer.parseInt(lineSplit[3]);
                int edgeLabel = 0;
                graph.addEdge(sourceId, targetId, edgeLabel);
            }
        }
        for(int i=0;i<graph.nodes.size();i++){
            int degree=0;
            for (int j=0;j<graph.edges.size();j++){
                if (graph.edges.get(j).source==graph.nodes.get(i)||graph.edges.get(j).target==graph.nodes.get(i)){
                    degree++;
                }
            }
            System.out.println(graph.nodes.get(i).id+" "+degree);

        }
        scanner.close();
        return graphSet;
    }
    private static List<List<Integer>> loadDataSetFromFile(Path inpath, String namePrefix) throws FileNotFoundException{
        List<List<Integer>> result = new ArrayList<>();
        Scanner scanner = new Scanner(inpath.toFile());
        List<Integer> list = new ArrayList<>();
        while (scanner.hasNextLine()){
            String line = scanner.nextLine().trim();
            if (line.equals("")){
                continue;
            } else if (line.startsWith("t")) {
                if (list != null&&list.size()>0){
                    result.add(list);
                }
                list = new ArrayList<>();
            } else {
                String[] lineSplit = line.split(" : ");
                int n = Integer.parseInt(lineSplit[1]);
                list.add(n);

            }
        }
        if (list != null&&list.size()>0){
            result.add(list);
        }
        scanner.close();
        return result;
    }

    private static void printUsage(){
        System.out.println("Usage: -t target_graph_path -q query_graph_path -o output_path");
    }
}