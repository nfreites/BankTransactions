import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TransactionHistoryTest {
	
	private TransactionHistory transactionHistory;
	private List<BankTransaction> transactions;
	
	private String reference = "ref1234";
	private String iban = "ES240000111122223333";
	private Double amount = 100.0;
	private Double fee = 5.0;
	private Date date = new Date(System.currentTimeMillis());
	private String description = "description";
	
	@Before
	public void setObjects() {
		transactionHistory = new TransactionHistory();
		transactions = new ArrayList<>();
		
		BankTransaction trans1 = new BankTransaction();
		trans1.setAccount_iban(iban + "1");
		trans1.setAmount(amount + 1.0);
		trans1.setFee(fee + 1.0);
		trans1.setDate(new Date(System.currentTimeMillis()));
		trans1.setReference(reference + "1");
		BankTransaction trans2 = new BankTransaction();
		trans2.setAccount_iban(iban + "2");
		trans2.setAmount(amount + 10.0);
		trans2.setFee(fee + 2.0);
		trans2.setDate(new Date(System.currentTimeMillis()));
		trans2.setReference(reference + "2");
		BankTransaction trans3 = new BankTransaction();
		trans3.setAccount_iban(iban + "3");
		trans3.setAmount(amount + 100.0);
		trans3.setFee(fee + 3.0);
		trans3.setDate(new Date(System.currentTimeMillis()));
		trans3.setReference(reference + "3");
		
		transactions.add(trans1);
		transactions.add(trans2);
		transactions.add(trans3);
		
		Map<String, List<BankTransaction>> transactionsMap = new HashMap<>();
		for(BankTransaction trans : transactions) {
			if(transactionsMap == null) {
				transactionsMap = new HashMap<>();
			}
			else {
				if(transactionsMap.get(trans.getAccount_iban()) == null) {
					List<BankTransaction> transactions = transactionsMap.get(trans.getAccount_iban());
					transactions = new ArrayList<>();
					transactionsMap.put(transactions.get(0).getAccount_iban(), transactions);
				}
				else {
					transactionsMap.get(trans.getAccount_iban()).add(trans);
				}
			}
		}
	}
	
	@Test
	public void saveBankTransactionsTest() {
		
	}
}
