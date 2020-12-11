package com.thelak.core.filters;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpServletRequestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest requestWrapper = new MultiReadHttpServletRequest((HttpServletRequest) servletRequest);
        chain.doFilter(requestWrapper, servletResponse);
    }

    @Override
    public void destroy() {
    }
}
