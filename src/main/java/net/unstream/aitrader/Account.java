package net.unstream.aitrader;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.unstream.aitrader.strategies.Strategy;

@Data
@Builder
@Getter
@Setter
public class Account {
	private double shares;
	private double cash;
	private Strategy strategy;
	
	public double balance() {
		return cash + shares;
	}

	public void invest() {
		shares = shares + cash;
		cash = 0;
	}

	public void cashout() {
		cash = cash + shares;
		shares = 0;
	}
}
