package com.money.Filter;


import com.money.SpringSession.SessionRepositoryRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by liumin on 16/2/17.
 */

public class HttpSessionFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        SessionRepositoryRequestWrapper customRequest =
                new SessionRepositoryRequestWrapper(httpRequest);

        filterChain.doFilter(customRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
