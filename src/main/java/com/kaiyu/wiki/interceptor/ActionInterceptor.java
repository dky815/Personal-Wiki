package com.kaiyu.wiki.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.kaiyu.wiki.resp.CommonResp;
import com.kaiyu.wiki.resp.UserLoginResp;
import com.kaiyu.wiki.util.LoginUserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interceptor: A feature unique to the Spring framework, commonly used for login verification, permission verification, and request log printing
 */
@Component
public class ActionInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(ActionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // No validation is performed for OPTIONS requests.
        // In a front-end and back-end separation architecture, the front-end sends an OPTIONS request for pre-flight checks. No validation is performed for pre-flight requests.
        if("OPTIONS".equals(request.getMethod().toUpperCase())){
            return true;
        }

        UserLoginResp userLoginResp = LoginUserContext.getUser();
        if ("admin".equals(userLoginResp.getLoginName())) {
            // The admin user is not intercepted
            return true;
        }

        LOG.info("The operation has been intercepted");
        response.setStatus(HttpStatus.OK.value());
        CommonResp commonResp = new CommonResp();
        commonResp.setSuccess(false);
        commonResp.setMessage("Haha, the operation has been intercepted. You can consider it a successful operation! Please note that the sample website does not currently support adding, deleting, or modifying operations.");
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().print(JSONObject.toJSON(commonResp));
        return false;
    }

}
