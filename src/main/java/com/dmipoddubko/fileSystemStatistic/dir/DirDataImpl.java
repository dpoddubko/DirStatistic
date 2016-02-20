package com.dmipoddubko.fileSystemStatistic.dir;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;

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

    public void delFiles(String path) {
        DeleteFileVisitor fileVisitor = new DeleteFileVisitor();
        try {
            Files.walkFileTree(Paths.get(path), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Folder error createFiles.", e);
        }
    }

    public void delDir(String path) {
        try {
            FileUtils.deleteDirectory(new File(path));
        } catch (IOException e) {
            throw new RuntimeException("Some error with deleting folder.", e);
        }
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

    public static class DeleteFileVisitor extends SimpleFileVisitor<Path> {
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            return CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            for (int i = 1; i < NUMBER; i++) {
                StringBuilder sb = new StringBuilder().append(dir).append(File.separator).append("file").append(i).append(".");
                File txt = new File(new StringBuilder().append(sb).append("txt").toString());
                File xml = new File(new StringBuilder().append(sb).append("xml").toString());
                File pdf = new File(new StringBuilder().append(sb).append("pdf").toString());
                File xls = new File(new StringBuilder().append(sb).append("xls").toString());
                File avi = new File(new StringBuilder().append(sb).append("avi").toString());
                try {
                    Files.deleteIfExists(txt.toPath());
                    Files.deleteIfExists(xml.toPath());
                    Files.deleteIfExists(xls.toPath());
                    Files.deleteIfExists(pdf.toPath());
                    Files.deleteIfExists(avi.toPath());
                } catch (IOException e) {
                    throw new RuntimeException("Some error with deleting files.", e);
                }
            }
            return CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return CONTINUE;
        }
    }
}
