package org.mogaroo.myuw.api.model;

public class ScheduleLineNumber {
	
	private int _slnValue;
	
	protected ScheduleLineNumber(int slnValue) {
		_slnValue = slnValue;
	}
	
	public int getValue() {
		return _slnValue;
	}
}
