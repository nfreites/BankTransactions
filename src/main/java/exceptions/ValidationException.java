package exceptions;
import java.util.List;

public class ValidationException extends Exception{
	
	public ValidationException(String fields) {
		super("The field/s: " + fields + " is/are null or not a valid input.");
	}
}
