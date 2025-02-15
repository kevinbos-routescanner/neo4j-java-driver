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
package org.neo4j.driver.internal.async.connection;

import org.junit.jupiter.api.Test;

import org.neo4j.driver.internal.spi.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.neo4j.driver.AccessMode.READ;
import static org.neo4j.driver.internal.DatabaseNameUtil.defaultDatabase;

public class DirectConnectionTest
{
    @Test
    void shouldReturnServerAgent()
    {
        // given
        Connection connection = mock( Connection.class );
        DirectConnection directConnection = new DirectConnection( connection, defaultDatabase(), READ, null );
        String agent = "Neo4j/4.2.5";
        given( connection.serverAgent() ).willReturn( agent );

        // when
        String actualAgent = directConnection.serverAgent();

        // then
        assertEquals( agent, actualAgent );
        then( connection ).should().serverAgent();
    }
}
