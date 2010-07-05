package com.spreadthesource.tapestry.installer.services;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import com.spreadthesource.tapestry.installer.InstallerConstants;

public class TerminatorTaskImpl implements TerminatorTask
{
    private String terminatorPage;

    public TerminatorTaskImpl(
            @Inject @Symbol(InstallerConstants.TERMINATOR_PAGE) String terminatorPage)
    {
        this.terminatorPage = terminatorPage;
    }

    public String getStartPage()
    {
        return terminatorPage;
    }

    public Object getStartPageContext()
    {
        return null;
    }

    public boolean isConfigured()
    {
        return true;
    }

    public void rollback()
    {
        // nothing to do
    }

    public void run()
    {
        // nothing to do
    }

}
