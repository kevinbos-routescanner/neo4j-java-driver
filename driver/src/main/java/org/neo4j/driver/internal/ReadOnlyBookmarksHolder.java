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
package org.neo4j.driver.internal;

import java.util.Set;

import org.neo4j.driver.Bookmark;

/**
 * @since 2.0
 */
public class ReadOnlyBookmarksHolder implements BookmarksHolder
{
    private final Set<Bookmark> bookmarks;

    public ReadOnlyBookmarksHolder( Set<Bookmark> bookmarks )
    {
        this.bookmarks = bookmarks;
    }

    @Override
    public Set<Bookmark> getBookmarks()
    {
        return bookmarks;
    }

    @Override
    public void setBookmark( Bookmark bookmark )
    {
        // NO_OP
    }
}
