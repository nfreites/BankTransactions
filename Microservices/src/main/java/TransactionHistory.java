import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import exceptions.ValidationException;

public class TransactionHistory {

	public static Map<String, List<BankTransaction>> bankTransactionsPerIban = new HashMap<>();
	private final String REFERENCE = "reference";
	private final String IBAN = "iban";
	private final String DATE = "date";
	private final String AMOUNT = "amount";
	private final String FEE = "fee";
	private final String STATUS = "status";
	
	private final String STATUS_INVALID = "INVALID";
	private final String STATUS_PENDING = "PENDING";
	private final String STATUS_SETTLED = "SETTLED";
	private final String STATUS_FUTURE = "FUTURE";
	
	private final String CHANNEL_CLIENT = "CLIENT";
	private final String CHANNEL_ATM = "ATM";
	private final String CHANNEL_INTERNAL = "INTERNAL";
	
	// 1 day = 24 hour / day * 60 minute / hour * 60 second / minute * 1000 milisecond / second
	private final long DAY_IN_MILISECONDS = 24 * 60 * 60 * 1000;
	
	public TransactionHistory() {
	}
	
	public TransactionHistory(String iban) {
		bankTransactionsPerIban.put(iban, new ArrayList<BankTransaction>());
	}
	
	public void saveBankTransactions(List<BankTransaction> bankTransactions, String iban) {
		if(bankTransactionsPerIban.containsKey(iban)) {
			if(bankTransactionsPerIban.get(iban) == null) {
				bankTransactionsPerIban.put(iban, bankTransactions);
			}
			bankTransactionsPerIban.get(iban).addAll(bankTransactions);
		}
	}
	
	public List<BankTransaction> filterTransaction(String reference, String iban, Date date, Double amount, Double fee, String status) {
		List<BankTransaction> transactions = new ArrayList<>();
		
		if(reference != null) {
			filterTransaction2(reference, REFERENCE);
		}
		else if(iban != null) {
			filterTransaction2(iban, IBAN);
		}
		else if(date != null) {
			filterTransaction2(date, DATE);
		}
		else if(amount != null) {
			filterTransaction2(amount, AMOUNT);
		}
		else if(fee != null) {
			filterTransaction2(fee, FEE);
		}
		else if(status != null) {
			filterTransaction2(status, STATUS);
		}
		else {
			TransactionsSettings.adviceLogger(Level.WARNING, "There are not any field to filter.");
		}
		
		return transactions;
	}
	
	private List<BankTransaction> filterTransaction2(Object filterField, String fieldName) {
		List<BankTransaction> filteredTransactions = new ArrayList<>();
		
		for(List<BankTransaction> transactionsList : bankTransactionsPerIban.values()) {
			for(BankTransaction transaction : transactionsList) {
				if(fieldName.equalsIgnoreCase(REFERENCE) && transaction.getReference().equalsIgnoreCase((String)filterField)) {
					filteredTransactions.add(transaction);
				}
				else if(fieldName != null && fieldName.equalsIgnoreCase(IBAN) && transaction.getAccount_iban().equalsIgnoreCase((String)filterField)) {
					filteredTransactions.add(transaction);
				}
				else if(fieldName != null && fieldName.equalsIgnoreCase(DATE) && transaction.getDate().equals((Date)filterField)) {
					filteredTransactions.add(transaction);
				}
				else if(fieldName != null && fieldName.equalsIgnoreCase(AMOUNT) && transaction.getAmount().equals((Double)filterField)) {
					filteredTransactions.add(transaction);
				}
				else if(fieldName != null && fieldName.equalsIgnoreCase(FEE) && transaction.getFee().equals((Double)filterField)) {
					filteredTransactions.add(transaction);
				}
				else if(fieldName != null && fieldName.equalsIgnoreCase(STATUS) && transaction.getStatus().equalsIgnoreCase((String)filterField)) {
					filteredTransactions.add(transaction);
				}
			}
		}
		
		return filteredTransactions;
	}
	
	public void filterTransactionValidations(String reference, String iban, Date date, Double amount, Double fee, String status) throws ValidationException {
		if(reference != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + REFERENCE + ".");
		}
		else if(iban != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + IBAN + ".");
		}
		else if(date != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + DATE + ".");
		}
		else if(amount != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + AMOUNT + ".");
		}
		else if(fee != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + FEE + ".");
		}
		else if(status != null) {
			TransactionsSettings.adviceLogger(Level.INFO, "The transactions will be filtered by " + STATUS + ".");
		}
		else {
			TransactionsSettings.adviceLogger(Level.WARNING, "There is no field to filter.");
			throw new ValidationException("all the fields");
		}
	}
	
	public static BankTransaction consultStatus(String reference) {
		for(List<BankTransaction> transactionsList : bankTransactionsPerIban.values()) {
			for(BankTransaction transaction : transactionsList) {
				if(transaction.getReference().equals(reference)) {
					return transaction;
				}
			}
		}
		return null;
	}
	
	public BankTransaction getTranssaction(String reference, String channel) {
		BankTransaction transaction = applyBussinessRule(reference, channel);

		if(transaction.getBusinessRule() == 2
				|| transaction.getBusinessRule() == 4
				|| transaction.getBusinessRule() == 6
				|| transaction.getBusinessRule() == 7) {
			transaction.setAmount(transaction.getAmount() - transaction.getFee());
		}
		
		return transaction;
	}
	
	public BankTransaction applyBussinessRule(String reference, String channel) {
		List<BankTransaction> transactions = filterTransaction(reference, null, null, null, null, null);
//		Given: A transaction that is not stored in our system
//		When: I check the status from any channel
//		Then: The system returns the status 'INVALID'
		// BUSINESS RULE 01
		BankTransaction transaction = null;
		if(transactions == null || transactions.isEmpty()) {
			// The transaction is not stored in the system.
			TransactionsSettings.adviceLogger(Level.INFO, "The transaction with reference: " + reference + " is not stored in the system.");
			transaction = new BankTransaction();
			transaction.setReference(reference);
			transaction.setBusinessRule(1);
			transaction.setStatus(STATUS_INVALID);
		}
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT or ATM channel
//		And the transaction date is before today
//		Then: The system returns the status 'SETTLED'
//		And the amount substracting the fee
		// BUSINESS RULE 02
		else if (transactions != null && !transactions.isEmpty()) {
			if((CHANNEL_CLIENT.equals(channel) || CHANNEL_ATM.equals(channel))) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().before(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS)))) {
						TransactionsSettings.adviceLogger(Level.INFO, "The transaction with reference: " + reference + " was found on the system.");
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_SETTLED);
						transaction.setBusinessRule(2);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//		And the transaction date is before today
//		Then: The system returns the status 'SETTLED'
//		And the amount
//		And the fee
		// BUSINESS RULE 03
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_INTERNAL.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().before(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS)))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_SETTLED);
						transaction.setBusinessRule(3);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT or ATM channel
//		And the transaction date is equals to today
//		Then: The system returns the status 'PENDING'
//		And the amount substracting the fee
		// BUSINESS RULE 04
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_CLIENT.equals(channel) || CHANNEL_ATM.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().after(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS) -1)) && bankTransaction.getDate().before(
									new Date(System.currentTimeMillis() - (System.currentTimeMillis() % DAY_IN_MILISECONDS)
											+ DAY_IN_MILISECONDS))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_PENDING);
						transaction.setBusinessRule(4);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//		And the transaction date is equals to today
//		Then: The system returns the status 'PENDING'
//		And the amount
//		And the fee
		// BUSINESS RULE 05
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_INTERNAL.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().after(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS) -1)) && bankTransaction.getDate().before(
									new Date(System.currentTimeMillis() - (System.currentTimeMillis() % DAY_IN_MILISECONDS)
											+ DAY_IN_MILISECONDS))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_PENDING);
						transaction.setBusinessRule(5);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from CLIENT channel
//		And the transaction date is greater than today
//		Then: The system returns the status 'FUTURE'
//		And the amount substracting the fee
		// BUSINESS RULE 06
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_CLIENT.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().after(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS) + DAY_IN_MILISECONDS))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_FUTURE);
						transaction.setBusinessRule(6);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from ATM channel
//		And the transaction date is greater than today
//		Then: The system returns the status 'PENDING'
//		And the amount substracting the fee
		// BUSINESS RULE 07
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_ATM.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().after(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS) + DAY_IN_MILISECONDS))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_PENDING);
						transaction.setBusinessRule(7);
					}
				}
			}
		}
		
//		Given: A transaction that is stored in our system
//		When: I check the status from INTERNAL channel
//		And the transaction date is greater than today
//		Then: The system returns the status 'FUTURE'
//		And the amount
//		And the fee
		// BUSINESS RULE 08
		else if (transactions != null && !transactions.isEmpty()) {
			if(CHANNEL_INTERNAL.equals(channel)) {
				for(BankTransaction bankTransaction : transactions) {
					if(bankTransaction.getDate() != null && bankTransaction.getDate().after(new Date(System.currentTimeMillis()
							- (System.currentTimeMillis() % DAY_IN_MILISECONDS) + DAY_IN_MILISECONDS))) {
						transaction = new BankTransaction();
						transaction.setReference(reference);
						transaction.setAmount(bankTransaction.getAmount());
						transaction.setDate(bankTransaction.getDate());
						transaction.setDescription(bankTransaction.getDescription());
						transaction.setFee(bankTransaction.getFee());
						transaction.setStatus(STATUS_FUTURE);
						transaction.setBusinessRule(8);
					}
				}
			}
		}
		
		return transaction;
	}
	
	public void upsateTransactionHistory(List<BankTransaction> transactions, int polarity) throws ValidationException{
		validationTransactionHistory(transactions);
		
		if(bankTransactionsPerIban != null && polarity > 0) {
			TransactionHistory.getBankTransactionsPerAccount().get(transactions.get(0).getAccount_iban()).addAll(transactions);
		}
		else if(bankTransactionsPerIban != null && polarity < 0) {
			TransactionHistory.getBankTransactionsPerAccount().get(transactions.get(0).getAccount_iban()).removeAll(transactions);
		}
		else {
			TransactionsSettings.adviceLogger(Level.WARNING, "The polarity have no meaning being 0.");
		}
	}
	
	private void validationTransactionHistory(List<BankTransaction> transactions) throws ValidationException{
		if(transactions != null || !transactions.isEmpty()) {
			String firstIban = transactions.get(0).getAccount_iban();
			for(BankTransaction transaction : transactions) {
				if(!transaction.getAccount_iban().equals(firstIban)) {
					TransactionsSettings.adviceLogger(Level.SEVERE, "The transactions are not grouped by iban.");
					throw new ValidationException(transaction.getAccount_iban());
				}
			}
		}
	}

	public static Map<String, List<BankTransaction>> getBankTransactionsPerAccount() {
		return bankTransactionsPerIban;
	}

	public static void setBankTransactionsPerAccount(Map<String, List<BankTransaction>> bankTransactionsPerIban) {
		TransactionHistory.bankTransactionsPerIban = bankTransactionsPerIban;
	}
}
