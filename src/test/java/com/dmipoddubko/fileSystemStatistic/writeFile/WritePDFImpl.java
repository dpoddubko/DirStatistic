package com.dmipoddubko.fileSystemStatistic.writeFile;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class WritePDFImpl implements WriteFile {
    public void doFile(String path) {
        Document document = new Document();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            document.add(new Paragraph("Let’s start writing our example codes with customary “Hello World” application. In this application, I will create a PDF file with a single statement in content."));
            document.close();
            writer.close();
        } catch (DocumentException | FileNotFoundException e) {
            throw new RuntimeException("Some error with writing pdf files in folder.", e);
        }
    }
}
