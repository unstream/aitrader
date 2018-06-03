package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

/**
 * Indicators for a crash: Huge decline in the last 3 days.
 * Then trading is pause for a while.
 *
 */
public class CrashDetectionStrategy implements Strategy {
	public static int crashCount = 0;
	private final static double CRASH_THRESHOLD = 0.15;
	private final static int CRASH_PAUSE = 30;

	public boolean invest(List<DaxData> dax) {
		for (int i = dax.size() - 1; (i > 4) && (i > dax.size() - CRASH_PAUSE); i--) {
			double current = dax.get(i).getOpen();
			double maxLast3days = Math.max(dax.get(i-1).getOpen(), dax.get(i-2).getOpen());
			maxLast3days = Math.max(maxLast3days, dax.get(i-3).getOpen());
			if (current < maxLast3days * (1 - CRASH_THRESHOLD)) {
				return false;
			}
		}
		return true;
	}
}