package org.mogaroo.myuw.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mogaroo.myuw.api.utils.XmlUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlUtilitiesTest {
	private static final File INPUT_FILE = new File("src/test/resources/ugly.xml");
	
	@Test
	public void testXmlUtilities() throws FileNotFoundException, IOException {
	    Document doc = XmlUtilities.getDocumentFromDirtyString(IOUtils.toString(new FileReader(INPUT_FILE))); 
	    assertNotNull("Document is null.", doc);
	    
	    int testNodes = 23;
	    NodeList nodes = XmlUtilities.getNodes(doc, "//input[@type='hidden']");
	    assertNotNull("Node list is null", nodes);
	    assertEquals("Node list length did not equal expected value. ", testNodes, nodes.getLength());
	    
	    Node node = XmlUtilities.getNode(doc, "//title");
	    assertEquals("Node values are not equal.", "UW NetID Weblogin", node.getTextContent());
	}
	
	@Test
	public void txtXmlUtilitiesAttr() throws Exception {
		Document doc = XmlUtilities.getDocumentFromDirtyString(IOUtils.toString(new FileReader(INPUT_FILE))); 
        assertNotNull("Document is null.", doc);
        
        Node n = XmlUtilities.getNode(doc, "//input[@name='first_kiss']");
        assertNotNull("Node is null.", n);
        
        assertEquals("Attribute values are not equal.", "1321838836-904103", XmlUtilities.getAttributeFromNode(n, "value"));
	}
}