package com.weisanju.autoGeneratorSummary.core;

import java.io.File;

public class FileWithPointObj extends FileObj{
    public FileWithPointObj(File e) {
        super(e);
    }

    @Override
    String write(int depth) {
        if(isIndexMd()){
            return "";
        }
        String name = getRealFile().getName();
        int i = name.lastIndexOf(".");
        if(i < 0){
            i = name.length();
        }
        return repeatString("  ", depth+1, "")+"* [" + name.substring(0,i) + "](" + getRelativePath() + ")" + System.lineSeparator();
    }

}
