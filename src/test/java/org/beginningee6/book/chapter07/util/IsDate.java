package org.beginningee6.book.chapter07.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * 
 * テストで値の等価性を検証するassertThatメソッドで、
 * Date型の値の等価性を年・月・日のみで判定できるように
 * するためのカスタムマッチャ
 *
 */
public class IsDate extends BaseMatcher<Date> {
	
	private final Date expected;
	
	Object actual;
	
	public static Matcher<Date> dateOf(Date expected) {
		return new IsDate(expected);
	}
	
	IsDate(Date expected) {
		this.expected = expected;
	}
	
	public boolean matches(Object actual) {
		this.actual = actual;
		if (!(actual instanceof Date))
			return false;
		
		Calendar actualCal = Calendar.getInstance();
		actualCal.setTime((Date)actual);
		Calendar expectedCal = Calendar.getInstance();
		expectedCal.setTime(expected);
		
		if (actualCal.get(Calendar.YEAR) != expectedCal.get(Calendar.YEAR)) 
			return false;

		if (actualCal.get(Calendar.MONTH) != expectedCal.get(Calendar.MONTH)) 
			return false;

		if (actualCal.get(Calendar.DATE) != expectedCal.get(Calendar.DATE)) 
			return false;

		return true;
	}

	public void describeTo(Description desc) {
		desc.appendValue(new SimpleDateFormat("yyyy/MM/dd").format(expected));
		if (actual != null) {
			desc.appendText(" but actual is ");
			desc.appendValue(new SimpleDateFormat("yyyy/MM/dd").format((Date)actual));
		}
	}

}
