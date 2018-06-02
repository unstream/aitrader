package net.unstream.aitrader;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.gen5.api.BeforeAll;
import org.junit.gen5.api.Test;

class AITraderTest {
	private static List<DaxData> dax;
	
	@BeforeAll
	public static void setup() {
		dax = new ArrayList<DaxData>();
		dax.add(DaxData.builder().open(1000).build());
		dax.add(DaxData.builder().open(1100).build());
	}
	
	@Test
	void testOnlyCash() {
		Account a = Account.builder()
				.cash(1000)
				.build();
		AITrader ai = new AITrader();
		ai.updateAccount(a, dax);
		assertEquals(1000.0, a.balance());
	}

	@Test
	void testIncreaseInShareValue() {
		Account a = Account.builder()
				.cash(1000)
				.build();
		a.invest();
		assertEquals(0, a.getCash());
		assertEquals(1000, a.getShares());
		AITrader ai = new AITrader();
		ai.updateAccount(a, dax);
		assertEquals(1100, a.balance());
	}

}
