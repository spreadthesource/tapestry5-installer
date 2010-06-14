package com.spreadthesource.tapestry.installer;

public abstract class InstallerConstants
{
    /**
     * Define your current installer version number, if the current installation is equal to the one
     * provided by your application, then the installation application is not started.
     */
    public static final String INSTALLER_VERSION = "sps.installer-version";

    /**
     * Can be use to get the previous installer version if needed from your installation
     * application.
     */
    public static final String PREVIOUS_INSTALLER_VERSION = "sps.previous-installer-version";
    
    /**
     * Use to set the configuration filename
     */
    public static final String CONFIGURATION_FILEPATH = "sps.installer-filename";
    
}
