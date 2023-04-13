package com.kaiyu.wiki.interceptor;

import com.alibaba.fastjson.JSON;
import com.kaiyu.wiki.resp.UserLoginResp;
import com.kaiyu.wiki.util.LoginUserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor: A feature unique to the Spring framework, commonly used for login verification, permission verification, and request log printing.
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(LoginInterceptor.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Print request information.
        LOG.info("------------- LoginInterceptor start -------------");
        long startTime = System.currentTimeMillis();
        request.setAttribute("requestStartTime", startTime);

        // No validation is performed for OPTIONS requests.
        // In a front-end and back-end separation architecture, the front-end sends an OPTIONS request for pre-flight checks. No validation is performed for pre-flight requests.
        if(request.getMethod().toUpperCase().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURL().toString();
        LOG.info("Interface login interception, path: {}", path);

        // Get the token parameter from the header
        String token = request.getHeader("token");
        LOG.info("Login verification started, token: {}", token);
        if (token == null || token.isEmpty()) {
            LOG.info( "The token is empty. The request has been intercepted." );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        Object object = redisTemplate.opsForValue().get(token);
        if (object == null) {
            LOG.warn( "The token is invalid. The request has been intercepted." );
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        } else {
            LOG.info("Logged in: {}", object);
            LoginUserContext.setUser(JSON.parseObject((String) object, UserLoginResp.class));
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long startTime = (Long) request.getAttribute("requestStartTime");
        LOG.info("------------- LoginInterceptor Finished. Time elapsed: {} ms -------------", System.currentTimeMillis() - startTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        LOG.info("LogInterceptor Finished");
    }
}
