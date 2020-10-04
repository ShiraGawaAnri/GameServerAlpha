package com.nekonade.gamegateway.server.handler.codec;


import com.nekonade.network.message.game.EnumMesasageType;
import com.nekonade.network.message.game.GameMessageHeader;
import com.nekonade.network.message.game.IGameMessage;
import lombok.Getter;

public class GateResponseMessage {

	@Getter
	private GameMessageHeader header;
	@Getter
	private byte[] responseBody; // 返回的消息体
	@Getter
	private IGameMessage request;
	
	public GateResponseMessage(GameMessageHeader header, byte[] responseBody){
		this.header = header;
		this.responseBody = responseBody;
	}

	public GateResponseMessage(IGameMessage request, IGameMessage response) {
		super();

		if (request != null && request.getHeader().getMesasageType() != EnumMesasageType.REQUEST) {
			throw new IllegalArgumentException("参数不是请求消息实例");
		}
		if (response != null) {
			EnumMesasageType responseType = response.getHeader().getMesasageType();
			if (responseType != EnumMesasageType.RESPONSE && responseType != EnumMesasageType.PUSH) {
				throw new IllegalArgumentException("参数不是返回消息实例");
			}
		}
		this.request = request;
		if (response != null) {
			this.header = response.getHeader();
			this.responseBody = response.write();
		}
		if (request != null && this.header != null) {
			GameMessageHeader requestHeader = request.getHeader();
			this.header.setClientSendTime(requestHeader.getClientSendTime());
			this.header.setClientSeqId(requestHeader.getClientSeqId());
		}

	}
}
