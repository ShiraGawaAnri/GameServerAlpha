package com.nekonade.network.message.game;

public class GameInnerMessage {

	private final GameMessageHeader header;
	private final byte[] body;
	public GameInnerMessage(GameMessageHeader header, byte[] body) {
		super();
		this.header = header;
		this.body = body;
	}
	public GameMessageHeader getHeader() {
		return header;
	}
	public byte[] getBody() {
		return body;
	}


}
