package com.spreadthesource.tapestry.installer;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.services.HttpServletRequestHandler;

/**
 * This filter is only in charge of getting a fresh registry after installation process and handle
 * request.
 * 
 * @author ccordenier
 */
public class TapestryTerminatorFilter implements Filter
{
    private HttpServletRequestHandler handler;

    private Registry registry;

    private ServletContext context;

    public void destroy()
    {
        // Nothing to do here this is only a terminator with no logic inside
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {

        // Lazy init registry
        if (registry == null)
        {
            registry = (Registry) context
                    .getAttribute(TapestryDelayedFilter.REGISTRY_CONTEXT_NAME);
            if (registry != null)
            {
                handler = registry.getService(HttpServletRequestHandler.class);
            }
        }

        // Check if tapestry handler exists
        if (handler == null)
        {
            chain.doFilter(request, response);
            return;
        }

        try
        {
            boolean handled = handler.service(
                    (HttpServletRequest) request,
                    (HttpServletResponse) response);

            if (!handled) chain.doFilter(request, response);
        }
        finally
        {
            registry.cleanupThread();
        }

    }

    public void init(FilterConfig config) throws ServletException
    {
        context = config.getServletContext();
    }

}
