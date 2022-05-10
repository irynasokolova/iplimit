package com.sokolova.test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class IpLimitFilter implements Filter {
	@Autowired
	IpLimitCache cache;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			if (!cache.put(WebUtils.getClientIpAddressIfServletRequestExist())) {
				HttpServletResponse httpResponse = (HttpServletResponse) response;
				httpResponse.reset();
				httpResponse.setStatus(HttpStatus.BAD_GATEWAY.value());
				return;
			}
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		chain.doFilter(request, response);

	}
}
