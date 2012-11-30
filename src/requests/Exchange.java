package requests;

import packet.MethodRequest;


public class Exchange extends MethodRequest<RetailStoreRemoteMethod> {
	String customerID;
	int boughtItemID;
	int boughtNumber;
	int desiredItemID;
	int desiredNumber;
		
	public Exchange(String customerID, int boughtItemID, int boughtNumber,
			int desiredItemID, int desiredNumber) {
		super(RetailStoreRemoteMethod.EXCHANGE);
		this.customerID    = customerID ;		
		this.boughtItemID  = boughtItemID;
		this.boughtNumber  = boughtNumber;
		this.desiredItemID = desiredItemID;
		this.desiredNumber = desiredNumber;
	}

	public String getCustomerID() {
		return customerID;
	}

	public int getBoughtItemID() {
		return boughtItemID;
	}

	public int getBoughtNumber() {
		return boughtNumber;
	}

	public int getDesiredItemID() {
		return desiredItemID;
	}

	public int getDesiredNumber() {
		return desiredNumber;
	}

}
