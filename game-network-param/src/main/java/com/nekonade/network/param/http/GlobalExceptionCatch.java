package com.nekonade.network.param.http;

import com.alibaba.fastjson.JSONObject;
import com.nekonade.common.error.GameErrorException;
import com.nekonade.common.error.IServerError;
import com.nekonade.common.error.GameCenterError;
import com.nekonade.network.param.http.response.ResponseEntity;
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
        IServerError error = null;
        if (ex instanceof GameErrorException) {
            GameErrorException gameError = (GameErrorException) ex;
            error = gameError.getError();
            logger.error("服务调用失败,{}", ex.getMessage());
        } else {
            error = GameCenterError.UNKNOW;
            logger.error("服务预料外异常,{}", ex.getClass().getName(), ex);
        } 
        JSONObject data = new JSONObject();//统一给客户端返回结果
        data.put("errorMsg", ex.getMessage());
        ResponseEntity<JSONObject> response = new ResponseEntity<>(error);
        response.setData(data);
        return response;

    }

}
