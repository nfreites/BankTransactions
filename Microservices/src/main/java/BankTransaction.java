
import java.util.Date;
import java.util.logging.Level;

public class BankTransaction {
	
	// Optional.
	// The transaction unique reference number in our system. If not present, the system will generate one.
	private String reference;
	// Mandatory.
	// The IBAN number of the account where the transaction has happened.
	private String account_iban;
	// Optional.
	// Date when the transaction took place.
	private Date date;
	// Mandatory.
	// If positive the transaction is a credit (add money) to the account. If negative it is a
	// debit (deduct money from the account).
	private Double amount;
	// Optional.
	// Fee that will be deducted from the amount, regardless on the amount being positive or negative.
	private Double fee;
	// Optional.
	// The description of the transaction.
	private String description;
	private String status;
	private int businessRule;
	
	/** 
	 * This endpoint will receive the transaction information and store it into the system.
	 */
	public void createTransaction(TransactionDTOIn transactionDtoIn) throws NegativeAccountException{
		// The transaction may be a debit or not but with the fee, it leave the account with negative balance.
		TransactionsSettings.verifyNegativeAccount(this.amount, transactionDtoIn);

		// We set the transaction properties
		this.reference = transactionDtoIn.getReference();
		this.account_iban = transactionDtoIn.getAccount_iban();
		this.date = transactionDtoIn.getDate();
		this.amount = transactionDtoIn.getAmount();
		this.fee = transactionDtoIn.getFee();
		this.description = transactionDtoIn.getDescription();

		TransactionsSettings.adviceLogger(Level.INFO, "The transaction was created successfully.");
	}
	
	public static String consultStatus(String reference) {
		BankTransaction transaction = TransactionHistory.consultStatus(reference);
		if(transaction == null) {
			return null;
		}
		return transaction.getStatus();
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getAccount_iban() {
		return account_iban;
	}

	public void setAccount_iban(String account_iban) {
		this.account_iban = account_iban;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getFee() {
		return fee;
	}

	public void setFee(Double fee) {
		this.fee = fee;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getBusinessRule() {
		return businessRule;
	}

	public void setBusinessRule(int businessRule) {
		this.businessRule = businessRule;
	}
}
