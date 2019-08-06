package exceptions;
import java.util.List;

public class Exceptions {
	
	public static void throwNegativeAccountException() throws NegativeAccountException{
		throw new NegativeAccountException();
	}
	
	public static void throwValidationException(List<Object> fields) throws ValidationException{
		String fieldsString = "";
		for(int i = 0; i < fields.size(); i++) {
			fieldsString += fields.get(i).getClass().getName();
			if(i != fields.size() - 1) {
				fieldsString += ", ";
			}
		}
		throw new ValidationException(fieldsString);
	}
}
