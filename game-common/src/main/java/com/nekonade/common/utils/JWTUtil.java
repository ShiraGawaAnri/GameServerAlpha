package com.nekonade.common.utils;

import com.alibaba.fastjson.JSON;
import com.nekonade.common.errors.TokenException;
import io.jsonwebtoken.*;

import java.time.Duration;
import java.util.Date;

public class JWTUtil {
    private final static String TOKEN_SECRET = "game_token#$%Abc";

    public static String createToken(TokenContent tokenContent,Duration expire) {
        if(expire == null) {
            throw new IllegalArgumentException("expire 不能为空");
        }
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;//使用对称加密算法生成签名
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        String subject = JSON.toJSONString(tokenContent);
        JwtBuilder builder = Jwts.builder().setId(String.valueOf(nowMillis)).setIssuedAt(now).setSubject(subject).signWith(signatureAlgorithm, TOKEN_SECRET);
        long expMillis = nowMillis + expire.toMillis();
        Date exp = new Date(expMillis);
        builder.setExpiration(exp);
        return builder.compact();
    }

    public static TokenContent getTokenContent(String token) throws TokenException {
        try {
            Claims claims = Jwts.parser().setSigningKey(TOKEN_SECRET).parseClaimsJws(token).getBody();
            String subject = claims.getSubject();
            TokenContent tokenBody = JSON.parseObject(subject, TokenContent.class);
            return tokenBody;
        } catch (Throwable e) {
            TokenException exp = new TokenException("token解析失败", e);
            if (e instanceof ExpiredJwtException) {
                exp.setExpire(true);
            }
            throw exp;
        }
    }

    public static class TokenContent {
        
        private String openId;
        private long userId;
        private long playerId;
        private String serverId;
        private int loginType;
        private String publicKey;//与客户端交互的公钥
        private String gatewayIp;//网关ip地址
        
        public String getGatewayIp() {
            return gatewayIp;
        }
        public void setGatewayIp(String gatewayIp) {
            this.gatewayIp = gatewayIp;
        }
        public String getPublicKey() {
            return publicKey;
        }
        public void setPublicKey(String publicKey) {
            this.publicKey = publicKey;
        }
        public String getOpenId() {
            return openId;
        }
        public void setOpenId(String openId) {
            this.openId = openId;
        }
        public long getUserId() {
            return userId;
        }
        public void setUserId(long userId) {
            this.userId = userId;
        }
        public long getPlayerId() {
            return playerId;
        }
        public void setPlayerId(long playerId) {
            this.playerId = playerId;
        }
        public String getServerId() {
            return serverId;
        }
        public void setServerId(String serverId) {
            this.serverId = serverId;
        }
        public int getLoginType() {
            return loginType;
        }
        public void setLoginType(int loginType) {
            this.loginType = loginType;
        }
    }

 
}
