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
import neo4j.org.testkit.backend.TestkitState;
import neo4j.org.testkit.backend.holder.SessionHolder;
import neo4j.org.testkit.backend.messages.responses.Bookmarks;
import neo4j.org.testkit.backend.messages.responses.TestkitResponse;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import org.neo4j.driver.Bookmark;

@Setter
@Getter
public class SessionLastBookmarks implements TestkitRequest
{
    private SessionLastBookmarksBody data;

    @Override
    public TestkitResponse process( TestkitState testkitState )
    {
        SessionHolder sessionHolder = testkitState.getSessionHolder( data.getSessionId() );
        return createResponse( sessionHolder.getSession().lastBookmarks() );
    }

    @Override
    public CompletionStage<TestkitResponse> processAsync( TestkitState testkitState )
    {
        return testkitState.getAsyncSessionHolder( data.getSessionId() )
                           .thenApply( sessionHolder -> sessionHolder.getSession().lastBookmarks() )
                           .thenApply( this::createResponse );
    }

    @Override
    public Mono<TestkitResponse> processRx( TestkitState testkitState )
    {
        return testkitState.getRxSessionHolder( data.getSessionId() )
                           .map( sessionHolder -> sessionHolder.getSession().lastBookmark() )
                           .map( bookmark -> bookmark.values().stream().map( Bookmark::from ).collect( Collectors.toSet() ) )
                           .map( this::createResponse );
    }

    @Override
    public Mono<TestkitResponse> processReactive( TestkitState testkitState )
    {
        return testkitState.getReactiveSessionHolder( data.getSessionId() )
                           .map( sessionHolder -> sessionHolder.getSession().lastBookmarks() )
                           .map( this::createResponse );
    }

    private Bookmarks createResponse( Set<Bookmark> bookmarks )
    {
        return Bookmarks.builder()
                        .data( Bookmarks.BookmarksBody.builder()
                                                      .bookmarks( bookmarks.stream().map( Bookmark::value ).collect( Collectors.toSet() ) )
                                                      .build() )
                        .build();
    }

    @Setter
    @Getter
    public static class SessionLastBookmarksBody
    {
        private String sessionId;
    }
}
