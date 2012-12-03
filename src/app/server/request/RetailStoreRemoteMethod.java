package app.server.request;

import java.io.Serializable;

public enum RetailStoreRemoteMethod implements Serializable {
	PURCHASE_ITEM, RETURN_ITEM, TRANSFER_ITEM,
	CHECK_STOCK, EXCHANGE
}