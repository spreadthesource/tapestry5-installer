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

import com.spreadthesource.tapestry.installer.InstallerConstants;

public class ApplicationSettingsImpl implements ApplicationSettings, RegistryShutdownListener
{
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
            @Inject @Symbol(InstallerConstants.CONFIGURATION_FILEPATH) String configFilePath,
            @Inject @Symbol(SymbolConstants.APPLICATION_VERSION) String installerVersion)
            throws IOException
    {
        this.properties = new Properties();
        
       
        int folderx = configFilePath.lastIndexOf(System.getProperty("file.separator"));
        
        if (folderx < 0) 
            throw new RuntimeException("File path must at least contains one file separator : " + System.getProperty("file.separator"));
        
        String folderPath = configFilePath.substring(0, folderx);
        String fileName = configFilePath.substring(folderx + 1);
        
        File folder = new File(folderPath);
        File propertyFile = new File(folderPath, fileName);
        
        if (!propertyFile.exists())
        {
            if (folder.canWrite() && folder.canRead())
            {
                this.config = propertyFile;
                this.properties.put(InstallerConstants.INSTALLER_VERSION, installerVersion);
                this.alreadyInstalled = false;
            }
            else
            {
                throw new IOException("Cannot write nor read the configuration in "
                        + folder);
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
                                        "Cannot find installer version, configuration file '%s' is maybe corrupt.",
                                        this.config.getPath()));
            }
        }

    }

    public String get(String key)
    {
        return this.properties.getProperty(key);
    }


    public boolean containsKey(String key)
    {
        return this.properties.containsKey(key);
    }

    public String valueForSymbol(String symbolName)
    {
        return this.get(symbolName);
    }

    public void put(String key, String value)
    {
        if (value == null)
            value = "";
        this.properties.put(key, value);
    }

    /**
     * The properties will be stored to the disk.
     */
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
            throw new FileNotFoundException("Cannot write nor read the configuration in "
                    + this.config.getAbsolutePath());
        }
    }


    public boolean alreadyInstalled()
    {
        return this.alreadyInstalled;
    }

}
