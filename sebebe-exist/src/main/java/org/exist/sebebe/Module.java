/*
 *  eXist Open Source Native XML Database
 *  Copyright (C) 2001-2014 The eXist Project
 *  http://exist-db.org
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.exist.sebebe;

import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 * 
 */
public class Module extends AbstractInternalModule {

    public final static String NAMESPACE_URI = "http://exist-db.org/sebebe";
    public final static String PREFIX = "sebebe";
    private final static String RELEASED_IN_VERSION = "eXist-2.2";
    private final static String DESCRIPTION = "Module implements sebebe protocol.";

    private final static FunctionDef[] functions = {
            new FunctionDef(Execute.signatures[0], Execute.class),
    };
    
    public Module(Map<String, List<? extends Object>> parameters) {
        super(functions, parameters);
    }

    public String getDefaultPrefix() {
        return PREFIX;
    }

    public String getDescription() {
        return DESCRIPTION;
    }

    public String getNamespaceURI() {
        return NAMESPACE_URI;
    }

    public String getReleaseVersion() {
        return RELEASED_IN_VERSION;
    }

}
