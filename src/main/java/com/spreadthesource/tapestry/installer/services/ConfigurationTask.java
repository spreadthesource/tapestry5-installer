package com.spreadthesource.tapestry.installer.services;

public interface ConfigurationTask
{
    public boolean isConfigured();
    
    public String getStartPage();
    
    public Object getStartPageContext();
    
    public void run();
    
    public void rollback();
}
