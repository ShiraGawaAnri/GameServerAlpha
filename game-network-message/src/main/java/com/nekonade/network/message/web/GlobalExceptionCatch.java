package com.nekonade.network.message.web;

import com.alibaba.fastjson.JSONObject;
import com.nekonade.network.message.errors.GameErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @ClassName: GlobalExceptionCatch
 * @Description: 全局异常捕获
 * @author: wgs
 * @date: 2019年3月15日 下午10:27:55
 */
@ControllerAdvice
public class GlobalExceptionCatch {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionCatch.class);

    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<JSONObject> exceptionHandler(Throwable ex) {
        ResponseEntity<JSONObject> response = null;
        
        //这里的ex会对异常包装一下，所以需要获取一下
        if (ex instanceof GameErrorException) {
            GameErrorException gameError = (GameErrorException) ex;
            response = new ResponseEntity<>(gameError.getError());
            logger.error("服务器异常,{}", ex.getMessage());
        } else {
            response = new ResponseEntity<>(-1, "未知异常:" + ex.getMessage());
            logger.error("服务器未知异常,{}",ex.getClass().getName(), ex);
        }
        return response;
    }

}
