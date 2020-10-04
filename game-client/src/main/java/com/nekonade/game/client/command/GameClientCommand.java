package com.nekonade.game.client.command;

import com.nekonade.game.client.service.GameClientBoot;
import com.nekonade.game.client.service.GameClientConfig;
import com.nekonade.network.param.game.common.GameMessageHeader;
import com.nekonade.network.param.game.message.ConfirmMsgRequest;
import com.nekonade.network.param.game.message.FirstMsgRequest;
import com.nekonade.network.param.game.message.SecondMsgRequest;
import com.nekonade.network.param.game.message.ThirdMsgRequest;
import com.nekonade.network.param.game.message.body.ThirdMsgBody;
import com.nekonade.network.param.game.message.xinyue.BuyArenaChallengeTimesMsgRequest;
import com.nekonade.network.param.game.message.xinyue.EnterGameMsgRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GameClientCommand {

    private static final Logger logger = LoggerFactory.getLogger(GameClientCommand.class);

    @Autowired
    private GameClientBoot gameClientBoot;
    @Autowired
    private GameClientConfig gameClientConfig;


    @ShellMethod("连接服务器，格式：cs [port]")//连接服务器命令，
    public void cs() {
        connectServer("127.0.0.1",16002);
    }

    @ShellMethod("连接服务器，格式：cs [host] [port]")//连接服务器命令，
    public void connectServer(@ShellOption(defaultValue = "127.0.0.1") String host, @ShellOption(defaultValue = "0") int port) {
        if (!host.isEmpty()) {//如果默认的host不为空，说明是连接指定的host，如果没有指定host，使用配置中的默认host和端口
            if (port == 0) {
                logger.error("请输入服务器端口号");
                return;
            }
            gameClientConfig.setDefaultGameGatewayHost(host);
            gameClientConfig.setDefaultGameGatewayPort(port);
        }
        gameClientBoot.launch();// 启动客户端并连接游戏网关
    }

    @ShellMethod("关闭连接")
    public void close() {
        gameClientBoot.getChannel().close();
    }

    @ShellMethod("发送测试消息，格式：msg 消息号")
    public void msg(int messageId) {

        if (messageId == 1) {//发送认证请求
            ConfirmMsgRequest request = new ConfirmMsgRequest();
            GameMessageHeader header = request.getHeader();
            header.setClientSendTime(System.currentTimeMillis());
            request.getBodyObj().setToken(gameClientConfig.getGatewayToken());
//            GameMessageHeader header = new GameMessageHeader();
//            GateRequestMessage gateRequestMessage = new GateRequestMessage(header,Unpooled.wrappedBuffer(request.write()),"");
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 10001) {
            // 向服务器发送一条消息
            FirstMsgRequest request = new FirstMsgRequest();
            request.setValue("Hello,server !!");
            request.getHeader().setClientSendTime(System.currentTimeMillis());
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 10002) {
            SecondMsgRequest request = new SecondMsgRequest();
            request.getBodyObj().setValue1("你好，这是测试请求");
            request.getBodyObj().setValue2(System.currentTimeMillis());
            gameClientBoot.getChannel().writeAndFlush(request);
        }
        if (messageId == 10003) {
            ThirdMsgRequest request = new ThirdMsgRequest();
            ThirdMsgBody.ThirdMsgRequestBody requestBody = ThirdMsgBody.ThirdMsgRequestBody.newBuilder().setValue1("我是Protocol Buffer序列化的").setValue2(System.currentTimeMillis()).build();
            request.setRequestBody(requestBody);
            gameClientBoot.getChannel().writeAndFlush(request);
        }
       if(messageId == 201) {//进入游戏请求
           EnterGameMsgRequest request = new EnterGameMsgRequest();
           gameClientBoot.getChannel().writeAndFlush(request);
       }
       if(messageId == 210) {
           BuyArenaChallengeTimesMsgRequest request = new BuyArenaChallengeTimesMsgRequest();
           gameClientBoot.getChannel().writeAndFlush(request);
       }
    }
}
