package com.pomelo.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 允许跨域访问
 * @author Jeffrey
 * @since 2017年1月10日 上午11:16:30
 */
public class AllowCrossAccessFilter extends HttpServlet implements Filter {
	private static final long serialVersionUID = 1L;

	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		String originHeader = request.getHeader("Origin");
		response.setHeader("Access-Control-Allow-Credentials", "true");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type");
		response.setHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE");
		response.setHeader("Access-Control-Allow-Origin", originHeader);
		chain.doFilter(req, response);
	}

	public void init(FilterConfig filterConfig) {
	}
}