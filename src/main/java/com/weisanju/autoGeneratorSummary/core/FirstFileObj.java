package com.weisanju.autoGeneratorSummary.core;

import lombok.Data;

import java.io.File;
import java.util.Arrays;

@Data
public class FirstFileObj extends FileObj implements DirFileObj {
    protected String groupName = "";
    protected int order;
    protected String contentName;

    public FirstFileObj(File realFile, FileObj parent) {
        super(realFile, parent);
        String name = realFile.getName();
        int i2 = name.indexOf('.');
        if (i2 > -1) {
            try {
                order = Integer.parseInt(name.substring(0, i2));
            } catch (NumberFormatException ignored) {
            }
            name = name.substring(i2 + 1);
        }
        int i1 = name.indexOf('_');
        if (i1 > -1) {
            contentName = name.substring(i1 + 1);
            groupName = name.substring(0, i1);
        } else {
            contentName = name;
        }

    }

    @Override
    String write(int depth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(repeatString("  ", depth + 1, "")).append("* [").append(getContentName()).append("](").append(getReadMeFile()).append(")").append(System.lineSeparator());
        for (FileObj child : children) {
            stringBuilder.append(child.write(depth + 1));
        }
        return stringBuilder.toString();
    }
}
