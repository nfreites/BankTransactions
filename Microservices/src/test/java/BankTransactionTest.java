import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;

public class BankTransactionTest {
	
	// Object under test
	private BankTransaction transaction;
	
	// Input fields
	private String reference = "ref1234";
	private String iban = "ES240000111122223333";
	private Double amount = 100.0;
	private Double fee = 5.0;
	private Date date = new Date(System.currentTimeMillis());
	private String description = "description";
	
	@Before 
	public void setObjects(){
		transaction = new BankTransaction();
		transaction.setReference(reference);
		transaction.setAccount_iban(iban);
		transaction.setAmount(amount);
		transaction.setFee(fee);
		transaction.setDate(date);
		transaction.setDescription(description);
	}
	
	public void createTransactionTest() throws NegativeAccountException {
		// We setup the object under test
		TransactionDTOIn transactionDtoIn = new TransactionDTOIn();
		transactionDtoIn.setAccount_iban(iban);
		transactionDtoIn.setDate(date);
		transactionDtoIn.setDescription(description);
		transactionDtoIn.setFee(fee);
		transactionDtoIn.setReference(reference);
		
		// Method call to prove it.
		transaction.createTransaction(transactionDtoIn);
		
		// Assertions.
		assertEquals("The field iban should have been setted from the DTO to the object under test.", transaction.getAccount_iban(), transactionDtoIn.getAccount_iban());
		assertEquals("The field reference should have been setted from the DTO to the object under test.", transaction.getReference(), transactionDtoIn.getReference());
	}
}
