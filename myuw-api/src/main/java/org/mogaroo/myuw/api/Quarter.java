package org.mogaroo.myuw.api;

public class Quarter {
	
	private int _year;
	private Season _season;
	
	public Quarter(int year, Season season) {
		_year = year;
		_season = season;
	}
	
	public int getYear() {
		return _year;
	}

	public Season getSeason() {
		return _season;
	}

	public enum Season {
		FALL,
		WINTER,
		SPRING,
		SUMMER
	}
}
