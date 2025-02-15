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
package org.neo4j.driver.internal.retry;

import java.io.Serializable;

public final class RetrySettings implements Serializable
{
    private static final long serialVersionUID = -2895062473220745239L;

    public static final RetrySettings DEFAULT =
            new RetrySettings( ExponentialBackoffRetryLogic.DEFAULT_MAX_RETRY_TIME_MS );

    private final long maxRetryTimeMs;

    public RetrySettings( long maxRetryTimeMs )
    {
        this.maxRetryTimeMs = maxRetryTimeMs;
    }

    public long maxRetryTimeMs()
    {
        return maxRetryTimeMs;
    }
}
