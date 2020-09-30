package com.nekonade.web.gateway.filter;

import com.nekonade.common.GameConstants;
import com.nekonade.common.errors.TokenException;
import com.nekonade.common.utils.JWTUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 
 * @ClassName: TokenVerifyFilter
 * @Description: token验证的filter，用户登陆成功之后，以后再访问服务需要对token进行验证。
 * @author: wgs
 * @date: 2019年3月15日 下午8:01:58
 */
@Service
public class TokenVerifyFilter implements GlobalFilter, Ordered {

    @Autowired
    private FilterConfig filterConfig;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    private final Logger logger = LoggerFactory.getLogger(TokenVerifyFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestUri = exchange.getRequest().getURI().getPath();
        List<String> whiteRequestUris = filterConfig.getWhiteRequestUri();
        if (whiteRequestUris.contains(requestUri)) {
            return chain.filter(exchange);// 如果请求的uri在白名单中，则跳过验证。
        }

        String token = exchange.getRequest().getHeaders().getFirst(GameConstants.USER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            logger.debug("{} 请求验证失败,token为空", requestUri);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        try {
            JWTUtil.getTokenContent(token);
            return chain.filter(exchange);
        } catch (TokenException e) {
            logger.debug("{} 请求验证失败,token非法");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

    }

}
