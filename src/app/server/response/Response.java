package app.server.response;

import packet.StatusPacket;

public class Response extends StatusPacket<ReturnStatus> {
	public Response(ReturnStatus status) {
		super(status);
	}
}
