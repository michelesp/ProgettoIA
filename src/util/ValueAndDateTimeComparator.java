package util;

import java.util.Comparator;

public class ValueAndDateTimeComparator implements Comparator<ValueAndDateTime> {

	@Override
	public int compare(ValueAndDateTime o1, ValueAndDateTime o2) {
		return o2.getDateTime().compareTo(o1.getDateTime());
	}

}
