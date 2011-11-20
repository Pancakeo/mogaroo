package org.mogaroo.myuw.api;

import org.w3c.dom.Document;

public class MyUWServiceResponse {
	
	private int _responseCode;
	private Document _responseDoc;
	
	public MyUWServiceResponse(int responseCode, Document responseDoc) {
		_responseCode = responseCode;
		_responseDoc = responseDoc;
	}
	
	public int getResponseCode() {
		return _responseCode;
	}

	public Document getResponseEntity() {
		return _responseDoc;
	}

}
