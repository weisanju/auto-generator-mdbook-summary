package com.weisanju.autoGeneratorSummary.core;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RootFileObj extends FileObj{

    private String noteName;

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public RootFileObj(File realFile, FileObj parent) {
        super(realFile,parent);
    }

    @Override
    String write(int depth) {
        Map<String, List<FileObj>> collect = getChildren().stream().collect(Collectors.groupingBy(
                e ->((FirstFileObj) (e)).getGroupName(),LinkedHashMap::new,Collectors.toList())
        );

        StringBuilder stringBuilder = new StringBuilder("["+noteName+"](README.md)").append(System.lineSeparator());
        collect.forEach((k,v)->{
            stringBuilder.append(writerH1Header(k));
            for (FileObj fileObj : v) {
                stringBuilder.append(fileObj.write(0));
            }
            stringBuilder.append(System.lineSeparator());
        });
        return stringBuilder.toString();
    }
    public  String writerH1Header(String content){
        return "# " + content + System.lineSeparator();
    }

    @Override
    protected String getRelativePath() {
        return ".";
    }
}
