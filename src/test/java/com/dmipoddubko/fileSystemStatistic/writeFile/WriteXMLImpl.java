package com.dmipoddubko.fileSystemStatistic.writeFile;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class WriteXMLImpl implements WriteFile{
    public void doFile(String path) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("company");
            doc.appendChild(rootElement);
            Element staff = doc.createElement("Staff");
            rootElement.appendChild(staff);
            Attr attr = doc.createAttribute("id");
            attr.setValue("1");
            staff.setAttributeNode(attr);
            Element firstName = doc.createElement("firstname");
            firstName.appendChild(doc.createTextNode("Vasya"));
            staff.appendChild(firstName);
            Element lastName = doc.createElement("lastname");
            lastName.appendChild(doc.createTextNode("Pupkin"));
            staff.appendChild(lastName);
            Element nickname = doc.createElement("nickname");
            nickname.appendChild(doc.createTextNode("pupik"));
            staff.appendChild(nickname);
            Element salary = doc.createElement("salary");
            salary.appendChild(doc.createTextNode("100$"));
            staff.appendChild(salary);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | TransformerException e) {
            throw new RuntimeException("Some error with writing xml files in folder.", e);
        }
    }
}
