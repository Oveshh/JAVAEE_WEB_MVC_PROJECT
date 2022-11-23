package com.springmvc.xml;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

//解析xml
public class XmlPaser {
    //base-package
    public static String getBasePackage(String xml){
        SAXReader saxReader =new SAXReader();
        InputStream inputStream=XmlPaser.class.getClassLoader().getResourceAsStream(xml);
        try {
            //XML 文档对象
            Document document=saxReader.read(inputStream);
            Element rootElement=document.getRootElement();
            Element componentScan=rootElement.element("component-scan");
            Attribute attribute=componentScan.attribute("base-package");
            String basePackage=attribute.getText();
            return basePackage;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return "";
    }
}
