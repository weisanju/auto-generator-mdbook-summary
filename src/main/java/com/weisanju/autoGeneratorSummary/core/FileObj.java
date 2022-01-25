package com.weisanju.autoGeneratorSummary.core;

import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Data
public abstract class FileObj {
    protected File realFile;
    protected List<FileObj> children;
    protected FileObj parent;
   private static String [] staticFilter = {".git","images"};

    public FileObj(File realFile) {
        this.realFile = realFile;
        if(realFile.isDirectory()){
            File[] list = realFile.listFiles();
            if(list == null){
                children = new ArrayList<>();
                return;
            }
            if (this.getClass() == RootFileObj.class) {
                //查找过滤文件
                File ignore = new File(realFile, ".gitignore");
                Set<String> filter = new HashSet<>();
                try {
                    filter.addAll(Files.lines(ignore.toPath()).
                            collect(
                                    Collectors.toSet()
                            ));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                filter.addAll(Arrays.asList(staticFilter));
                children = Arrays.stream(list).filter(e -> {
                    return (!e.isHidden()) && e.isDirectory() && (!e.getName().startsWith(".")) &&
                            filter.stream().noneMatch(x -> x.contains(e.getName()));
                }).map(e -> {
                    FirstFileObj firstFileObj = new FirstFileObj(e);
                    firstFileObj.setParent(this);
                    return firstFileObj;
                }).collect(Collectors.toList());
                return;
            }
            children = Arrays.stream(list).map(e->{

                if(e.isDirectory()){
                    DirWithPointObj dirWithPointObj = new DirWithPointObj(e);
                    dirWithPointObj.setParent(this);
                    return dirWithPointObj;
                }
                FileWithPointObj fileWithPointObj = new FileWithPointObj(e);
                fileWithPointObj.setParent(this);
                return fileWithPointObj;
                }
            ).collect(Collectors.toList());
        }else{
            children = new ArrayList<>();
        }
    }

    public boolean isIndexMd(){
        return getRealFile().getName().equalsIgnoreCase("README.md");
    }


    public static String repeatString(String str, int n, String seg) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(str).append(seg);
        }
        return sb.substring(0, sb.length() - seg.length());
    }



    public FileObj() {
    }

    abstract String write(int depth);

    protected String getRelativePath(){
        if(this.parent == null)
            return getRealFile().getName();

        return this.parent.getRelativePath()+"/"+getRealFile().getName();
    }
    public String getReadMeFile() {
        return getChildren().stream().filter(FileObj::isIndexMd).findFirst().map(FileObj::getRelativePath).orElse("");
    }
}
