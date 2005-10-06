package org.apache.maven.plugins.release.helpers;

import org.apache.maven.model.Scm;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class ReleaseProgressTracker
{

    private static final String RELEASE_PROPERTIES = "release.properties";

    private static final String USERNAME = "maven.username";

    private static final String SCM_TAG = "scm.tag";

    private static final String SCM_URL = "scm.url";

    private static final String SCM_TAG_BASE = "scm.tag-base";

    private static final String SCM_PASSWORD = "scm.password";

    private static final String CHECKPOINT_PREFIX = "checkpoint.";

    private static final String SCM_INFO_PREFIX = "scm-info.";

    public static final String CP_INITIALIZED = "initialized";

    public static final String CP_LOCAL_MODIFICATIONS_CHECKED = "local-modifications-checked";

    public static final String CP_POM_TRANSFORMED_FOR_RELEASE = "transformed-pom-for-release";

    public static final String CP_GENERATED_RELEASE_POM = "generated-release-pom";

    public static final String CP_CHECKED_IN_RELEASE_VERSION = "checked-in-release-version";

    public static final String CP_TAGGED_RELEASE = "tagged-release";

    public static final String CP_POM_TRANSORMED_FOR_DEVELOPMENT = "transform-pom-for-development";

    public static final String CP_REMOVED_RELEASE_POM = "removed-release-pom";

    public static final String CP_CHECKED_IN_DEVELOPMENT_VERSION = "check-in-development-version";

    public static final String CP_PREPARED_RELEASE = "prepared-release";

    private Properties releaseProperties;

    private boolean resumeAtCheckpoint = false;

    private ReleaseProgressTracker( Properties properties )
    {
        this.releaseProperties = properties;
    }

    public static ReleaseProgressTracker loadOrCreate( String basedir )
        throws IOException
    {
        ReleaseProgressTracker tracker;

        if ( new File( basedir, RELEASE_PROPERTIES ).exists() )
        {
            tracker = load( basedir );
        }
        else
        {
            tracker = create();
        }

        return tracker;
    }

    public static ReleaseProgressTracker create()
    {
        return new ReleaseProgressTracker( new Properties() );
    }

    public static ReleaseProgressTracker load( String basedir )
        throws IOException
    {
        File releasePropertiesFile = new File( basedir, RELEASE_PROPERTIES );

        InputStream inStream = null;

        Properties rp;
        try
        {
            inStream = new FileInputStream( releasePropertiesFile );

            rp = new Properties();

            rp.load( inStream );
        }
        finally
        {
            IOUtil.close( inStream );
        }

        return new ReleaseProgressTracker( rp );
    }

    public static String getReleaseProgressFilename()
    {
        return RELEASE_PROPERTIES;
    }

    public void setUsername( String username )
    {
        releaseProperties.setProperty( USERNAME, username );
    }

    public String getUsername()
    {
        return releaseProperties.getProperty( USERNAME );
    }

    public void setScmTag( String scmTag )
    {
        releaseProperties.setProperty( SCM_TAG, scmTag );
    }

    public String getScmTag()
    {
        return releaseProperties.getProperty( SCM_TAG );
    }

    public void setScmUrl( String scmUrl )
    {
        releaseProperties.setProperty( SCM_URL, scmUrl );
    }

    public String getScmUrl()
    {
        return releaseProperties.getProperty( SCM_URL );
    }

    public void setScmTagBase( String tagBase )
    {
        releaseProperties.setProperty( SCM_TAG_BASE, tagBase );
    }

    public String getScmTagBase()
    {
        return releaseProperties.getProperty( SCM_TAG_BASE );
    }

    public void setPassword( String password )
    {
        releaseProperties.setProperty( SCM_PASSWORD, password );
    }

    public String getPassword()
    {
        return releaseProperties.getProperty( SCM_PASSWORD );
    }

    public void checkpoint( String basedir, String pointName )
        throws IOException
    {
        setCheckpoint( pointName );

        File releasePropertiesFile = new File( basedir, RELEASE_PROPERTIES );

        FileOutputStream outStream = null;

        try
        {
            outStream = new FileOutputStream( releasePropertiesFile );

            releaseProperties.store( outStream, "Generated by Release Plugin on: " + new Date() );
        }
        finally
        {
            IOUtil.close( outStream );
        }
    }

    private void setCheckpoint( String pointName )
    {
        releaseProperties.setProperty( CHECKPOINT_PREFIX + pointName, "OK" );
    }

    public boolean verifyCheckpoint( String pointName )
    {
        return resumeAtCheckpoint && "OK".equals( releaseProperties.getProperty( CHECKPOINT_PREFIX + pointName ) );
    }

    public void setResumeAtCheckpoint( boolean resumeAtCheckpoint )
    {
        this.resumeAtCheckpoint = resumeAtCheckpoint;
    }

    public void addOriginalScmInfo( String projectId, Scm scm )
    {
        String connection = scm.getConnection();
        if ( connection != null )
        {
            releaseProperties.setProperty( SCM_INFO_PREFIX + projectId + ".connection", connection );
        }

        String devConnection = scm.getDeveloperConnection();
        if ( devConnection != null )
        {
            releaseProperties.setProperty( SCM_INFO_PREFIX + projectId + ".developerConnection", devConnection );
        }

        String url = scm.getUrl();
        if ( url != null )
        {
            releaseProperties.setProperty( SCM_INFO_PREFIX + projectId + ".url", url );
        }

        String tag = scm.getTag();
        if ( tag != null )
        {
            releaseProperties.setProperty( SCM_INFO_PREFIX + projectId + ".tag", tag );
        }
    }

    public void restoreScmInfo( String projectId, Scm scm )
    {
        String connection = releaseProperties.getProperty( SCM_INFO_PREFIX + projectId + ".connection" );
        if ( connection != null )
        {
            scm.setConnection( connection );
        }

        String devConnection = releaseProperties.getProperty( SCM_INFO_PREFIX + projectId + ".developerConnection" );
        if ( devConnection != null )
        {
            scm.setDeveloperConnection( devConnection );
        }

        String url = releaseProperties.getProperty( SCM_INFO_PREFIX + projectId + ".url" );
        if ( url != null )
        {
            scm.setUrl( url );
        }

        String tag = releaseProperties.getProperty( SCM_INFO_PREFIX + projectId + ".tag" );
        if ( tag != null )
        {
            scm.setTag( tag );
        }
    }
}
