package com.neo.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 通用配置，比如：filter,listener,servlet等
 * @author Jeffrey
 * @since 2017年2月15日 上午10:11:34
 */
@Configuration
public class WebConfig {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Bean
	public FilterRegistrationBean registFilter() {
		FilterRegistrationBean registration = new FilterRegistrationBean();
		registration.setFilter(new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {
				logger.info("过滤器 init!");
			}

			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
				HttpServletResponse httpResponse = (HttpServletResponse) response;  
				allowCrossAccess((HttpServletRequest) request, httpResponse);
				chain.doFilter(request, response);
			}

			@Override
			public void destroy() {
				logger.info("过滤器 destroy!");
			}

		});
		registration.addUrlPatterns("/*");
		registration.setOrder(1);
		return registration;
	}
	protected void allowCrossAccess(HttpServletRequest request, HttpServletResponse response) {
//        String allowOrigin = "*";
        String allowOrigin = request.getHeader("Origin");
        String allowMethods = "GET, PUT, POST, DELETE";
        String allowHeaders = "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With";
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.addHeader("Access-Control-Allow-Headers", allowHeaders);
        response.addHeader("Access-Control-Allow-Methods", allowMethods);
        response.addHeader("Access-Control-Allow-Origin", allowOrigin);
    }
}
