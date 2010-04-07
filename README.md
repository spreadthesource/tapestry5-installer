# Tapestry 5 Application Installer

This contribution aims to provide an easy way for Tapestry developers to include an installation application in their application so
the user can provide its production environment configuration values. Once the installation process is finished then the 'real' application 
will be started and the user will be redirected to '/'

NO SERVER RESTART is needed, the real application startup is simply deferred after the configuration process.

## How to

To use this contribution, first refer to the Maven dependency chapter to retrieve the JAR files.

Once your pom.xml is configured you can start to build your installation application. 

Let's say we have two packages :

* com.wooki: this is the root package of the real application
* com.wooki.installer: this is the root package of the installation application

### Configure your web.xml

	<!-- Tapestry configuration -->
	<context-param>
		<param-name>tapestry.app-package</param-name>
		<param-value>com.wooki</param-value>
	</context-param>

	<context-param>
		<param-name>tapestry.installer-package</param-name>
		<param-value>com.wooki.installer</param-value>
	</context-param>

	<filter>
		<filter-name>wooki</filter-name>
		<filter-class>com.spreadthesource.tapestry.installer.TapestryDelayedFilter
		</filter-class>
	</filter>
	
	<filter>
		<filter-name>terminator</filter-name>
		<filter-class>com.spreadthesource.tapestry.installer.TapestryTerminatorFilter
		</filter-class>
	</filter>

	<filter-mapping>
		<filter-name>wooki</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

	<!-- Here you can add filters that will executed before your effective application requests -->

	<filter-mapping>
		<filter-name>terminator</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>

As you can see, the contribution is made of two filters, the first one stop all the requests till the application has
not finished its installation process.

The second one simply handles request once the application is configured.

Also their is a new symbol to set for the installation application : 'tapestry.installer-package'

### Create an InstallerModule

The installation application can contain a module that must be called InstallerModule, you must set here the version number
of your installation application, i.e. 

	public void contributeApplicationDefaults(MappedConfiguration<String, String> configuration)
    {
        configuration.add(InstallerConstants.INSTALLER_VERSION, "1.0");
    }

Note that the version number is important during server restart and must only evolve if you have new configuration properties to set.

### Add configuration properties via the installation application

It's easy as injecting the ApplicationSettings service in your page and call 'put' method, i.e. for a simple form based configuration :

	@Inject
    private ApplicationSettings settings;

	@Property
	private String userSuppliedUrl;

    @OnEvent(value = EventConstants.SUCCESS)
    public void addConfiguration()
    {
        settings.put("hibernate.connection.url", userSuppliedUrl);
        ...
    }
    
where userSuppliedUrl has been build by the installation application or directly supplied in a form by the user.

### Once all the settings has been set successfully by the user

You can can tell the filter to load your 'real' application by simply returning a 'Restart' instance from your success method :

	@OnEvent(value = EventConstants.SUCCESS)
    public Object addConfiguration()
    {
        settings.put("hibernate.connection.url", userSuppliedUrl);
        ...
        return new Restart();
    }

### For spring users

For developer who use Spring, simply use the corresponding SpringFilter :

	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath*:applicationContext*.xml</param-value>
	</context-param>

	<filter>
		<filter-name>wooki</filter-name>
		<filter-class>com.spreadthesource.tapestry.installer.TapestrySpringDelayedFilter
		</filter-class>
	</filter> 

Also, you can use our TapestryPropertyPlaceholderConfigurer class so once the 'real' application is started, spring configuration
files will be aware of what has been set by the user.

	<bean id="project-properties"
		class="com.spreadthesource.tapestry.installer.config.TapestryPropertyPlaceholderConfigurer">
		<property name="ignoreUnresolvablePlaceholders">
			<value>true</value>
		</property>
		<property name="locations">
			<!-- Additionnal locations -->
		</property>
	</bean>

### In development mode

In development mode we use to provide a pre-installed application with in memory db... To directly start the 'real' application
simple add a context-param in your web.xml file

	<context-param>
		<param-name>tapestry.production-mode</param-name>
		<param-value>false</param-value>
	</context-param>

## FAQ

### What to do once the configuration process is finsihed ?

This is up to the developer to handle the initialization of its services using the ApplicationSettings service to extract symbol values.
This can be done via an eager loaded service, or a ServletApplicationInitializerFilter.

### Where the contribution stores the installation related informations ?

Everything is stored in the user.home directory in a configuration called '.xxx.cfg' where xxx is the name
that you have given to the Tapestry Filter ('wooki' in our case). You can give another name to avoid conflicts by setting 
InstallerConstants.CONFIGURATION_FILENAME symbol.

### Why all the modules are not loaded for the installation application ?

Actually, during installation application start only a minimal set of Tapestry modules are loaded, you have to explicitly declare
the required modules via the @SubModule annotation in your InstallerModule class.

### What happens on server restart, How to upgrade the installation application ?

The filter check if your installation application version correspond to the last recently used, if it differs then the installation
application is started.

## Maven dependency

To use this plugin, add the following dependency in your `pom.xml`.

	<dependencies>
		...
		<dependency>
			<groupId>com.spreadthesource</groupId>
			<artifactId>tapestry5-</artifactId>
			<version>1.0.0-SNAPSHOT</version>
		</dependency>
		...
	</dependencies>
	
	<repositories>
		...
		<repository>
			<id>devlab722-repo</id>
			<url>http://nexus.devlab722.net/nexus/content/repositories/releases
			</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>

		<repository>
			<id>devlab722-snapshot-repo</id>
			<url>http://nexus.devlab722.net/nexus/content/repositories/snapshots
			</url>
			<releases>
				<enabled>false</enabled>
			</releases>
		</repository>
		
		...
	</repositories>

## More Informations & contacts

* Blog: http://spreadthesource.com
* Twitter: http://twitter.com/spreadthesource

## License

This project is distributed under Apache 2 License. See LICENSE.txt for more information.

