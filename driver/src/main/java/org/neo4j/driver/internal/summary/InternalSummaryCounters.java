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
package org.neo4j.driver.internal.summary;

import org.neo4j.driver.summary.SummaryCounters;

public class InternalSummaryCounters implements SummaryCounters
{
    public static final InternalSummaryCounters EMPTY_STATS =
            new InternalSummaryCounters( 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 );
    private final int nodesCreated;
    private final int nodesDeleted;
    private final int relationshipsCreated;
    private final int relationshipsDeleted;
    private final int propertiesSet;
    private final int labelsAdded;
    private final int labelsRemoved;
    private final int indexesAdded;
    private final int indexesRemoved;
    private final int constraintsAdded;
    private final int constraintsRemoved;
    private final int systemUpdates;

    public InternalSummaryCounters(
            int nodesCreated, int nodesDeleted,
            int relationshipsCreated, int relationshipsDeleted,
            int propertiesSet,
            int labelsAdded, int labelsRemoved,
            int indexesAdded, int indexesRemoved,
            int constraintsAdded, int constraintsRemoved, int systemUpdates )
    {
        this.nodesCreated = nodesCreated;
        this.nodesDeleted = nodesDeleted;
        this.relationshipsCreated = relationshipsCreated;
        this.relationshipsDeleted = relationshipsDeleted;
        this.propertiesSet = propertiesSet;
        this.labelsAdded = labelsAdded;
        this.labelsRemoved = labelsRemoved;
        this.indexesAdded = indexesAdded;
        this.indexesRemoved = indexesRemoved;
        this.constraintsAdded = constraintsAdded;
        this.constraintsRemoved = constraintsRemoved;
        this.systemUpdates = systemUpdates;
    }

    @Override
    public boolean containsUpdates()
    {
        return
             isPositive( nodesCreated )
          || isPositive( nodesDeleted )
          || isPositive( relationshipsCreated )
          || isPositive( relationshipsDeleted )
          || isPositive( propertiesSet )
          || isPositive( labelsAdded )
          || isPositive( labelsRemoved )
          || isPositive( indexesAdded )
          || isPositive( indexesRemoved )
          || isPositive( constraintsAdded )
          || isPositive( constraintsRemoved );
    }

    @Override
    public int nodesCreated()
    {
        return nodesCreated;
    }

    @Override
    public int nodesDeleted()
    {
        return nodesDeleted;
    }

    @Override
    public int relationshipsCreated()
    {
        return relationshipsCreated;
    }

    @Override
    public int relationshipsDeleted()
    {
        return relationshipsDeleted;
    }

    @Override
    public int propertiesSet()
    {
        return propertiesSet;
    }

    @Override
    public int labelsAdded()
    {
        return labelsAdded;
    }

    @Override
    public int labelsRemoved()
    {
        return labelsRemoved;
    }

    @Override
    public int indexesAdded()
    {
        return indexesAdded;
    }

    @Override
    public int indexesRemoved()
    {
        return indexesRemoved;
    }

    @Override
    public int constraintsAdded()
    {
        return constraintsAdded;
    }

    @Override
    public int constraintsRemoved()
    {
        return constraintsRemoved;
    }

    @Override
    public boolean containsSystemUpdates()
    {
        return isPositive( systemUpdates );
    }

    @Override
    public int systemUpdates()
    {
        return systemUpdates;
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        InternalSummaryCounters that = (InternalSummaryCounters) o;

        return nodesCreated == that.nodesCreated
            && nodesDeleted == that.nodesDeleted
            && relationshipsCreated == that.relationshipsCreated
            && relationshipsDeleted == that.relationshipsDeleted
            && propertiesSet == that.propertiesSet
            && labelsAdded == that.labelsAdded
            && labelsRemoved == that.labelsRemoved
            && indexesAdded == that.indexesAdded
            && indexesRemoved == that.indexesRemoved
            && constraintsAdded == that.constraintsAdded
            && constraintsRemoved == that.constraintsRemoved
            && systemUpdates == that.systemUpdates;
    }

    @Override
    public int hashCode()
    {
        int result = nodesCreated;
        result = 31 * result + nodesDeleted;
        result = 31 * result + relationshipsCreated;
        result = 31 * result + relationshipsDeleted;
        result = 31 * result + propertiesSet;
        result = 31 * result + labelsAdded;
        result = 31 * result + labelsRemoved;
        result = 31 * result + indexesAdded;
        result = 31 * result + indexesRemoved;
        result = 31 * result + constraintsAdded;
        result = 31 * result + constraintsRemoved;
        result = 31 * result + systemUpdates;
        return result;
    }

    private boolean isPositive( int value )
    {
        return value > 0;
    }

    @Override
    public String toString()
    {
        return "InternalSummaryCounters{" +
               "nodesCreated=" + nodesCreated +
               ", nodesDeleted=" + nodesDeleted +
               ", relationshipsCreated=" + relationshipsCreated +
               ", relationshipsDeleted=" + relationshipsDeleted +
               ", propertiesSet=" + propertiesSet +
               ", labelsAdded=" + labelsAdded +
               ", labelsRemoved=" + labelsRemoved +
               ", indexesAdded=" + indexesAdded +
               ", indexesRemoved=" + indexesRemoved +
               ", constraintsAdded=" + constraintsAdded +
               ", constraintsRemoved=" + constraintsRemoved +
               ", systemUpdates=" + systemUpdates +
               '}';
    }
}
