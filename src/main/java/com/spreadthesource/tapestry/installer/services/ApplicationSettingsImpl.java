package com.spreadthesource.tapestry.installer.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.TapestryException;
import org.apache.tapestry5.ioc.services.RegistryShutdownListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spreadthesource.tapestry.installer.InstallerConstants;

public class ApplicationSettingsImpl implements ApplicationSettings, RegistryShutdownListener
{

    private final Logger logger = LoggerFactory.getLogger(ApplicationSettings.class);

    private final Properties properties;

    private final File config;

    private final boolean alreadyInstalled;

    /**
     * Prepare properties with existing or create a new one.
     * 
     * @param appName
     * @param installerVersion
     * @throws FileNotFoundException
     */
    public ApplicationSettingsImpl(
            @Inject @Symbol(InstallerConstants.CONFIGURATION_FILENAME) String configFilename,
            @Inject @Symbol(SymbolConstants.APPLICATION_VERSION) String installerVersion)
            throws IOException
    {
        this.properties = new Properties();

        File userHome = new File(System.getProperty("user.home"));
        File propertyFile = new File(System.getProperty("user.home"), "." + configFilename + ".cfg");
        if (!propertyFile.exists())
        {
            if (userHome.canWrite() && userHome.canRead())
            {
                this.config = propertyFile;
                this.properties.put(InstallerConstants.INSTALLER_VERSION, installerVersion);
                this.alreadyInstalled = false;
            }
            else
            {
                throw new IOException("Cannot write nor read the wooki configuration in "
                        + System.getProperty("user.home"));
            }
        }
        else
        {
            // Load
            this.config = propertyFile;
            this.load();

            // Check installer version
            if (this.properties.containsKey(InstallerConstants.INSTALLER_VERSION))
            {
                if (this.properties.get(InstallerConstants.INSTALLER_VERSION).equals(
                        installerVersion))
                {
                    this.alreadyInstalled = true;
                }
                else
                {
                    // Set the new version and keep the previous one
                    this.properties.put(
                            InstallerConstants.PREVIOUS_INSTALLER_VERSION,
                            this.properties.get(InstallerConstants.INSTALLER_VERSION));
                    this.properties.put(InstallerConstants.INSTALLER_VERSION, installerVersion);
                    this.alreadyInstalled = false;
                }
            }
            else
            {
                throw new IllegalStateException(
                        String
                                .format(
                                        "Cannot find installer version, Wooki configuration file '%s' is maybe corrupt.",
                                        this.config.getPath()));
            }
        }

    }

    @Override
    public String get(String key)
    {
        return this.properties.getProperty(key);
    }

    @Override
    public String valueForSymbol(String symbolName)
    {
        return this.get(symbolName);
    }

    @Override
    public void put(String key, String value)
    {
        this.properties.put(key, value);
    }

    /**
     * The properties will be stored to the disk.
     */
    @Override
    public void registryDidShutdown()
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(this.config);
            this.properties.store(fos, null);
        }
        catch (Exception e)
        {
            throw new TapestryException("Error writing configuration to disk", e);
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    throw new TapestryException("Error writing configuration to disk", e);
                }
            }
        }

    }

    /**
     * Simply load existing properties and let the server restart
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void load() throws FileNotFoundException, IOException
    {
        if (this.config.canWrite() && this.config.canRead())
        {
            FileInputStream fis = new FileInputStream(this.config);
            try
            {
                this.properties.load(fis);
            }
            finally
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
        }
        else
        {
            throw new FileNotFoundException("Cannot write nor read the wooki configuration in "
                    + System.getProperty("user.home"));
        }
    }

    @Override
    public boolean alreadyInstalled()
    {
        return this.alreadyInstalled;
    }

}
