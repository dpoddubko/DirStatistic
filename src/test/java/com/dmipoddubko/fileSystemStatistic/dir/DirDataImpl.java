package com.dmipoddubko.fileSystemStatistic.dir;

import com.dmipoddubko.fileSystemStatistic.writeFile.*;

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
    private static int number;

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

    public void createFiles(String path, int depth) {
        CreateFileVisitor fileVisitor = new CreateFileVisitor();
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(path), opts, depth, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Some error with creating files in folder.", e);
        }
    }

    public void writeFiles(String path, int depth) {
        WriteFileVisitor fileVisitor = new WriteFileVisitor();
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(path), opts, depth, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Some error with writing files in folder.", e);
        }
    }

    public void delDir(String path, int depth) {
        DeleteFileVisitor fileVisitor = new DeleteFileVisitor();
        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        try {
            Files.walkFileTree(Paths.get(path), opts, depth, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Some error with deleting files.", e);
        }
    }

    public static File prepareFile(StringBuilder sb, String s) {
        return new File(prepareStr(sb, s));
    }

    public static String prepareStr(StringBuilder sb, String s) {
        return new StringBuilder().append(sb).append(s).toString();
    }

    public static class CreateFileVisitor extends SimpleFileVisitor<Path> {
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            return CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            for (int i = 1; i < number; i++) {
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
            for (int i = 1; i < number; i++) {
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

    public static class WriteFileVisitor extends SimpleFileVisitor<Path> {
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            return CONTINUE;
        }

        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            WriteTXTImpl writeTXT = new WriteTXTImpl();
            WriteXMLImpl writeXML = new WriteXMLImpl();
            WriteXLSImpl writeXLS = new WriteXLSImpl();
            WritePDFImpl writePDF = new WritePDFImpl();
            WriteAVIImpl writeAVI = new WriteAVIImpl();
            for (int i = 1; i < number; i++) {
                StringBuilder sb = new StringBuilder().append(dir).append(File.separator).append("file").append(i).append(".");
                String txt = prepareStr(sb, "txt");
                writeTXT.doFile(txt);
                String xml = prepareStr(sb, "xml");
                writeXML.doFile(xml);
                String xls = prepareStr(sb, "xls");
                writeXLS.doFile(xls);
                String pdf = prepareStr(sb, "pdf");
                writePDF.doFile(pdf);
                String avi = prepareStr(sb, "avi");
                writeAVI.doFile(dir.toString());
                File oldName = new File(new StringBuilder().append(dir).append("\\KnockYouOut.avi").toString());
                File newName = new File(avi);
                oldName.renameTo(newName);
            }
            return CONTINUE;
        }

        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return CONTINUE;
        }
    }

    public static void setNumber(int number) {
        DirDataImpl.number = number;
    }
}
