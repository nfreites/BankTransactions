import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TransactionSettingsTest {
	
	private TransactionsSettings transactionsSettings;
	
	// Input fields
	private TransactionHistory transactionHistory;
	
	List<BankTransaction> transactionsIban1;
	List<BankTransaction> transactionsIban2;
	
	private String reference = "ref1234";
	private String iban = "ES240000111122223333";
	private Double amount = 100.0;
	private Double fee = 5.0;
	private Date date = new Date(System.currentTimeMillis());
	private String description = "description";
	
	@Before
	public void setObjects() {
		transactionsSettings = new TransactionsSettings();
		
		transactionHistory = new TransactionHistory();
		
		transactionsIban1 = new ArrayList<>();
		transactionsIban2 = new ArrayList<>();
		
		BankTransaction bankTransaction1 = new BankTransaction();
		bankTransaction1.setReference(reference + "1");
		bankTransaction1.setAccount_iban(iban + "1");
		bankTransaction1.setAmount(amount + 10.0);
		bankTransaction1.setFee(fee + 1.0);
		bankTransaction1.setDate(date);
		bankTransaction1.setDescription(description + "1");
		BankTransaction bankTransaction2 = new BankTransaction();
		bankTransaction2.setReference(reference + "2");
		bankTransaction2.setAccount_iban(iban + "2");
		bankTransaction2.setAmount(amount + 100.0);
		bankTransaction2.setFee(fee + 10.0);
		bankTransaction2.setDate(date);
		bankTransaction2.setDescription(description + "2");
		BankTransaction bankTransaction3 = new BankTransaction();
		bankTransaction3.setReference(reference + "3");
		bankTransaction3.setAccount_iban(iban + "3");
		bankTransaction3.setAmount(amount + 1000.0);
		bankTransaction3.setFee(fee + 100.0);
		bankTransaction3.setDate(date);
		bankTransaction3.setDescription(description + "3");
		
		transactionsIban1.add(bankTransaction1);
		transactionsIban1.add(bankTransaction2);
		transactionsIban2.add(bankTransaction3);
		
		Map<String, List<BankTransaction>> transactionMap = new HashMap<>();
		transactionMap.put(bankTransaction1.getAccount_iban(), transactionsIban1);
		transactionMap.put(bankTransaction2.getAccount_iban(), transactionsIban2);
		transactionHistory.setBankTransactionsPerAccount(transactionMap);
	}
	
	@Test
	public void fillTransactionHistoryTest() {
		
		Map<String, List<BankTransaction>> transaction = transactionsSettings.fillTransactionHistory(transactionsIban1);
		
		assertEquals("The transaction should have been set as a property of transaction settings", transactionsSettings.getTransactionHistory().getBankTransactionsPerAccount().get(iban+1).get(0).getReference(),
				reference + "1");
	}
	
	@Test(expected = NegativeAccountException.class)
	public void verifyNegativeAccountTest() throws NegativeAccountException {
		
		TransactionDTOIn transactionDTOIn = new TransactionDTOIn();
		transactionDTOIn.setAccount_iban(iban);
		transactionDTOIn.setAmount(amount-100.0);
		transactionDTOIn.setFee(fee+1000.0);
		transactionDTOIn.setDate(date);
		transactionDTOIn.setDescription(description);
		transactionDTOIn.setReference(reference);
		
		TransactionsSettings.verifyNegativeAccount(5.0, transactionDTOIn);
	}
	
	@Test
	public void searchTransactionTest() {
		
		Map<String, List<BankTransaction>> transactions = transactionsSettings.searchTransactions(iban + "1");
		
		assertEquals("The transaction should have been return with its specific reference.", transactions.get(iban + "1").get(0).getReference(), reference + "1");
	}
	
	@Test
	public void transactionStatusTest() {
		
		BankTransaction trans = transactionsSettings.transactionStatus(reference, "CLIENT");
		
		assertNotNull("", trans.getStatus());
	}
}
