package com.spreadthesource.tapestry.installer.services;

import org.apache.tapestry5.ioc.services.SymbolProvider;

/**
 * This service is responsible to get all the parameters during the installation process and write
 * it to the file system once the application process is finished.
 * 
 * @author ccordenier
 */
public interface ApplicationSettings extends SymbolProvider
{

    /**
     * Put a configuration value.
     * 
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * Get a configuration value
     * 
     * @param key
     * @return
     */
    String get(String key);

    /**
     * Verify if the corresponding exists in the application settings.
     * 
     * @param key
     * @return
     */
    boolean containsKey(String key);

    /**
     * Return true if the current version of the installer application version corresponds to the
     * one found in the configuration file.
     * 
     * @return
     */
    boolean alreadyInstalled();

}
