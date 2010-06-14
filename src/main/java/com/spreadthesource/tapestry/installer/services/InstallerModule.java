package com.spreadthesource.tapestry.installer.services;

import java.io.IOException;

import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.services.ComponentEventResultProcessor;

import com.spreadthesource.tapestry.installer.InstallerConstants;

/**
 * Contains installer contributions.
 *
 * @author ccordenier
 *
 */
public class InstallerModule
{

    /**
     * Build application settings service.
     * 
     * @param installerVersion
     * @param binder
     * @param shutdownHub
     * @return
     * @throws IOException
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

    /**
     * Set factory defaults symbol values.
     * 
     * @param configuration
     * @param appName
     */
    public void contributeFactoryDefaults(MappedConfiguration<String, String> configuration,
            @Inject @Symbol(InternalSymbols.APP_NAME) String appName)
    {
        configuration.add(InstallerConstants.CONFIGURATION_FILEPATH, appName);
    }

    /**
     * Handle restart request when installation process is finished. 
     *
     * @param configuration
     */
    public void contributeComponentEventResultProcessor(
            MappedConfiguration<Class, ComponentEventResultProcessor> configuration)
    {
        configuration.addInstance(Restart.class, RestartResultProcessor.class);
    }

}
