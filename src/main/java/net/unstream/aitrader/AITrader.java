package net.unstream.aitrader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import net.unstream.aitrader.strategies.After3IncreasesStrategy;
import net.unstream.aitrader.strategies.AfterOneIncreasesStrategy;
import net.unstream.aitrader.strategies.BuyAndHoldStrategy;
import net.unstream.aitrader.strategies.InverseStrategy;
import net.unstream.aitrader.strategies.CrashDetectionStrategy;;

public class AITrader {
	private void runDax() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("GDAXI.csv");
		
		
		File outFile = new File("src/main/resources/result.csv");
		outFile.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));

		CSVPrinter csvPrinter = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader("time", "balance", "dax"));

		Account myAccount = Account.builder().cash(1000).strategy(new BuyAndHoldStrategy()).build();

		Reader in = new InputStreamReader(is);

		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		List<DaxData> dax = new ArrayList<DaxData>();
		int time = 1;

		for (CSVRecord record : records) {

			tradeForTheDay(myAccount, dax);

			String dateText = record.get("Date");
			String open = record.get("Open");
			SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
			try {
				dax.add(DaxData.builder().date(parser.parse(dateText)).open(Double.parseDouble(open)).build());
			} catch (NumberFormatException | ParseException e) {
				continue;
			}

			updateAccount(myAccount, dax);
			System.out.println("Time: "+time);
			System.out.println("Dax: " + dax.get(dax.size() - 1).getOpen());
			csvPrinter.printRecord(time, String.valueOf(myAccount.balance()),dax.get(dax.size() - 1).getOpen());
			System.out.println("Balance: " + myAccount.balance());
			time++;
		}
		
		csvPrinter.flush(); 
	}

	public void updateAccount(Account account, List<DaxData> dax) {
		if (dax.size() > 1) {
			account.setShares(
					account.getShares() * dax.get(dax.size() - 1).getOpen() / dax.get(dax.size() - 2).getOpen());
		}
	}

	public void tradeForTheDay(Account myAccount, List<DaxData> dax) {
		if (myAccount.getStrategy().invest(dax)) {
			myAccount.invest();
		} else {
			myAccount.cashout();
		}
	}

	public static void main(String[] args) throws IOException {
		AITrader ai = new AITrader();
		ai.runDax();
	}

}
