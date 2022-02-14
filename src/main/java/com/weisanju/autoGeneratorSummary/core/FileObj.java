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

    private Set<String> filter;

    private static String [] staticFilter = {".git","images"};

    public FileObj(File realFile,FileObj parent) {
        this.realFile = realFile;
        this.parent = parent;


        if(realFile.isDirectory()){
            File[] list = realFile.listFiles();
            if(list == null){
                children = new ArrayList<>();
                return;
            }
            //查找过滤文件
            buildFilter(realFile);

            Set<String> filter = this.getFilter();


            if (this.getClass() == RootFileObj.class) {

                children = Arrays.stream(list).filter(e -> {
                    return (!e.isHidden()) && e.isDirectory() && (!e.getName().startsWith(".")) &&
                            filter.stream().noneMatch(x -> x.contains(e.getName()));
                }).map(e -> {
                    return new FirstFileObj(e, this);
                }).collect(Collectors.toList());
            }else{
                //排序规则： 1. 目录优先 2.
                children = Arrays.stream(list).filter(e->{
                    return (!e.isHidden()) && filter.stream().noneMatch(x -> x.contains(e.getName()));
                }).sorted((left,right)->{
                    boolean directory1 = left.isDirectory();
                    boolean directory2 = right.isDirectory();
                    if ((directory1 && !directory2)) {
                        return -1;
                    }
                    if ((directory2 && !directory1)) {
                        return 1;
                    }
                    String leftName = left.getName();
                    String rightName = right.getName();
                    int i1,i2;
                    try {
                        if (!directory1) {
                            //去除文件后缀
                            int f1 = leftName.lastIndexOf('.');
                            int f2 = rightName.lastIndexOf('.');
                            if(f1>0 && f2>0){
                                i1 = leftName.substring(0,f1).indexOf('.');
                                i2 = rightName.substring(0,f2).indexOf('.');
                                if(i1>0 && i2>0){
                                    int order1 = Integer.parseInt(leftName.substring(0, i1));
                                    int order2 = Integer.parseInt(rightName.substring(0, i2));
                                    return  order1-order2;
                                }
                                if(i1>0){
                                    return 1;
                                }
                                if(i2>0){
                                    return -1;
                                }
                            }
                            if(f1>0){
                                return 1;
                            }
                            if(f2>0){
                                return -1;
                            }
                        }else{
                            i1 = leftName.indexOf('.');
                            i2 = rightName.indexOf('.');
                            if(i1>0 && i2>0){
                                int order1 = 0,order2 = 0;
                                boolean order1Number = true;
                                try {
                                    order1 = Integer.parseInt(leftName.substring(0, i1));
                                } catch (NumberFormatException ignore) {
                                    order1Number = false;
                                }

                                boolean order2Number = true;
                                try {
                                    order2 = Integer.parseInt(leftName.substring(0, i2));
                                } catch (NumberFormatException ignore) {
                                    order2Number = false;
                                }

                                if(order1Number && order2Number && order1>=0 && order2>=0 && order1<100 && order2<100){
                                    return  order1-order2;
                                }
                                if(order1Number && order1>=0 && order1<100){
                                    return 1;
                                }
                                if(order2Number && order2>=0 && order2<100){
                                    return -1;
                                }
                            }
                            if(i1>0){
                                return 1;
                            }
                            if(i2>0){
                                return -1;
                            }
                        }
                    } catch (NumberFormatException ignore) {
                    }
                    return leftName.compareTo(rightName);
                }).map(e->{

                            if(e.isDirectory()){
                                return new DirWithPointObj(e, this);
                            }
                    return new FileWithPointObj(e, this);
                        }
                ).collect(Collectors.toList());
            }
        }else{
            children = new ArrayList<>();
        }
    }

    private void buildFilter(File realFile) {
        File ignore = new File(realFile, ".gitignore");
        Set<String> filter = new HashSet<>();
        if (ignore.exists()) {
            try {
                filter.addAll(Files.lines(ignore.toPath()).
                        collect(
                                Collectors.toSet()
                        ));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        filter.addAll(Arrays.asList(staticFilter));
        if (this.parent!=null) {
            filter.addAll(this.parent.getFilter());
        }
        this.filter = filter;
    }

    public boolean isIndexMd(){
        return "README.md".equalsIgnoreCase(getRealFile().getName());
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

    public Set<String> getFilter() {
        return this.filter;
    }
}
