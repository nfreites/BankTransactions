import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionsSettings {
	
	private static Logger logger = Logger.getLogger("src.main.java.TransactionsSettings");
	private TransactionHistory transactionHistory;
	
	public TransactionsSettings() {
		
	}
	
	public Map<String, List<BankTransaction>> fillTransactionHistory(List<BankTransaction> transactions) {
		
		// First we filter by iban.
		if(transactionHistory == null || transactionHistory.getBankTransactionsPerAccount().isEmpty()) {
			transactionHistory = new TransactionHistory();
		}
		
		Map<String, List<BankTransaction>> transactionsPerIban = new HashMap<>();
		List<BankTransaction> transactionsOut = new ArrayList<>();
		for(BankTransaction transaction : transactions) {
			if(!transactionsOut.contains(transaction)) {
				transactionsOut.add(transaction);
			}
			for(BankTransaction transaction2 : transactions) {
				if(!transaction.getReference().equals(transaction2.getReference())
						&& transaction.getAccount_iban().equals(transaction2.getAccount_iban())) {
					if(!transactionsOut.contains(transaction2)) {
						transactionsOut.add(transaction2);
					}
				}
			}
			transactionsPerIban.put(transactionsOut.get(0).getAccount_iban(), transactionsOut);
		}
		
		// Now we order by amount.
		Map<String, List<BankTransaction>> sortedTransactions = new HashMap<>();
		for(String key : transactionsPerIban.keySet()) {
			sortedTransactions.put(key, bubbleSort(transactionsPerIban.get(key)));
		}
		transactionHistory.setBankTransactionsPerAccount(transactionsPerIban);
		
		return sortedTransactions;
	}
	
	private List<BankTransaction> bubbleSort(List<BankTransaction> transactions) {
		List<BankTransaction> transactionsOut = new ArrayList<>();
	    boolean sorted = false;
	    BankTransaction aux;
	    while(!sorted) {
	        sorted = true;
	        for (int i = 0; i < transactions.size() - 1; i++) {
	            if (transactions.get(i).getAmount().doubleValue() > transactions.get(i+1).getAmount().doubleValue()) {
	                aux = transactions.get(i);
	                transactionsOut.set(i+1, transactions.get(i+1));
	                transactionsOut.set(i+1, aux);
	                sorted = false;
	            }
	        }
	    }
	    return transactionsOut;
	}
	
	public static void verifyNegativeAccount(Double currentAmount, TransactionDTOIn transactionDtoIn) throws NegativeAccountException{
		// The transaction may be a debit or not but with the fee, it leave the account with negative balance.
		if((currentAmount.doubleValue() + (transactionDtoIn.getAmount().doubleValue()
				- ((transactionDtoIn.getFee() == null) ? 0.0 : transactionDtoIn.getFee().doubleValue()))) < 0.0) {
			adviceLogger(Level.SEVERE, "The transaction would leave the account with negative balance.");
			throw new NegativeAccountException();
		}
	}
	
	public Map<String, List<BankTransaction>> searchTransactions(String iban){
		TransactionHistory.getBankTransactionsPerAccount().get(iban);
		
		List<BankTransaction> transactions = new ArrayList<>();
		Map<String, List<BankTransaction>> transactionsPerIban = new HashMap<>();
		
		return transactionsPerIban;
	}
	
	public BankTransaction transactionStatus(String reference, String channel) {
		for(String iban : TransactionHistory.bankTransactionsPerIban.keySet()) {
			for(BankTransaction transaction : TransactionHistory.bankTransactionsPerIban.get(iban)) {
				if(transaction.getReference().equalsIgnoreCase(reference)) {
					BankTransaction transact = transactionHistory.getTranssaction(reference, channel);
					return transact;
				}
			}
		}
		return null;
	}
	
	public static void adviceLogger(Level level, String message) {
		logger.log(level, message);
	}

	public TransactionHistory getTransactionHistory() {
		return transactionHistory;
	}

	public void setTransactionHistory(TransactionHistory transactionHistory) {
		this.transactionHistory = transactionHistory;
	}
}
