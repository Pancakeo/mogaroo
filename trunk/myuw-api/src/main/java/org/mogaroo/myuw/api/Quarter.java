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
		FALL (4),
		WINTER (1),
		SPRING (2),
		SUMMER (3);
		
		private final int _value;
		
		// Give each season a numerical value. Used by registration form.
		private Season(int val) {
			_value = val;
		}
		
		public int getSeasonNumber() {
			return _value;
		}
	}
}
