/*
 * Copyright (c) 2002-2019 "Neo4j,"
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
package org.neo4j.driver.internal.async.pool;

import io.netty.bootstrap.Bootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import org.neo4j.driver.internal.BoltServerAddress;
import org.neo4j.driver.internal.ConnectionSettings;
import org.neo4j.driver.internal.async.BootstrapFactory;
import org.neo4j.driver.internal.async.ChannelConnector;
import org.neo4j.driver.internal.async.ChannelConnectorImpl;
import org.neo4j.driver.internal.security.SecurityPlan;
import org.neo4j.driver.internal.spi.Connection;
import org.neo4j.driver.internal.util.FakeClock;
import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.neo4j.driver.v1.util.DatabaseExtension;
import org.neo4j.driver.v1.util.ParallelizableIT;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.junit.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.neo4j.driver.internal.logging.DevNullLogging.DEV_NULL_LOGGING;
import static org.neo4j.driver.internal.metrics.InternalAbstractMetrics.DEV_NULL_METRICS;
import static org.neo4j.driver.v1.util.TestUtil.await;

@ParallelizableIT
class ConnectionPoolImplIT
{
    @RegisterExtension
    static final DatabaseExtension neo4j = new DatabaseExtension();

    private ConnectionPoolImpl pool;

    @BeforeEach
    void setUp() throws Exception
    {
        pool = newPool();
    }

    @AfterEach
    void tearDown()
    {
        pool.close();
    }

    @Test
    void shouldAcquireConnectionWhenPoolIsEmpty()
    {
        Connection connection = await( pool.acquire( neo4j.address() ) );

        assertNotNull( connection );
    }

    @Test
    void shouldAcquireIdleConnection()
    {
        Connection connection1 = await( pool.acquire( neo4j.address() ) );
        await( connection1.release() );

        Connection connection2 = await( pool.acquire( neo4j.address() ) );
        assertNotNull( connection2 );
    }

    @Test
    void shouldFailToAcquireConnectionToWrongAddress()
    {
        ServiceUnavailableException e = assertThrows( ServiceUnavailableException.class,
                () -> await( pool.acquire( new BoltServerAddress( "wrong-localhost" ) ) ) );

        assertThat( e.getMessage(), startsWith( "Unable to connect" ) );
    }

    @Test
    void shouldFailToAcquireWhenPoolClosed()
    {
        Connection connection = await( pool.acquire( neo4j.address() ) );
        await( connection.release() );
        await( pool.close() );

        IllegalStateException e = assertThrows( IllegalStateException.class, () -> pool.acquire( neo4j.address() ) );
        assertThat( e.getMessage(), startsWith( "Pool closed" ) );
    }

    @Test
    void shouldNotCloseWhenClosed()
    {
        assertNull( await( pool.close() ) );
        assertTrue( pool.close().toCompletableFuture().isDone() );
    }

    @Test
    void shouldFailToAcquireConnectionWhenPoolIsClosed()
    {
        await( pool.acquire( neo4j.address() ) );
        ExtendedChannelPool channelPool = this.pool.getPool( neo4j.address() );
        channelPool.close();
        ServiceUnavailableException error =
                assertThrows( ServiceUnavailableException.class, () -> await( pool.acquire( neo4j.address() ) ) );
        assertThat( error.getMessage(), containsString( "closed while acquiring a connection" ) );
        assertThat( error.getCause(), instanceOf( IllegalStateException.class ) );
        assertThat( error.getCause().getMessage(), containsString( "FixedChannelPooled was closed" ) );
    }

    private ConnectionPoolImpl newPool() throws Exception
    {
        FakeClock clock = new FakeClock();
        ConnectionSettings connectionSettings = new ConnectionSettings( neo4j.authToken(), 5000 );
        ChannelConnector connector = new ChannelConnectorImpl( connectionSettings, SecurityPlan.forAllCertificates( false ),
                DEV_NULL_LOGGING, clock );
        PoolSettings poolSettings = newSettings();
        Bootstrap bootstrap = BootstrapFactory.newBootstrap( 1 );
        return new ConnectionPoolImpl( connector, bootstrap, poolSettings, DEV_NULL_METRICS, DEV_NULL_LOGGING, clock );
    }
    private static PoolSettings newSettings()
    {
        return new PoolSettings( 10, 5000, -1, -1 );
    }
}
