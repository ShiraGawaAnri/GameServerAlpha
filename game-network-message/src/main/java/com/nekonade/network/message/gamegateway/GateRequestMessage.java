package com.nekonade.gamegateway.server.handler.codec;

import com.nekonade.common.utils.CompressUtil;
import com.nekonade.network.message.game.GameMessageHeader;
import com.nekonade.network.message.game.IGameMessage;
import io.netty.buffer.ByteBuf;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;

import java.io.IOException;

public class GateRequestMessage {

	@Getter
	private final GameMessageHeader header;
	@Getter
	private final ByteBuf byteBuf;// 读取包头之后，剩下的消息体内容
	@Getter
	private final String aesSecret;

	public GateRequestMessage(GameMessageHeader header, ByteBuf byteBuf, String aesSecret) {
		super();
		this.header = header;
		this.byteBuf = byteBuf;
		this.aesSecret = aesSecret;
	}

	public byte[] readBody() throws IOException {
		byte[] body = null;
		if (byteBuf.readableBytes() > 0) {
			body = new byte[byteBuf.readableBytes()];
			byteBuf.readBytes(body);
			if (this.aesSecret != null) {// 如果密钥不为空，且不是认证消息，对消息体解密
				// body = AESUtils.decode(aesSecret, body);
			}
			if (header.isCompress()) {
				body = CompressUtil.decompress(body);
			}
		}
		return body;
	}

	public void getGameMessage(IGameMessage gameMessage) {
		// gameMessage.setHeader(header);
		GameMessageHeader newHeader = gameMessage.getHeader();
		newHeader.setClientSendTime(header.getClientSendTime());
		newHeader.setClientSeqId(header.getClientSeqId());
		newHeader.setCompress(header.isCompress());
		newHeader.setErrorCode(header.getErrorCode());
		newHeader.setFromServerId(header.getFromServerId());
		newHeader.setMessageSize(header.getMessageSize());
		newHeader.setServerSendTime(header.getServerSendTime());
		newHeader.setToServerId(header.getToServerId());
		newHeader.setVersion(header.getVersion());
		try {
			gameMessage.read(this.readBody());
		} catch (IOException e) {
			e.printStackTrace();
			ReferenceCountUtil.release(byteBuf);
		}
	}

}
