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
package neo4j.org.testkit.backend;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import neo4j.org.testkit.backend.channel.handler.TestkitMessageInboundHandler;
import neo4j.org.testkit.backend.channel.handler.TestkitMessageOutboundHandler;
import neo4j.org.testkit.backend.channel.handler.TestkitRequestProcessorHandler;
import neo4j.org.testkit.backend.channel.handler.TestkitRequestResponseMapperHandler;

public class Runner
{
    public static void main( String[] args ) throws InterruptedException
    {
        TestkitRequestProcessorHandler.BackendMode backendMode;
        String modeArg = args.length > 0 ? args[0] : null;
        if ( "async".equals( modeArg ) )
        {
            backendMode = TestkitRequestProcessorHandler.BackendMode.ASYNC;
        }
        else if ( "reactive-legacy".equals( modeArg ) )
        {
            backendMode = TestkitRequestProcessorHandler.BackendMode.REACTIVE_LEGACY;
        }
        else if ( "reactive".equals( modeArg ) )
        {
            backendMode = TestkitRequestProcessorHandler.BackendMode.REACTIVE;
        }
        else
        {
            backendMode = TestkitRequestProcessorHandler.BackendMode.SYNC;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try

        {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group( group )
                     .channel( NioServerSocketChannel.class )
                     .localAddress( 9876 )
                     .childHandler( new ChannelInitializer<SocketChannel>()
                     {
                         @Override
                         protected void initChannel( SocketChannel channel )
                         {
                             channel.pipeline().addLast( new TestkitMessageInboundHandler() );
                             channel.pipeline().addLast( new TestkitMessageOutboundHandler() );
                             channel.pipeline().addLast( new TestkitRequestResponseMapperHandler() );
                             channel.pipeline().addLast( new TestkitRequestProcessorHandler( backendMode ) );
                         }
                     } );
            ChannelFuture server = bootstrap.bind().sync();
            server.channel().closeFuture().sync();
        }
        finally
        {
            group.shutdownGracefully().sync();
        }
    }
}
