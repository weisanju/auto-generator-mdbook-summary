package com.weisanju.autoGeneratorSummary.core;

import java.io.File;

public class FileWithPointObj extends FileObj{
    public FileWithPointObj(File e,FileObj parent) {
        super(e,parent);
    }

    @Override
    String write(int depth) {
        if(isIndexMd()){
            return "";
        }
        String name = getRealFile().getName();
        int end = name.lastIndexOf(".");
        if(end < 0){
            end = name.length();
        }
        int start = name.indexOf(".");
        if(start < 0){
            start = 0;
        }else if(start == end ){
            start = 0;
        }else{
            try {
                int i = Integer.parseInt(name.substring(0, start));
                if(i>=100 || i<0){
                    start = 0;
                }else{
                    start = start+1;
                }
            } catch (NumberFormatException ignore) {
                start = 0;
            }
        }
        return repeatString("  ", depth+1, "")+"* [" + name.substring(start,end) + "](" + getRelativePath() + ")" + System.lineSeparator();
    }

}
