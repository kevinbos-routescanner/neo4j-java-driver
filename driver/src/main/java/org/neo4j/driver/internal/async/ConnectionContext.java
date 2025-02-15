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
package org.neo4j.driver.internal.async;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.neo4j.driver.AccessMode;
import org.neo4j.driver.Bookmark;
import org.neo4j.driver.internal.DatabaseName;
import org.neo4j.driver.internal.spi.ConnectionProvider;

/**
 * Describes what kind of connection to return by {@link ConnectionProvider}
 */
public interface ConnectionContext
{
    Supplier<IllegalStateException> PENDING_DATABASE_NAME_EXCEPTION_SUPPLIER = () -> new IllegalStateException( "Pending database name encountered." );

    CompletableFuture<DatabaseName> databaseNameFuture();

    AccessMode mode();

    Set<Bookmark> rediscoveryBookmarks();

    String impersonatedUser();
}
