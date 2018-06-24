package net.unstream.aitrader;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class DaxData {
	//Date,Open,High,Low,Close,Adj Close,Volume
	private Date date;
	private double open;
	private double high;
	private double low;
	private double close;
	private double volume;
}
