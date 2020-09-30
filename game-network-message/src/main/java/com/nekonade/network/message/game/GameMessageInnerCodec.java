package com.nekonade.network.message.game;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: wgs
 * @date: 2019年5月6日 上午10:11:51
 */
public class GameMessageInnerCodec {
	private static final Logger logger = LoggerFactory.getLogger(GameMessageInnerCodec.class);

	public static byte[] encodeMessage(GameMessageHeader header, ByteBuf data) {
		try {
			int initialCapacity = GameMessageHeader.HEADER_INNER_LENGTH + 4;

			if (data != null) {
				initialCapacity += data.readableBytes();
			}
			byte[] headerAttrBytes = header.getHeaderAttributeBytes();
			initialCapacity += headerAttrBytes.length + 4;
			ByteBuf byteBuf = Unpooled.buffer(initialCapacity);// 这里使用Unpooled创建ByteBuf，可以直接使用byteBuf.array();获取byte[]
			byteBuf.writeInt(initialCapacity);// 依次写入包头的数据
			header.writeInnerHeader(byteBuf);
			byteBuf.writeInt(headerAttrBytes.length);
			byteBuf.writeBytes(headerAttrBytes);
			byte[] value = null;
			if (data != null && data.readableBytes() > 0) {// 写入包体信息
				// ByteBuf bodyBuf =
				// Unpooled.wrappedBuffer(data);//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
				ByteBuf allBuf = Unpooled.wrappedBuffer(byteBuf, data);
				int len = allBuf.readableBytes();
				value = new byte[len];
				allBuf.readBytes(value);
			} else {
				value = byteBuf.array();
			}

			return value;
		} catch (Exception e) {
			logger.error("内部消息编码错误", e);
		}
		return null;
	}

	public static GameInnerMessage readGameMessagePackage(byte[] value){
		try {
			ByteBuf byteBuf = Unpooled.wrappedBuffer(value);// 直接使用byte[]包装为ByteBuf，减少一次数据复制
			GameMessageHeader header = new GameMessageHeader();// 向包头对象中添加数据
			header.readInnerHeader(byteBuf);
			byte[] body = null;
			if (byteBuf.readableBytes() > 0) {
				body = new byte[byteBuf.readableBytes()];
				byteBuf.readBytes(body);
			}
			GameInnerMessage gameMessagePackage = new GameInnerMessage(header, body);// 创建消息对象

			return gameMessagePackage;
		} catch (Exception e) {
			logger.error("内部消息 解码失败", e);
		}
		return null;
	}
}
