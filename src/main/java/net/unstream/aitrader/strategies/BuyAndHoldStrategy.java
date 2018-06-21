package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

/**
 * buy all shares on the first day and hold them for the rest of the investment
 * period
 * 
 * @author FER
 *
 */
public class BuyAndHoldStrategy implements Strategy {

	public boolean invest(List<DaxData> dax) {
		return true;
	}

}
