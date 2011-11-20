package org.mogaroo.myuw.api.adapters;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.mogaroo.myuw.api.Adapter;
import org.w3c.dom.Document;

public abstract class DocumentAdapterBase<DEST> implements Adapter<Document, DEST> {
	
	private XPath _xPath;
	
	public DocumentAdapterBase() {
		_xPath = XPathFactory.newInstance().newXPath();
	}
	
	protected XPath getXPath() {
		return _xPath;
	}
	
	protected String evaluate(String expression, Document doc) throws XPathExpressionException {
		return _xPath.evaluate(expression, doc);
	}
}
