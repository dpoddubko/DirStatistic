package com.dmipoddubko.fileSystemStatistic.dir;

public interface DirData {
    void createDir(String path);

    void createFiles(String path, int depth);

    void delDir(String path, int depth);
}
