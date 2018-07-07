package br.com.senai.collabtrack.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter implements Filter{

	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:8585");
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
    	httpResponse.setHeader("Access-Control-Allow-Headers", "X-Auth-Token, Content-Type");
        httpResponse.setHeader("Access-Control-Expose-Headers", "custom-header1, custom-header2");
    	httpResponse.setHeader("Access-Control-Allow-Credentials", "false");
        httpResponse.setHeader("Access-Control-Max-Age", "4800");
        chain.doFilter(request, response);
		
	}
	
	public void destroy() {
		
	}
	
}
