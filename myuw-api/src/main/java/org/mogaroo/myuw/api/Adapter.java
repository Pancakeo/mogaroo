package org.mogaroo.myuw.api;

public interface Adapter<SRC, DEST> {
	
	public DEST adapt(SRC src) throws AdapterException;
}
