package com.spreadthesource.tapestry.installer.services;


/**
 * Will manage the process of configuring the application
 */
public interface ConfigurationManager
{    
    public boolean hasPrevious();
    public boolean hasNext();
    
    public ConfigurationTask getPreviousTask();
    public ConfigurationTask getCurrentTask();
    public ConfigurationTask getNextTask();
    
    /**
     * Configure 
     */
    public void configure();
    public void rollback();
    
    public void configureAll();
    public void rollbackAll(); 
}
