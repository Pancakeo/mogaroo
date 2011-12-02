package org.mogaroo.myuw.api.model;

public class ScheduleLineNumber {
	
	private int _slnValue;
	
	public ScheduleLineNumber(int slnValue) {
		_slnValue = slnValue;
	}
	
	public int getValue() {
		return _slnValue;
	}
	
	// Added these since we use sets of slns elsewhere.
	@Override
	public int hashCode() {
		return Integer.valueOf(_slnValue).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof ScheduleLineNumber) {
			return _slnValue == ((ScheduleLineNumber)o)._slnValue;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return "" + _slnValue;
	}
}
