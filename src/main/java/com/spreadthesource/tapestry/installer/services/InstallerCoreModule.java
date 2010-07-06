package com.spreadthesource.tapestry.installer.services;

import java.io.IOException;

import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;

import com.spreadthesource.tapestry.installer.InstallerConstants;

public class InstallerCoreModule
{
    /**
     * Build application settings service.
     */
    public ApplicationSettings buildApplicationSettings(
            @Inject @Symbol(InstallerConstants.CONFIGURATION_FILEPATH) String configFilename,
            @Inject @Symbol(InstallerConstants.INSTALLER_VERSION) String installerVersion,
            RegistryShutdownHub shutdownHub) throws IOException
    {
        ApplicationSettingsImpl settings = new ApplicationSettingsImpl(configFilename,
                installerVersion);
        shutdownHub.addRegistryShutdownListener(settings);
        return settings;
    }
    
    public void contributeFactoryDefaults(MappedConfiguration<String, String> configuration,
            @Inject @Symbol(InternalSymbols.APP_NAME) String appName)
    {
        configuration.add(InstallerConstants.CONFIGURATION_FILEPATH, appName);
    }
}
