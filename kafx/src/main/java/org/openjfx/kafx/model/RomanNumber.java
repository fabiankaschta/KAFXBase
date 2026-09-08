package org.openjfx.kafx.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RomanNumber implements Serializable {

	private static final long serialVersionUID = -5439094746331659243L;

	private final String roman;
	private final int integer;

	private RomanNumber(String roman, int integer) {
		this.roman = roman;
		this.integer = integer;
	}

	@Override
	public String toString() {
		return this.roman + '(' + this.integer + ')';
	}

	@Override
	public int hashCode() {
		return this.roman.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RomanNumber)) {
			return false;
		} else {
			RomanNumber r = (RomanNumber) obj;
			return this.integer == r.integer;
		}
	}

	public String getString() {
		return roman;
	}

	public int getInt() {
		return integer;
	}

	public final static Pattern REGEX = Pattern
			.compile("(?=[MDCLXVI])M*(C[MD]|D?C{0,3})(X[CL]|L?X{0,3})(I[XV]|V?I{0,3})");
	public final static Pattern REGEX_LOWERCASE = Pattern.compile(REGEX.pattern().toLowerCase());
	private final static Map<Character, Integer> map = new HashMap<>();
	private final static int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
	private final static String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

	static {
		map.put('I', 1);
		map.put('V', 5);
		map.put('X', 10);
		map.put('L', 50);
		map.put('C', 100);
		map.put('D', 500);
		map.put('M', 1000);
	}

	public static RomanNumber valueOf(String value) {
		int total = 0;
		int prevValue = 0;
		value = value.toUpperCase();

		for (int i = value.length() - 1; i >= 0; i--) {
			Integer v = map.get(value.charAt(i));
			if (v == null) {
				throw new UnsupportedOperationException("unknown charakter " + value.charAt(i));
			}
			if (v < prevValue) {
				total -= v;
			} else {
				total += v;
			}
			prevValue = v;
		}
		return new RomanNumber(value, total);
	}

	public static RomanNumber valueOf(int value) {
		if (value <= 0) {
			throw new UnsupportedOperationException("romans did not know of numbers less or equal to zero");
		} else {
			StringBuilder result = new StringBuilder();
			for (int i = 0; i < values.length; i++) {
				while (value >= values[i]) {
					value -= values[i];
					result.append(symbols[i]);
				}
			}
			return new RomanNumber(result.toString(), value);
		}
	}

}
