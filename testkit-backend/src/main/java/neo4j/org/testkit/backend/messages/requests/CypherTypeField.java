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
package neo4j.org.testkit.backend.messages.requests;

import lombok.Getter;
import lombok.Setter;
import neo4j.org.testkit.backend.CustomDriverError;
import neo4j.org.testkit.backend.messages.AbstractResultNext;
import neo4j.org.testkit.backend.messages.responses.BackendError;
import neo4j.org.testkit.backend.messages.responses.Field;
import neo4j.org.testkit.backend.messages.responses.TestkitResponse;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Entity;
import org.neo4j.driver.types.Path;

@Setter
@Getter
public class CypherTypeField extends AbstractResultNext
{
    private CypherTypeFieldBody data;

    @Override
    protected neo4j.org.testkit.backend.messages.responses.TestkitResponse createResponse( Record record )
    {
        Value value = record.get( data.getRecordKey() );
        String type = data.getType();
        String field = data.getField();
        String fieldValue = null;
        TestkitResponse testkitResponse = null;

        try
        {
            if ( "path".equals( type ) )
            {
                fieldValue = readPath( value.asPath() );
            }
            else if ( "node".equals( type ) )
            {
                fieldValue = readProperty( value.asNode(), field );
            }
            else if ( "relationship".equals( type ) )
            {
                fieldValue = readProperty( value.asRelationship(), field );
            }
            else
            {
                testkitResponse = BackendError.builder()
                                              .data( BackendError.BackendErrorBody.builder().msg( String.format( "Unexpected type %s", type ) ).build() )
                                              .build();
            }
        }
        catch ( Throwable t )
        {
            throw new CustomDriverError( t );
        }

        if ( testkitResponse == null )
        {
            testkitResponse = neo4j.org.testkit.backend.messages.responses.Field.builder()
                                                                                .data( Field.FieldBody.builder()
                                                                                                      .value( fieldValue )
                                                                                                      .build() )
                                                                                .build();
        }

        return testkitResponse;
    }

    @Override
    protected String getResultId()
    {
        return data.getResultId();
    }

    private String readPath( Path path ) throws Throwable
    {
        String[] parts = data.getField().split( "\\." );
        String propertyName = parts[0];
        int index = Integer.parseInt( parts[1] );
        String methodName = parts[2];

        Supplier<Iterable<? extends Entity>> iterableSupplier;
        if ( "nodes".equals( propertyName ) )
        {
            iterableSupplier = path::nodes;
        }
        else if ( "relationships".equals( propertyName ) )
        {
            iterableSupplier = path::relationships;
        }
        else
        {
            throw new RuntimeException( "Unexpected" );
        }

        Entity entity = getEntity( iterableSupplier.get(), index );
        return readProperty( entity, methodName );
    }

    private Entity getEntity( Iterable<? extends Entity> iterable, int index )
    {
        return StreamSupport.stream( iterable.spliterator(), false )
                            .skip( index > 0 ? index - 1 : index )
                            .findFirst()
                            .orElseThrow( IndexOutOfBoundsException::new );
    }

    private String readProperty( Entity value, String property ) throws Throwable
    {
        try
        {
            return String.valueOf( value.getClass().getMethod( property ).invoke( value ) );
        }
        catch ( InvocationTargetException e )
        {
            throw e.getTargetException();
        }
    }

    @Setter
    @Getter
    public static class CypherTypeFieldBody
    {
        private String resultId;
        private String recordKey;
        private String type;
        private String field;
    }
}
