package com.dmipoddubko.fileSystemStatistic.visit;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.FileVisitResult.CONTINUE;

public class VisitFolderImpl implements VisitFolder {

    public List<FolderData> visit(String defaultPath) {
        InsertFileVisitor fileVisitor = new InsertFileVisitor();
        try {
            Files.walkFileTree(Paths.get(defaultPath), fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException("Folder error visit.", e);
        }
        return fileVisitor.data;
    }

    public static class InsertFileVisitor extends SimpleFileVisitor<Path> {
        private List<FolderData> data;

        public InsertFileVisitor() {
            this.data = new ArrayList<>();
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
            if (attr.isRegularFile()) {
                data.add(new FolderDataImpl(file.toFile().getName(), file.toFile().getPath(), "file", file.toFile().length()));
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            data.add(new FolderDataImpl(dir.toFile().getName(), dir.toFile().getPath(), "folder", FileUtils.sizeOfDirectory(dir.toFile())));
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            return CONTINUE;
        }
    }
}
