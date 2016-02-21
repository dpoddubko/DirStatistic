package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.dir.DirDataImpl;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DirTest {
    @Test
    public void createDirTest() {
        DirDataImpl dirData = new DirDataImpl();
        dirData.createDir("C:\\TestDirectory\\1\\2\\3\\4\\5\\6\\7\\8\\9\\10\\11\\12\\13\\14\\15\\16\\17\\18\\19\\20\\21\\22\\23\\24\\25\\26\\27\\28\\29");
        dirData.createFiles("C:\\TestDirectory");
        try {
            long countDir = Files.find(
                    Paths.get("C:\\TestDirectory"), 35,
                    (path, attributes) -> attributes.isDirectory()
            ).count();
            assertEquals(30, countDir);

            long countFiles = Files.find(
                    Paths.get("C:\\TestDirectory"), 35,
                    (path, attributes) -> attributes.isRegularFile()
            ).count();
            assertEquals(499800, countFiles);
            System.out.println(countDir);
            System.out.println(countFiles);
        } catch (IOException e) {
            throw new RuntimeException("Some error with counting sub-folders.", e);
        }
    }

    @Test
    public void tableTest() {
        FileSystemServiceImpl systemService = new FileSystemServiceImpl();
        systemService.getFileDAO().create();
        systemService.index("C:\\TestDirectory");
        List<FolderData> list = systemService.getFileDAO().read();
        assertEquals(499830, list.size());
        systemService.getFileDAO().clean();
        list = systemService.getFileDAO().read();
        assertEquals(0, list.size());
    }

    @Test
    public void deleteDirTest() {
        DirDataImpl dirData = new DirDataImpl();
        String s = "C:\\DirectoryForTest";
        Path path = Paths.get(s);
        assertTrue(Files.exists(path, LinkOption.NOFOLLOW_LINKS));
        dirData.delDir("C:\\DirectoryForTest");
        assertTrue(Files.notExists(path));

    }
}
