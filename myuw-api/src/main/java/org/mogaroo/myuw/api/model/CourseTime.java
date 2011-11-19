package org.mogaroo.myuw.api.model;

public class CourseTime {
	
	private Day _day;
	private int _startHour;
	private int _endHour;
	private int _startMinute;
	private int _endMinute;
	
	protected CourseTime(Day day, int startHour, int startMinute,
			int endHour, int endMinute) {
		_day = day;
		_startHour = startHour;
		_startMinute = startMinute;
		_endHour = endHour;
		_endMinute = endMinute;
	}

	public Day getDay() {
		return _day;
	}

	public int getStartHour() {
		return _startHour;
	}

	public int getEndHour() {
		return _endHour;
	}

	public int getStartMinute() {
		return _startMinute;
	}

	public int getEndMinute() {
		return _endMinute;
	}
}
