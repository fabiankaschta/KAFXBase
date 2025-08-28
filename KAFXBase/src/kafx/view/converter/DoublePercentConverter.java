package kafx.view.converter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javafx.util.StringConverter;

public class DoublePercentConverter extends StringConverter<Double> {

	private final DecimalFormat decimalFormat;

	public DoublePercentConverter(int fractionDigits) {
		this.decimalFormat = new DecimalFormat("#0.###");
		this.decimalFormat.setMinimumFractionDigits(fractionDigits);
		this.decimalFormat.setMaximumFractionDigits(fractionDigits);
		this.decimalFormat.setPositiveSuffix("%");
		this.decimalFormat.setNegativeSuffix("%");
	}

	public DecimalFormat getDecimalFormat() {
		return this.decimalFormat;
	}

	@Override
	public String toString(Double object) {
		return decimalFormat.format(object * 100.0);
	}

	@Override
	public Double fromString(String string) {
		try {
			ParsePosition pp = new ParsePosition(0);
			Double result = decimalFormat.parse(string, pp).doubleValue() / 100.0;
			if (pp.getIndex() == string.length()) {
				return result;
			} else {
				throw new ParseException("parsing did not use full string", pp.getIndex());
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
