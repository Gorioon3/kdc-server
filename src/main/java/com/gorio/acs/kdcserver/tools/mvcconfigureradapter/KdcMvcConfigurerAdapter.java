package com.gorio.acs.kdcserver.tools.mvcconfigureradapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Class Name KdcMvcConfigurerAdapter
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/27
 */
@Slf4j
public class KdcMvcConfigurerAdapter extends WebMvcConfigurerAdapter {
    private static final int LEGAL_LOGO_LENGTH = 6;
    /**
     * 自定义消息拦截器
     * @param registry  InterceptorRegistry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        HandlerInterceptor handlerInterceptor = new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                String[] starts = {"/apps/","/clients","/servers"};
                String requestURI = request.getRequestURI();
                log.info("\n===========================拦截器生效中===========================");
                log.info("收到请求：{}",requestURI);
                boolean b=false;
                //"/apps/"
                if (requestURI.startsWith(starts[0])) {
                    //如果args为空
                    //如果args的长度比6小
                     b= !"".equals(requestURI.substring(starts[0].length()))
                            && requestURI.substring(starts[0].length()).length() > LEGAL_LOGO_LENGTH;

                }
                else if (requestURI.startsWith(starts[1])) {
                    b= !"".equals(requestURI.substring(starts[1].length()))
                            && requestURI.substring(starts[1].length()).length() > LEGAL_LOGO_LENGTH;
                } else {
                    b= requestURI.startsWith(starts[2]) && !"".equals(requestURI.substring(starts[2].length()))
                            && requestURI.substring(starts[2].length()).length() > LEGAL_LOGO_LENGTH;
                }
                log.info("拦截结果{}",b);
                return b;
            }

            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                                   ModelAndView modelAndView) throws Exception {

            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                        Exception ex) throws Exception {
            }
        };
        registry.addInterceptor(handlerInterceptor).addPathPatterns("/**");
    }


    /**
     * 自定义消息转化器
     * @param converters List<HttpMessageConverter<?>>
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter converter  = new StringHttpMessageConverter(Charset.forName("UTF-8"));
        converters.add(converter);
    }


}
