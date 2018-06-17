package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

public class ZyclicStrategy implements Strategy {

	// invest on mod 2=0 days and desinvest on mod 2=1 days => 1399€

	// invest on mondays and tuesdays and desinvest on wednesdays, thursday,
	// friday => 1961€

	// invest only on fridays => 2217€

	// invest only on january, february, march, april, may, june (mod 125) and
	// november and december => 14.527€

	// invest only in november and december => 3.218€

	// only not invest in november and december => 3.953€

	// only invest in first six month and in november and december => 46.763€

	public boolean invest(List<DaxData> dax) {
		if (dax.size() > 1) {
			int t = dax.size() - 1;
			if (t % 250 >= 210 || t % 250 <= 125 ) {
				return true;
			} else {
				return false;
			}

		} else {
			return false;
		}

	}

}
