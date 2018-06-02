package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

public interface Strategy {
	boolean invest(List<DaxData> dax);
}
