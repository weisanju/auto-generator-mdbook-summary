package com.weisanju.autoGeneratorSummary.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RootFileObj extends FileObj{
    public RootFileObj(File realFile) {
        super(realFile);
    }

    @Override
    String write(int depth) {
        Map<String, List<FileObj>> collect = getChildren().stream().collect(Collectors.groupingBy(e -> ((FirstFileObj) (e)).getGroupName()));
        StringBuilder stringBuilder = new StringBuilder("[我的笔记库](README.md)").append(System.lineSeparator());
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
