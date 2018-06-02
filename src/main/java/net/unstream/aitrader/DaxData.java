package net.unstream.aitrader;

import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.Builder;

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
