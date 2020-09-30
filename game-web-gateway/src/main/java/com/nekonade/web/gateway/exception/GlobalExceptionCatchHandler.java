package com.nekonade.web.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: GlobalExceptionCatchHandler
 * @Description: 网关全局web错误异常捕获
 * @author: wgs
 * @date: 2019年3月22日 上午10:11:44
 */
public class GlobalExceptionCatchHandler extends DefaultErrorWebExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionCatchHandler.class);

    public GlobalExceptionCatchHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties, ErrorProperties errorProperties, ApplicationContext applicationContext) {
        super(errorAttributes, resourceProperties, errorProperties, applicationContext);
    }

    /**
     * 当捕获到异常之后，在这里构造返回给客户端的错误内容。这里构造的格式和用户中心服务返回的错误格式是一致的。这样方便客户端对错误信息做统一处理。
     */
    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        // 当捕获到异常之后，在这里构造返回给客户端的错误内容。这里构造的格式和用户中心服务返回的错误格式是一致的。这样方便客户端对错误信息做统一处理。
        Throwable error = super.getError(request);
        Map<String, Object> result = new HashMap<>();
        if(error instanceof WebGatewayException){
            WebGatewayException ex = (WebGatewayException)error;
            result.put("code",ex.getError().getErrorCode());
            result.put("errorMsg",ex.getError().getErrorDesc());
            logger.error("",ex);
        }else {
            // 这里可以根据自己的业务需求添加不同的错误码。
            result.put("code", WebGatewayError.UNKNOWN.getErrorCode());
            result.put("errorMsg", error.getMessage());
            logger.error("{}", WebGatewayError.UNKNOWN, error);
        }
        return result;
    }

    /**
     * 根据code获取对应的HttpStatus
     *
     * @param errorAttributes
     * @return
     */
    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes) {
        // 这里正常返回消息，请客户端根据返回的code做自定义处理。
        return HttpStatus.OK.value();
    }

    @Override
    protected RequestPredicate acceptsTextHtml() {
        // 这里指定客户端不接收HTML格式的信息，全部以JSON的格式返回。
        return c -> false;
    }

}
