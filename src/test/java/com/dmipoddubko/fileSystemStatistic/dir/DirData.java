package com.dmipoddubko.fileSystemStatistic.dir;

public interface DirData {
    void createDir(String path);

    void createFiles(String path);

    void delDir(String path);
}
