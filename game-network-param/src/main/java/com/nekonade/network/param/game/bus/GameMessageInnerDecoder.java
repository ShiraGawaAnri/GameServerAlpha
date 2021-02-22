package com.nekonade.network.param.game.bus;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.common.GameMessagePackage;
import com.nekonade.network.param.game.common.HeaderAttribute;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;

public class GameMessageInnerDecoder {

    private final static int HEADER_FIX_LEN = 60;


    /**
     * 由于是内部处理，所以使用Fastjson问题不大,但最终应该统一为Jackson
     * @param gameMessagePackage
     * @return
     */
    @Deprecated
    public static byte[] sendMessage(GameMessagePackage gameMessagePackage) {
        int initialCapacity = HEADER_FIX_LEN;
        GameMessageHeader header = gameMessagePackage.getHeader();

        String headerAttJson = JSON.toJSONString(header.getAttribute());//把包头的属性类序列化为json
        byte[] headerAttBytes = headerAttJson.getBytes();
        initialCapacity += headerAttBytes.length;
        if (gameMessagePackage.getBody() != null) {
            initialCapacity += gameMessagePackage.getBody().length;
        }
        ByteBuf byteBuf = Unpooled.buffer(initialCapacity);//这里使用Unpooled创建ByteBuf，可以直接使用byteBuf.array();获取byte[]
        byteBuf.writeInt(initialCapacity);//依次写入包头的数据
        byteBuf.writeInt(header.getToServerId());
        byteBuf.writeInt(header.getFromServerId());
        byteBuf.writeInt(header.getClientSeqId());
        byteBuf.writeInt(header.getMessageId());
        byteBuf.writeInt(header.getServiceId());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeLong(header.getClientSendTime());
        byteBuf.writeLong(header.getServerSendTime());
        byteBuf.writeLong(header.getPlayerId());
        byteBuf.writeInt(headerAttBytes.length);
        byteBuf.writeBytes(headerAttBytes);
        byteBuf.writeInt(header.getErrorCode());
        byte[] value = null;
        if (gameMessagePackage.getBody() != null) {//写入包体信息
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(gameMessagePackage.getBody());//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
            ByteBuf allBuf = Unpooled.wrappedBuffer(byteBuf, bodyBuf);
            value = new byte[allBuf.readableBytes()];
            allBuf.readBytes(value);
            //ReferenceCountUtil.release(bodyBuf);
            //ReferenceCountUtil.release(allBuf);
        } else {
            value = byteBuf.array();
        }
        //ReferenceCountUtil.release(byteBuf);
        return value;
    }

    /**
     * 由于是内部处理，所以使用Fastjson问题不大,但最终应该统一为Jackson
     * @param value
     * @return
     */
    @Deprecated
    public static GameMessagePackage readGameMessagePackage(byte[] value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(value);//直接使用byte[]包装为ByteBuf，减少一次数据复制
        int messageSize = byteBuf.readInt();//依次读取包头信息
        int toServerId = byteBuf.readInt();
        int fromServerId = byteBuf.readInt();
        int clientSeqId = byteBuf.readInt();
        int messageId = byteBuf.readInt();
        int serviceId = byteBuf.readInt();
        int version = byteBuf.readInt();
        long clientSendTime = byteBuf.readLong();
        long serverSendTime = byteBuf.readLong();
        long playerId = byteBuf.readLong();
        int headerAttrLength = byteBuf.readInt();
        HeaderAttribute hearerAttr = null;
        if (headerAttrLength > 0) {//读取包头属性
            byte[] headerAttrBytes = new byte[headerAttrLength];
            byteBuf.readBytes(headerAttrBytes);
            String headerAttrJson = new String(headerAttrBytes);
            hearerAttr = JSON.parseObject(headerAttrJson, HeaderAttribute.class);
        }
        int errorCode = byteBuf.readInt();
        byte[] body = null;
        if (byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        GameMessageHeader header = new GameMessageHeader();//向包头对象中添加数据
        header.setAttribute(hearerAttr);
        header.setClientSendTime(clientSendTime);
        header.setClientSeqId(clientSeqId);
        header.setErrorCode(errorCode);
        header.setFromServerId(fromServerId);
        header.setMessageId(messageId);
        header.setMessageSize(messageSize);
        header.setPlayerId(playerId);
        header.setServerSendTime(serverSendTime);
        header.setServiceId(serviceId);
        header.setToServerId(toServerId);
        header.setVersion(version);
        GameMessagePackage gameMessagePackage = new GameMessagePackage();//创建消息对象
        gameMessagePackage.setHeader(header);
        gameMessagePackage.setBody(body);
        //ReferenceCountUtil.release(byteBuf);
        return gameMessagePackage;
    }

    @SneakyThrows
    public static byte[] sendMessage(ObjectMapper objectMapper, GameMessagePackage gameMessagePackage) {
        int initialCapacity = HEADER_FIX_LEN;
        GameMessageHeader header = gameMessagePackage.getHeader();

        String headerAttJson = objectMapper.writeValueAsString(header.getAttribute());//把包头的属性类序列化为json
        byte[] headerAttBytes = headerAttJson.getBytes();
        initialCapacity += headerAttBytes.length;
        if (gameMessagePackage.getBody() != null) {
            initialCapacity += gameMessagePackage.getBody().length;
        }
        ByteBuf byteBuf = Unpooled.buffer(initialCapacity);//这里使用Unpooled创建ByteBuf，可以直接使用byteBuf.array();获取byte[]
        byteBuf.writeInt(initialCapacity);//依次写入包头的数据
        byteBuf.writeInt(header.getToServerId());
        byteBuf.writeInt(header.getFromServerId());
        byteBuf.writeInt(header.getClientSeqId());
        byteBuf.writeInt(header.getMessageId());
        byteBuf.writeInt(header.getServiceId());
        byteBuf.writeInt(header.getVersion());
        byteBuf.writeLong(header.getClientSendTime());
        byteBuf.writeLong(header.getServerSendTime());
        byteBuf.writeLong(header.getPlayerId());
        byteBuf.writeInt(headerAttBytes.length);
        byteBuf.writeBytes(headerAttBytes);
        byteBuf.writeInt(header.getErrorCode());
        byte[] value = null;
        if (gameMessagePackage.getBody() != null) {//写入包体信息
            ByteBuf bodyBuf = Unpooled.wrappedBuffer(gameMessagePackage.getBody());//使用byte[]包装为ByteBuf，减少一次byte[]拷贝。
            ByteBuf allBuf = Unpooled.wrappedBuffer(byteBuf, bodyBuf);
            value = new byte[allBuf.readableBytes()];
            allBuf.readBytes(value);
            //ReferenceCountUtil.release(bodyBuf);
            //ReferenceCountUtil.release(allBuf);
        } else {
            value = byteBuf.array();
        }
        //ReferenceCountUtil.release(byteBuf);
        return value;
    }

    @SneakyThrows
    public static GameMessagePackage readGameMessagePackage(ObjectMapper objectMapper, byte[] value) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(value);//直接使用byte[]包装为ByteBuf，减少一次数据复制
        int messageSize = byteBuf.readInt();//依次读取包头信息
        int toServerId = byteBuf.readInt();
        int fromServerId = byteBuf.readInt();
        int clientSeqId = byteBuf.readInt();
        int messageId = byteBuf.readInt();
        int serviceId = byteBuf.readInt();
        int version = byteBuf.readInt();
        long clientSendTime = byteBuf.readLong();
        long serverSendTime = byteBuf.readLong();
        long playerId = byteBuf.readLong();
        int headerAttrLength = byteBuf.readInt();
        HeaderAttribute hearerAttr = null;
        if (headerAttrLength > 0) {//读取包头属性
            byte[] headerAttrBytes = new byte[headerAttrLength];
            byteBuf.readBytes(headerAttrBytes);
            String headerAttrJson = new String(headerAttrBytes);
            hearerAttr = objectMapper.readValue(headerAttrJson, HeaderAttribute.class);
        }
        int errorCode = byteBuf.readInt();
        byte[] body = null;
        if (byteBuf.readableBytes() > 0) {
            body = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(body);
        }
        GameMessageHeader header = new GameMessageHeader();//向包头对象中添加数据
        header.setAttribute(hearerAttr);
        header.setClientSendTime(clientSendTime);
        header.setClientSeqId(clientSeqId);
        header.setErrorCode(errorCode);
        header.setFromServerId(fromServerId);
        header.setMessageId(messageId);
        header.setMessageSize(messageSize);
        header.setPlayerId(playerId);
        header.setServerSendTime(serverSendTime);
        header.setServiceId(serviceId);
        header.setToServerId(toServerId);
        header.setVersion(version);
        GameMessagePackage gameMessagePackage = new GameMessagePackage();//创建消息对象
        gameMessagePackage.setHeader(header);
        gameMessagePackage.setBody(body);
        //ReferenceCountUtil.release(byteBuf);
        return gameMessagePackage;
    }

}
