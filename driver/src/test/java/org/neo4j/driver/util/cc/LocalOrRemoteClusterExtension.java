/*
 * Copyright (c) "Neo4j"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.neo4j.driver.util.cc;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.Neo4jContainer;

import java.net.URI;
import java.util.Optional;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.util.TestUtil;

public class LocalOrRemoteClusterExtension implements BeforeAllCallback, AfterEachCallback, AfterAllCallback
{
    private static final String CLUSTER_URI_SYSTEM_PROPERTY_NAME = "externalClusterUri";
    private static final String NEO4J_USER_PASSWORD_PROPERTY_NAME = "neo4jUserPassword";

    private Neo4jContainer<?> neo4jContainer;
    private URI clusterUri;

    public LocalOrRemoteClusterExtension()
    {
        assertValidSystemPropertiesDefined();
    }

    public URI getClusterUri()
    {
        return clusterUri;
    }

    public AuthToken getAuthToken()
    {
        if ( remoteClusterExists() )
        {
            return AuthTokens.basic( "neo4j", neo4jUserPasswordFromSystemProperty() );
        }
        return AuthTokens.basic( "neo4j", neo4jContainer.getAdminPassword() );
    }

    @Override
    public void beforeAll( ExtensionContext context )
    {
        if ( remoteClusterExists() )
        {
            clusterUri = remoteClusterUriFromSystemProperty();
            cleanDb();
        }
        else
        {
            String neo4JVersion = Optional.ofNullable( System.getenv( "NEO4J_VERSION" ) )
                                          .orElse( "4.4" );
            neo4jContainer = new Neo4jContainer<>( String.format( "neo4j:%s-enterprise", neo4JVersion ) )
                    .withEnv( "NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes" );
            neo4jContainer.start();

            clusterUri = URI.create( neo4jContainer.getBoltUrl().replace( "bolt://", "neo4j://" ) );
        }
    }

    @Override
    public void afterEach( ExtensionContext context )
    {
        cleanDb();
    }

    @Override
    public void afterAll( ExtensionContext context )
    {
        if ( !remoteClusterExists() )
        {
            neo4jContainer.stop();
        }
    }

    private void cleanDb()
    {
        Config.ConfigBuilder builder = Config.builder();
        builder.withEventLoopThreads( 1 );

        try ( Driver driver = GraphDatabase.driver( getClusterUri(), getAuthToken(), builder.build() ) )
        {
            TestUtil.cleanDb( driver );
        }
    }

    private static void assertValidSystemPropertiesDefined()
    {
        URI uri = remoteClusterUriFromSystemProperty();
        String password = neo4jUserPasswordFromSystemProperty();
        if ( (uri != null && password == null) || (uri == null && password != null) )
        {
            throw new IllegalStateException(
                    "Both cluster uri and 'neo4j' user password system properties should be set. " +
                    "Uri: '" + uri + "', Password: '" + password + "'" );
        }
    }

    private static boolean remoteClusterExists()
    {
        return remoteClusterUriFromSystemProperty() != null;
    }

    private static URI remoteClusterUriFromSystemProperty()
    {
        String uri = System.getProperty( CLUSTER_URI_SYSTEM_PROPERTY_NAME );
        return uri == null ? null : URI.create( uri );
    }

    private static String neo4jUserPasswordFromSystemProperty()
    {
        return System.getProperty( NEO4J_USER_PASSWORD_PROPERTY_NAME );
    }
}
