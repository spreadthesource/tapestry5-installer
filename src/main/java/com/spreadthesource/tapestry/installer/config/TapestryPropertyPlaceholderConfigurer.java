package com.spreadthesource.tapestry.installer.config;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.web.context.ServletContextAware;

import com.spreadthesource.tapestry.installer.services.ApplicationSettings;

/**
 * This property place holder gives access to <code>ApplicationSettings</coder> service first.
 * 
 * @author ccordenier
 */
public class TapestryPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements
        ServletContextAware
{

    private ServletContext context;

    private ApplicationSettings settings;

    @Override
    protected String resolvePlaceholder(String placeholder, Properties props,
            int systemPropertiesMode)
    {
        if (settings == null)
        {
            Registry registry = (Registry) this.context
                    .getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
            if (registry != null)
            {
                this.settings = registry.getService(ApplicationSettings.class);
            }
        }

        // Check if settings exist
        if (settings != null)
        {
            if (this.settings.containsKey(placeholder))
            {
                String value = this.settings.get(placeholder);
                props.put(placeholder, value);
                return value;
            }
        }

        return super.resolvePlaceholder(placeholder, props, systemPropertiesMode);
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.context = servletContext;
    }

}
