package com.weisanju.autoGeneratorSummary.core;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {
        String dirPath = args[0];
        String targetPath;
        if(args.length == 2){
            targetPath = args[1];
        }else{
            targetPath = null;
        }

        File root = new File(dirPath);
        RootFileObj rootFileObj = new RootFileObj(root,null);
        if(targetPath == null){
            System.out.println(rootFileObj.write(0));
        }else{
            File file = new File(targetPath);
            Files.write(file.toPath(),rootFileObj.write(0).getBytes(StandardCharsets.UTF_8));
        }
    }
}
