package net.unstream.aitrader.strategies;

import net.unstream.aitrader.DaxData;

import java.util.List;

/**
 * Anticyclic on a daily basis.
 * @author eric
 *
 */
public class InverseStrategy implements Strategy {
	public boolean invest(List<DaxData> dax) {
		if (dax.size() > 1) {
			int t = dax.size() - 1;
			if (dax.get(t).getOpen() < dax.get(t - 1).getOpen()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
		
	}
}
