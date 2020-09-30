package com.nekonade.network.message.game;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @ClassName: AbstractJsonGameMessage 
 * @Description: 定义一个Json格式的消息体处理模板，如果客户端与服务器以Json格式交互，则继承此抽象类。
 * @author: wang guang shuai
 * @date: 2020年1月16日 上午10:26:36
 */
public abstract class AbstractJsonGameMessage<T,R extends IGameMessage> extends AbstractGameMessage {
	protected static Logger logger = LoggerFactory.getLogger(AbstractJsonGameMessage.class);
    private T bodyObj;//具体的参数类实例对象。所有的请求参数和响应参数，必须以对象的形式存在。

    public AbstractJsonGameMessage() {
    	super();
        if (this.getBodyObjClass() != null) {
            try {
                bodyObj = this.getBodyObjClass().newInstance();//在子类实例化时，同时实例化参数对象。
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected byte[] encode() {//使用JSON，将参数对象序列化
        String str = JSON.toJSONString(bodyObj);
        logger.debug("<={} [{}] :{}",this.getHeader().getDesc(),this.getHeader().getClientSeqId(),str);
        return str.getBytes();
    }

    @Override
    protected void decode(byte[] body) {//使用JSON，将收到的数据反序列化
        String str = new String(body);
        bodyObj = JSON.parseObject(str, this.getBodyObjClass());
    }

    @Override
    protected boolean isBodyMsgNull() {
        return this.bodyObj == null;
    }

    protected abstract Class<T> getBodyObjClass();//由子类返回具体的参数对象类型。

    public T getBodyObj() {
        return bodyObj;
    }

    public void setBodyObj(T bodyObj) {
        this.bodyObj = bodyObj;
    }
    
    public R createCouple() {
		R response = null;
		try {
			GameMessageHeader header = this.getHeader().clone();
			header.setFromServerId(this.header().getToServerId());
			header.setToServerId(this.header().getFromServerId());
			header.setMessageSize(0);
			response = newCouple();
			header.setMesasageType(response.getHeader().getMesasageType());
			header.setDesc(response.getHeader().getDesc());
			response.setHeader(header);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return response;
	}
    protected abstract R newCouple();

    @Override
    public String toString() {//重写toString，方便打印日志
        String msg = null;
        if (this.bodyObj != null) {
            msg = JSON.toJSONString(bodyObj);
        }
        return "Header:" + this.getHeader() + ", " + this.getClass().getSimpleName() + "=[bodyObj=" + msg + "]";
    }



}
