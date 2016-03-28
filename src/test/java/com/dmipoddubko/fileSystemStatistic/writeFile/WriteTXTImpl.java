package com.dmipoddubko.fileSystemStatistic.writeFile;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class WriteTXTImpl implements WriteFile{
    public void doFile(String path){
        List<String> lines = Arrays.asList("The first line of this txt file ", "The second line");
        Path file = Paths.get(path);
        try {
            Files.write(file, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException("Some error with writing txt files in folder.", e);
        }
    }
}
