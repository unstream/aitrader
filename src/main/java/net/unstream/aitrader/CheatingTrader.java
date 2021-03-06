package net.unstream.aitrader;

import net.unstream.aitrader.strategies.CheatingStrategy;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

;


public class CheatingTrader {
	private void runDax() throws IOException {
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("GDAXI.csv");

		Account myAccount = Account.builder()
				.cash(1000)
				.strategy(new CheatingStrategy())
				.build();
		
		Reader in = new InputStreamReader(is);
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
		List<DaxData> dax = new ArrayList<DaxData>();
		for (CSVRecord record : records) {
			
			tradeForTheDay(myAccount, dax);
			
			String dateText = record.get("Date");
		    String open = record.get("Open");
		    SimpleDateFormat parser=new SimpleDateFormat("yyyy-MM-dd");
		    try {
				dax.add(DaxData.builder()
						.date(parser.parse(dateText))
						.open(Double.parseDouble(open))
						.build()
				);
			} catch (NumberFormatException | ParseException e) {
				continue;
			}
		    
			updateAccount(myAccount, dax);
			System.out.println("Dax: " + dax.get(dax.size() - 1).getOpen());
			System.out.println("Balance: " + myAccount.balance());
		    
		}
	}

	public void updateAccounOold(Account account, List<DaxData> dax) {
		if (dax.size() > 1) {
			account.setShares(
					account.getShares() * dax.get(dax.size() - 1).getOpen()
							/ dax.get(dax.size() - 2).getOpen());
		}
	}

	public void updateAccount(Account account, List<DaxData> dax) {
		if (dax.size() > 2) {
			account.setShares(
					account.getShares() * dax.get(dax.size() - 2).getOpen()
							/ dax.get(dax.size() - 3).getOpen());
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
		CheatingTrader ai = new CheatingTrader();
		ai.runDax();
	}

}
