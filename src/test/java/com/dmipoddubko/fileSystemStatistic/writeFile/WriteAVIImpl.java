package com.dmipoddubko.fileSystemStatistic.writeFile;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class WriteAVIImpl implements WriteFile {

    public void doFile(String path) {
        File srcFile = new File("C:\\Users\\dpoddubko\\DirStatistic\\KnockYouOut.avi");
        File dest = new File(path);
        try {
            FileUtils.copyFileToDirectory(srcFile, dest);
        } catch (IOException e) {
            throw new RuntimeException("Some error with coping avi files in folder.", e);
        }
    }
}
