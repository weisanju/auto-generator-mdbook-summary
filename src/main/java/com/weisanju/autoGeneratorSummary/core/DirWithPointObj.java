package com.weisanju.autoGeneratorSummary.core;

import java.io.File;
public class DirWithPointObj extends FileObj{
    public DirWithPointObj(File e,FileObj parent) {
        super(e,parent);
    }

    @Override
    String write(int depth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(repeatString("  ", depth+1, "")).append("* [").append(getRealFile().getName()).append("](").append(getReadMeFile()).append(")").append(System.lineSeparator());
        for (FileObj child : children) {
            stringBuilder.append(child.write(depth+1));
        }
        return stringBuilder.toString();
    }
}
