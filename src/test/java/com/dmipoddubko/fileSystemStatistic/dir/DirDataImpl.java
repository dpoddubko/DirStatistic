package com.dmipoddubko.fileSystemStatistic.dir;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.FileVisitResult.CONTINUE;

public class DirDataImpl implements DirData {
    private final static int NUMBER = 3334;

    public void createDir(String path) {
        File file = new File(path);
        file.mkdirs();
    }

    public String buildPath(String path, int depth) {
        StringBuilder prepPath = new StringBuilder().append(path);
        for (int i = 1; i < depth; i++)
            prepPath = prepPath.append(File.separator).append(i);
        return prepPath.toString();
    }

    public static List<String> dividePath(String path, int depth, int treads) {
        List<String> paths = new ArrayList<>();
        StringBuilder prepPath = new StringBuilder().append(path);
        paths.add(prepPath.toString());
        int num = depth / treads;
        for (int i = 1; i < depth; i++) {
            prepPath = prepPath.append(File.separator).append(i);
            if (i % num == 0) paths.add(prepPath.toString());
        }
        return paths;
    }

    public void createFiles(String path) {
        CreateFileVisitor fileVisitor = new CreateFileVisitor();
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(path), opts, 6, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Some error with creating files in folder.", e);
        }
    }

    public void delDir(String path) {
        DeleteFileVisitor fileVisitor = new DeleteFileVisitor();
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(path), opts, 6, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Some error with deleting files.", e);
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
