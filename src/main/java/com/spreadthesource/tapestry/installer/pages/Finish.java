package com.spreadthesource.tapestry.installer.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;

import com.spreadthesource.tapestry.installer.InternalConstants;

public class Finish
{
    @Inject
    private Request request;

    public String getRestartUri()
    {
        return request.getContextPath() + InternalConstants.RESTART_URI;
    }
}
