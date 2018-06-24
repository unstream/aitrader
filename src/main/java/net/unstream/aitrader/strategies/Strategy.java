package net.unstream.aitrader.strategies;

import net.unstream.aitrader.DaxData;

import java.util.List;

public interface Strategy {
	boolean invest(List<DaxData> dax);
}
