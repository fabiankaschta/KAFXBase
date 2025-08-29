package org.openjfx.kafx.view.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import javafx.util.StringConverter;

public class BigDecimalPercentConverter extends StringConverter<BigDecimal> {

	private static final BigDecimal ONE_HUNDRET = BigDecimal.valueOf(100);

	private final DecimalFormat decimalFormat;

	public BigDecimalPercentConverter(int fractionDigits) {
		this.decimalFormat = new DecimalFormat("#0.###");
		this.decimalFormat.setMinimumFractionDigits(fractionDigits);
		this.decimalFormat.setMaximumFractionDigits(fractionDigits);
		this.decimalFormat.setPositiveSuffix("%");
		this.decimalFormat.setNegativeSuffix("%");
		this.decimalFormat.setParseBigDecimal(true);
		this.decimalFormat.setRoundingMode(RoundingMode.HALF_UP);
	}

	public DecimalFormat getDecimalFormat() {
		return this.decimalFormat;
	}

	@Override
	public String toString(BigDecimal object) {
		return decimalFormat.format(object.multiply(ONE_HUNDRET));
	}

	@Override
	public BigDecimal fromString(String string) {
		try {
			ParsePosition pp = new ParsePosition(0);
			BigDecimal result = ((BigDecimal) decimalFormat.parseObject(string, pp)).divide(ONE_HUNDRET);
			if (pp.getIndex() == string.length()) {
				if (result.scale() <= this.decimalFormat.getMaximumFractionDigits()) {
					if (result.scale() <= this.decimalFormat.getMinimumFractionDigits()) {
						result.setScale(this.decimalFormat.getMinimumFractionDigits());
					}
					return result;
				} else {
					throw new ParseException("parsing did not pass scale requirement", pp.getIndex());
				}
			} else {
				throw new ParseException("parsing did not use full string", pp.getIndex());
			}
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

}
