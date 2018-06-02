package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

/**
 * We only invest when the dax increased for the last 3 days
 * @author eric
 *
 */
public class After3IncreasesStrategy implements Strategy{
	public boolean invest(List<DaxData> dax) {
		if (dax.size() > 3) {
			int t = dax.size() - 1;
			if (   (dax.get(t).getOpen() > dax.get(t - 1).getOpen()) 
				&& (dax.get(t - 1).getOpen() > dax.get(t - 2).getOpen())
				&& (dax.get(t - 2).getOpen() > dax.get(t - 3).getOpen())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
}
