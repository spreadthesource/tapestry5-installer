package com.spreadthesource.tapestry.installer.services;

import java.io.IOException;

import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

import com.spreadthesource.tapestry.installer.InternalConstants;

/**
 * Simply redirect to the restart URI.
 *
 * @author ccordenier
 *
 */
public class RestartResultProcessor implements ComponentEventResultProcessor<Restart>
{
    private final Request request;

    private final Response response;

    public RestartResultProcessor(Request request, Response response)
    {
        super();
        this.request = request;
        this.response = response;
    }

    public void processResultValue(Restart value) throws IOException
    {
        String path = request.getContextPath();
        path += InternalConstants.RESTART_URI;
        response.sendRedirect(path);
    }

}
