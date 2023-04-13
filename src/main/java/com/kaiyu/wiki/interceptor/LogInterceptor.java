// package com.kaiyu.wiki.interceptor;
//
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// import org.springframework.web.servlet.HandlerInterceptor;
// import org.springframework.web.servlet.ModelAndView;
//
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
//
// /**
//  * Interceptor: A feature unique to the Spring framework, commonly used for login verification, permission verification, and request log printing. /login
//  */
// @Component
// public class LogInterceptor implements HandlerInterceptor {
//
//     private static final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);
//
//     @Override
//     public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//         // Print request information
//         LOG.info("------------- LogInterceptor start -------------");
//         LOG.info("Request address: {} {}", request.getRequestURL().toString(), request.getMethod());
//         LOG.info("Remote address: {}", request.getRemoteAddr());
//
//         long startTime = System.currentTimeMillis();
//         request.setAttribute("requestStartTime", startTime);
//         return true;
//     }
//
//     @Override
//     public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//         long startTime = (Long) request.getAttribute("requestStartTime");
//         LOG.info("------------- LogInterceptor Finished. Time elapsed:：{} ms -------------", System.currentTimeMillis() - startTime);
//     }
// }
