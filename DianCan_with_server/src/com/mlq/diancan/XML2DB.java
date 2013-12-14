package com.mlq.diancan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class XML2DB {
	public DocumentBuilder builder = null;
	public Document document = null;

	public XML2DB() {
		// TODO Auto-generated constructor stub
		try { 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) { 
			System.out.println(e.getMessage()); 
		}
	}
	
	public void parserXml(InputStream ins) { 
		try { 			
			document = builder.parse(ins);
			Element clients = document.getDocumentElement();
			Element client = (Element) clients.getElementsByTagName("table").item(0);
			Element queryframe = (Element) client.getElementsByTagName("frame").item(0);
			String frameName = queryframe.getAttribute("name");
			System.out.println("in parserXml,frameName:" + frameName);
		} catch (FileNotFoundException e) { 
			System.out.println(e.getMessage()); 
		} catch (SAXException e) { 
			System.out.println(e.getMessage()); 
		} catch (IOException e) { 
			System.out.println(e.getMessage()); 
		} 
	}
}
