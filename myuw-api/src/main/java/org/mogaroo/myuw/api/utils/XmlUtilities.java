package org.mogaroo.myuw.api.utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtilities {
	private static final String ENCODING = "ISO-8859-1";
	
    private XmlUtilities() {

    }

    /**
     * Returns the attribute value of the given attribute of a node.
     * @param node The node to look at
     * @param attribute The attribute name to find
     * @return The value of the attribute, or null if not found
     */
    public static String getAttributeFromNode(Node node, String attribute) {
        NamedNodeMap attributes = node.getAttributes();
        Node nameNode = attributes.getNamedItem(attribute);

        if (nameNode == null) {
            return null;
        }

        return nameNode.getNodeValue();
    }

    /**
     * Returns a nodelist found in the document at the given xPath
     * @param doc Document to search in
     * @param xPath Path to look at
     * @return The nodes at that path
     */
    public static NodeList getNodes(Document doc, String xPath)  {
        NodeList nodes = null;

        try {
            XPath xPathFactory = XPathFactory.newInstance().newXPath();
            nodes = (NodeList)xPathFactory.evaluate(xPath, doc, XPathConstants.NODESET);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return nodes;
    }

    /** Return document from string.  **/
    public static Document getDocumentFromString(String content) {
        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.parse(IOUtils.toInputStream(content, ENCODING));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document getDocumentFromDirtyString(String dirtyXml) {

        try {
            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode rootTag = htmlCleaner.clean(dirtyXml);
            rootTag.setName("qabot_manifest");

            DomSerializer domSerializer = new DomSerializer(htmlCleaner.getProperties());
            Document doc = domSerializer.createDOM(rootTag);
            return doc;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Parses a file to a document.
     * @param f File to parse.
     * @return A document representation of the file, or null if an error occurs.
     */
    public static Document getDocumentFromFile(File f) {

        try {
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return db.parse(f);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the node found in the document at the given xPath
     * @param doc Document to search in
     * @param xPath Path to look at
     * @return The node at that path
     * @throws XPathExpressionException 
     */
    public static Node getNode(Document doc, String xPath) {
        XPath xPathFactory = XPathFactory.newInstance().newXPath();

        try {
            return (Node)xPathFactory.evaluate(xPath, doc, XPathConstants.NODE);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /** Return a new document. **/
    public static Document newDocument() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

}