package net.unstream.aitrader.strategies;

import java.util.List;

import net.unstream.aitrader.DaxData;

/**
 * see
 * https://www.brokerdeal.de/blog/trading-mit-zwei-indikatoren-simpel-und-doch-
 * profitabel
 * http://www.great-trades.com/Help/bollinger%20bands%20calculation.htm
 * https://www.boerse.de/technische-indikatoren/Bollinger-Bands-3
 * 
 * @author FER
 *
 */

public class TechnicalAnalysisStrategy implements Strategy {
	public boolean invest(List<DaxData> dax) {

		int numberOfBollingBandValues = 4;

		if (dax.size() > numberOfBollingBandValues + 1) {
			int t = dax.size() - 1;

			// calculate moving average
			int count = t - numberOfBollingBandValues;
			double movingAverage = 0;
			while (count < t) {
				movingAverage = movingAverage + dax.get(count).getOpen();
				count++;
			}
			movingAverage = movingAverage / numberOfBollingBandValues;

			// calculate squares
			count = t - numberOfBollingBandValues;
			double square = 0;
			while (count < t) {
				square = square + Math.pow((dax.get(count).getOpen() - movingAverage), 2);
				count++;
			}

			square = square / numberOfBollingBandValues;

			double deviationValue = Math.sqrt(square);

			double upperBollingBand = movingAverage + (2 * square);
			double middleBollingBand = movingAverage;
			double lowerBollingBand = movingAverage - (2 * square);

			System.out.println("moving average: " + movingAverage);
			System.out.println("upperBollingBand: " + upperBollingBand);
			System.out.println("lowerBollingBand: " + lowerBollingBand);
			System.out.println("middleBollingBand: " + middleBollingBand);

			//if open under lowerBollingBand or very close too => crash, pause for 30 days trade

			if (dax.get(count).getOpen()>lowerBollingBand+12) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

}
