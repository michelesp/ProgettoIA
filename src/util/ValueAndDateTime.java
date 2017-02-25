package util;

import java.time.LocalDateTime;

public class ValueAndDateTime {
	private float value;
	private LocalDateTime dateTime;
	
	public ValueAndDateTime(float value, LocalDateTime dateTime) {
		this.value = value;
		this.dateTime = dateTime;
	}

	public float getValue() {
		return value;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}
	
}
