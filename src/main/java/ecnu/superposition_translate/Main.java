package ecnu.superposition_translate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final Integer N=999999;
    public static List<String> getFileList(String path) {
        List<String> list = new ArrayList();
        try {
            File file = new File(path);
            String[] filelist = file.list();
            for (int i = 0; i < filelist.length; i++) {
                list.add(path+"\\"+filelist[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void main(String[] args) {
        String inPath = "E:\\github\\quantum_compiler_optim\\examples";//遍历文件夹下的所有.jpg文件
        List<String> files=  getFileList(inPath);
        for (int i=0;i<files.size();i++){
            if (files.get(i)==" "){
                return;
            }
//            File file = new File("E:\\github\\quantum_compiler_optim\\examples\\dist_223.qasm");
            File file = new File(files.get(i));
            BufferedReader reader = null;
            Level[] tower=new Level[N];
            for (int k=0;k<N;k++){
                tower[k]=new Level(k);
            }
            try {
                reader = new BufferedReader(new FileReader(file));
                Translate trans=new Translate();
                System.out.println(files.get(i));
                int total_level = trans.translate(tower,reader);
                System.out.println("\n重新打印代码:\n");
                StringBuffer sb=new StringBuffer();
                sb.append("E:\\github\\quantum_compiler_optim\\examples_result\\");
                String []splits=files.get(i).split("\\\\");

                sb.append(splits[splits.length-1]);
                File outfile = new File(sb.toString());
                if(!file.exists())
                    file.createNewFile();
                FileWriter fw = new FileWriter(sb.toString(), false);
                PrintWriter pw = new PrintWriter(fw);
                System.out.println(total_level);
                for (int j = 1, line = 1; j <= total_level; j++) {
                    Instruction p = tower[j].head.getNext();
                    while (p!=null) {
                       // System.out.println(line+" : ");
                        line++;
                        p.print(pw);
                        //System.out.println("\n");
                        p = p.getNext();
                    }
                }
                pw.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        return ;
    }
}
