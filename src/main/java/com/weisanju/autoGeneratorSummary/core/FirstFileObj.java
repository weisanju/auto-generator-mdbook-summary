package com.weisanju.autoGeneratorSummary.core;

import lombok.Data;

import java.io.File;
import java.util.Arrays;
@Data
public class FirstFileObj extends FileObj implements DirFileObj{
    protected String groupName = "";
    protected int order;
    protected String contentName;

    public FirstFileObj(File realFile) {
        super(realFile);


        //解析其他
        String name = realFile.getName();
        int i1 = name.indexOf('_');
        //没有则取默认分组为空字符串
        if (i1 < 0) {
            contentName = name;
            return;
        }

        int i2 = name.indexOf('_', i1 + 1);
        if (i2 < 0) {
            groupName = name.substring(0, i1);
            contentName = name.substring(i1 + 1);
            return;
        }
        groupName = name.substring(0, i1);
        try {
            order = Integer.parseInt(name.substring(i1 + 1, i2));
        } catch (NumberFormatException ignored) {
        }
        contentName = name.substring(i2 + 1);
    }

    @Override
    String write(int depth) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(repeatString("  ",depth+1,"")).append("* [").append(getContentName()).append("](").append(getReadMeFile()).append(")").append(System.lineSeparator());
        for (FileObj child : children) {
            stringBuilder.append(child.write(depth+1));
        }
        return stringBuilder.toString();
    }
}
