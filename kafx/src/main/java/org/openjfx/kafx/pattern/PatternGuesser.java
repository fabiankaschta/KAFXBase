package org.openjfx.kafx.pattern;

import java.util.regex.Matcher;

import org.openjfx.kafx.model.RomanNumber;

public class PatternGuesser {

	public static String guessPattern(String current) {
		// try number first
		String number = current.replaceAll("\\D+", "");
		if (number.length() > 0) {
			int numberIndex = current.indexOf(number);
			String before = current.substring(0, numberIndex);
			String after = current.substring(numberIndex + number.length());
			// increase number by 1
			int n = Integer.valueOf(number) + 1;
			return before + n + after;
		}
		// check for uppercase roman numbers
		Matcher matcher = RomanNumber.REGEX.matcher(current);
		if (matcher.find()) {
			// exclude single 'C' here to skip to letters
			if (!matcher.group().equals("C") && !matcher.group().equals("D")) {
				String before = current.substring(0, matcher.start());
				RomanNumber numeral = RomanNumber.valueOf(matcher.group());
				// increase number by 1
				numeral = RomanNumber.valueOf(numeral.getInt() + 1);
				String after = current.substring(matcher.end());
				return before + numeral.getString() + after;
			}
		}
		// check for lowercase roman numbers
		matcher = RomanNumber.REGEX_LOWERCASE.matcher(current);
		if (matcher.find()) {
			// exclude single 'c' and 'd' here to skip to letters
			if (!matcher.group().equals("c") && !matcher.group().equals("d")) {
				String before = current.substring(0, matcher.start());
				RomanNumber numeral = RomanNumber.valueOf(matcher.group());
				// increase number by 1
				numeral = RomanNumber.valueOf(numeral.getInt() + 1);
				String after = current.substring(matcher.end());
				return before + numeral.getString().toLowerCase() + after;
			}
		}
		// check for first letter
		String letterString = current.replaceAll("[^a-zA-Z]", "");
		if (letterString.length() > 0) {
			char firstLetter = letterString.charAt(0);
			if (firstLetter == 'Z' || firstLetter == 'z') {
				return current;
			} else {
				int firstLetterIndex = current.indexOf(firstLetter);
				// increase first letter by 1
				firstLetter++;
				String before = current.substring(0, firstLetterIndex);
				String after;
				if (firstLetterIndex == current.length() - 1) {
					after = "";
				} else {
					after = current.substring(firstLetterIndex + 1);
				}
				return before + firstLetter + after;
			}
		}
		// nothing found, repeat current
		return current;
	}

}
