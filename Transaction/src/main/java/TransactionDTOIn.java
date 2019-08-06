import java.util.Date;

public class TransactionDTOIn {
	
		private String reference;
		private String account_iban;
		private Date date;
		private Double amount;
		private Double fee;
		private String description;
		private String status;
		
		public TransactionDTOIn() {
			
		}

		public TransactionDTOIn(String reference, String account_iban, Date date, Double amount, Double fee,
				String description, String status) {
			super();
			this.reference = reference;
			this.account_iban = account_iban;
			this.date = date;
			this.amount = amount;
			this.fee = fee;
			this.description = description;
			this.status = status;
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
}
