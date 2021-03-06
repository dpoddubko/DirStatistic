package com.dmipoddubko.fileSystemStatistic;

import com.dmipoddubko.fileSystemStatistic.folderData.FolderData;
import com.dmipoddubko.fileSystemStatistic.folderData.FolderDataImpl;
import com.dmipoddubko.fileSystemStatistic.service.FileSystemServiceImpl;
import com.dmipoddubko.fileSystemStatistic.visit.VisitFolder;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileSystemServiceTest {

    private ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

    @Test
    public void fileSystemServiceTest() {
        PropertyConfigurator.configure("log4j.properties");
        int num = DirTest.count();
        List<FolderData> list = new ArrayList<>();
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500001));
        list.add(new FolderDataImpl("testFile.txt", "C:\\TestDirectory", "file", 123, 500002));
        VisitFolder visitFolder = mock(VisitFolder.class);
        when(visitFolder.visit("path")).thenReturn(list);
        FileSystemServiceImpl service = (FileSystemServiceImpl) context.getBean("fileSystemService");
        service.setVisitFolderImpl(visitFolder);
        service.index("path");
        assertEquals(num + 2, DirTest.count());
    }
}
