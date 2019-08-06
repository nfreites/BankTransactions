package exceptions;

public class NegativeAccountException extends Exception{
	
	public NegativeAccountException() {
		super("The transaction would leave the account balance below 0.");
	}
	
}
