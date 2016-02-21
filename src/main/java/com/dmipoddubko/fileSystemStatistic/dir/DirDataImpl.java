package com.dmipoddubko.fileSystemStatistic.dir;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.apache.commons.io.FileUtils.deleteQuietly;

public class DirDataImpl implements DirData {
    private final static int NUMBER = 3333;

    public void createDir(String path) {
        File file = new File(path);
        file.mkdirs();
    }

    public void createFiles(String path) {
        CreateFileVisitor fileVisitor = new CreateFileVisitor();
        try {
            Files.walkFileTree(Paths.get(path), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Folder error createFiles.", e);
        }
    }

    public void delDir(String path) {
        deleteQuietly(Paths.get(path).toFile());
    }

    public static File prepareFile(StringBuilder sb, String s) {
        return new File(new StringBuilder().append(sb).append(s).toString());
    }

    public static class CreateFileVisitor extends SimpleFileVisitor<Path> {
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            return CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            for (int i = 1; i < NUMBER; i++) {
                StringBuilder sb = new StringBuilder().append(dir).append(File.separator).append("file").append(i).append(".");
                File txt = prepareFile(sb, "txt");
                File xml = prepareFile(sb, "xml");
                File pdf = prepareFile(sb, "pdf");
                File xls = prepareFile(sb, "xls");
                File avi = prepareFile(sb, "avi");
                txt.getParentFile().mkdir();
                xml.getParentFile().mkdir();
                pdf.getParentFile().mkdir();
                xls.getParentFile().mkdir();
                avi.getParentFile().mkdir();
                try {
                    txt.createNewFile();
                    xml.createNewFile();
                    pdf.createNewFile();
                    xls.createNewFile();
                    avi.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException("Some error with creating files in folder.", e);
                }
            }
            return CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return CONTINUE;
        }
    }
}
