package com.univocity.freecommerce.utils;

import java.time.*;
import java.util.concurrent.*;

public class TimeInterval {

	public static final TimeInterval MINUTE = minutes(1);
	public static final TimeInterval HOUR = hours(1);

	public final long duration;
	public final TimeUnit unit;
	public final long ms;
	private String unitStr;

	private TimeInterval(long duration, TimeUnit unit) {
		this.duration = duration;
		this.unit = unit;
		this.ms = unit.toMillis(duration);
		unitStr = getUnitStr(unit);

	}

	public static String getUnitStr(TimeUnit unit) {
		switch (unit) {
			case DAYS:
				return "d";
			case HOURS:
				return "h";
			case MINUTES:
				return "m";
			case SECONDS:
				return "s";
			case MILLISECONDS:
				return "ms";
		}
		return "";
	}

	public static TimeInterval millis(long duration) {
		return new TimeInterval(duration, TimeUnit.MILLISECONDS);
	}

	public static TimeInterval seconds(long duration) {
		return new TimeInterval(duration, TimeUnit.SECONDS);
	}

	public static TimeInterval minutes(long duration) {
		return new TimeInterval(duration, TimeUnit.MINUTES);
	}

	public static TimeInterval hours(long duration) {
		return new TimeInterval(duration, TimeUnit.HOURS);
	}

	public static TimeInterval days(long duration) {
		return new TimeInterval(duration, TimeUnit.DAYS);
	}

	public static TimeInterval weeks(long duration) {
		return new TimeInterval(duration * 7, TimeUnit.DAYS);
	}

	public static TimeInterval months(long duration) {
		double minutesInMonth = 525600.0 / 12.0;
		return new TimeInterval((long) (duration * minutesInMonth), TimeUnit.MINUTES);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TimeInterval that = (TimeInterval) o;

		return ms == that.ms;
	}

	@Override
	public int hashCode() {
		return (int) (ms ^ (ms >>> 32));
	}

	@Override
	public String toString() {
		return duration + unitStr;
	}

	public static TimeInterval fromString(String interval) {
		if (interval.toLowerCase().endsWith("ms")) {
			interval = interval.substring(0, interval.length() - 2);
			return minutes(Long.parseLong(interval));
		} else {
			char unit = interval.toLowerCase().charAt(interval.length() - 1);
			interval = interval.substring(0, interval.length() - 1);
			long duration = Long.parseLong(interval);
			switch (unit) {
				case 'd':
					return days(duration);
				case 'h':
					return hours(duration);
				case 'm':
					return minutes(duration);
				case 's':
					return seconds(duration);
			}
			throw new IllegalArgumentException("Unparseable interval: " + interval);
		}
	}

	public static String getFormattedDuration(TimeInterval interval) {
		return getFormattedDuration(interval.ms);
	}

	public static String getFormattedDurationShort(TimeInterval interval) {
		return getFormattedDurationShort(interval.ms);
	}

	public static String getFormattedDurationShort(long ms) {
		int[] duration = getDurationComponents(ms);
		return duration[0] + append(duration[1]) + append(duration[2]);
	}

	private static String append(long duration) {
		if (duration < 10) {
			return ":0" + duration;
		}
		return ":" + duration;
	}

	public static String getFormattedDuration(long ms) {
		int[] duration = getDurationComponents(ms);

		int hours = duration[0];
		int minutes = duration[1];
		int seconds = duration[2];

		if (hours > 0) {
			if (minutes == 0) {
				return pluralize("hour", hours);
			} else {
				return pluralize("hour", hours) + " and " + pluralize("minute", minutes);
			}
		}
		if (minutes > 0) {
			if (seconds == 0) {
				return pluralize("minute", minutes);
			} else {
				return pluralize("minute", minutes) + " and " + pluralize("second", seconds);
			}
		}
		return pluralize("second", seconds);
	}

	private static String pluralize(String word, int len) {
		if (len != 1) {
			return len + " " + word + 's';
		}
		return len + " " + word;
	}

	private static int[] getDurationComponents(long ms) {
		Duration duration = Duration.ofMillis(ms);
		return new int[]{(int) duration.toHours() % 24, (int) duration.toMinutes() % 60, (int) duration.getSeconds() % 60};
	}
}
