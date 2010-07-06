// Copyright 2007, 2008, 2010 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.spreadthesource.tapestry.installer.services;

import java.io.IOException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.Dispatcher;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

/**
 * Redirect to the first configuration task start page
 */
public class InstallerStarterDispatcher implements Dispatcher
{
    private static final EventContext EMPTY_CONTEXT = new EmptyEventContext();

    private final ComponentClassResolver componentClassResolver;

    private final ComponentRequestHandler handler;

    private final String startPageName;

    private final PageRenderRequestParameters parameters;

    public InstallerStarterDispatcher(ComponentClassResolver componentClassResolver,

    ComponentRequestHandler handler,

    ConfigurationManager configurationManager)
    {
        this.componentClassResolver = componentClassResolver;
        this.handler = handler;
        
        this.startPageName = configurationManager.getCurrentTask().getStartPage();

        parameters = new PageRenderRequestParameters(this.startPageName, EMPTY_CONTEXT, false);
    }

    public boolean dispatch(Request request, final Response response) throws IOException
    {
        if (request.getPath().equals("/") && componentClassResolver.isPageName(startPageName))
        {
            handler.handlePageRender(parameters);

            return true;
        }

        return false;
    }

}
