package org.mogaroo.myuw.api.model;

public class Department {
	
	private String _departmentName;
	private String _departmentAbbreviation;
	
	public Department(String name, String abbv) {
		_departmentName = name;
		_departmentAbbreviation = abbv;
	}
	
	public String getName() {
		return _departmentName;
	}
	
	public String getAbbreviation() {
		return _departmentAbbreviation;
	}
}
