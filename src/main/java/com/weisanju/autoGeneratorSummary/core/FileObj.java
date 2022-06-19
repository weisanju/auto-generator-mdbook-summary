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

    private static String[] staticFilter = {".git", "images"};

    public FileObj(File realFile, FileObj parent) {
        this.realFile = realFile;
        this.parent = parent;


        if (realFile.isDirectory()) {
            File[] list = realFile.listFiles();
            if (list == null) {
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
                }).sorted(Comparator.comparing(FirstFileObj::getOrder)).collect(Collectors.toList());
            } else {
                //排序规则：
                //  1. 目录与文件：目录优先
                //  2. 目录与目录：
                // 都不存在排序：字母顺序排序
                // 都存在排序： 排序号优先
                // 不存在排序优先

                children = Arrays.stream(list).filter(e -> {
                    return (!e.isHidden()) && filter.stream().noneMatch(x -> x.contains(e.getName()));
                }).sorted((left, right) -> {
                    boolean directory1 = left.isDirectory();
                    boolean directory2 = right.isDirectory();
                    //目录优先
                    if ((directory1 && !directory2)) {
                        return 1;
                    }
                    if ((directory2 && !directory1)) {
                        return -1;
                    }

                    String leftName = left.getName();
                    String rightName = right.getName();
                    if (!directory1) {
                        int i = leftName.lastIndexOf('.');
                        if (i > -1) {
                            leftName = leftName.substring(0, i);
                        }
                        i = rightName.lastIndexOf('.');
                        if (i > -1) {
                            rightName = rightName.substring(0, i);
                        }

                    }
                    int i1 = leftName.indexOf('.');
                    int i2 = rightName.indexOf('.');
                    int ret = ((i1 == -1 || !isNumeric(leftName.substring(0, i1))) ? 1 : 0) +
                            ((i2 == -1 || !isNumeric(rightName.substring(0, i2))) ? 2 : 0);
                    if (ret > 0) {
                        if (ret != 3) {
                            return ret == 1 ? 1 : -1;
                        }
                        return leftName.compareTo(rightName);
                    }
                    return Integer.parseInt(leftName.substring(0, i1)) - Integer.parseInt(rightName.substring(0, i2));
                }).map(e -> {
                            if (e.isDirectory()) {
                                return new DirWithPointObj(e, this);
                            }
                            return new FileWithPointObj(e, this);
                        }
                ).collect(Collectors.toList());
            }
        } else {
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
        if (this.parent != null) {
            filter.addAll(this.parent.getFilter());
        }
        this.filter = filter;
    }

    public boolean isIndexMd() {
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

    protected String getRelativePath() {
        if (this.parent == null)
            return getRealFile().getName();

        return this.parent.getRelativePath() + "/" + getRealFile().getName();
    }

    public String getReadMeFile() {
        return getChildren().stream().filter(FileObj::isIndexMd).findFirst().map(FileObj::getRelativePath).orElse("");
    }

    public Set<String> getFilter() {
        return this.filter;
    }

    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
