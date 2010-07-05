package com.spreadthesource.tapestry.installer.services;

import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ComponentEventResultProcessor;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.LibraryMapping;

import com.spreadthesource.tapestry.installer.InstallerConstants;

/**
 * Contains installer contributions.
 */
@SubModule(InstallerCoreModule.class)
public class InstallerModule
{
    @Startup
    public static void initApplication(
            @Inject @Symbol(InstallerConstants.SILENT_MODE) boolean silentMode,
            ConfigurationManager manager)
    {
        if (silentMode)
        {
            manager.configureAll();
        }

    }

    public static void bind(ServiceBinder binder)
    {
        binder.bind(ConfigurationManager.class, ConfigurationManagerImpl.class);
        binder.bind(TerminatorTask.class, TerminatorTaskImpl.class);
    }

    public static void contributeMasterDispatcher(OrderedConfiguration<Dispatcher> configuration)
    {
        configuration
                .overrideInstance("RootPath", InstallerStarterDispatcher.class, "before:Asset");
    }

   
    public void contributeFactoryDefaults(MappedConfiguration<String, String> configuration,
            @Inject @Symbol(InternalSymbols.APP_NAME) String appName)
    {
        configuration.add(InstallerConstants.SILENT_MODE, "false");

        configuration.add(InstallerConstants.TERMINATOR_PAGE, "installer/finish");
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
    
    /**
     * Contribute here with all your configuration task your application
     */
    public static void contributeConfigurationManager(OrderedConfiguration<ConfigurationTask> configuration, @Inject TerminatorTask terminator) {
        configuration.add("Terminator", terminator, "after:*");
    }

    public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration)
    {
        configuration
                .add(new LibraryMapping("installer", "com.spreadthesource.tapestry.installer"));
    }
}
