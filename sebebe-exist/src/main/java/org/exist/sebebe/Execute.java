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

import org.apache.log4j.Logger;
import org.exist.Database;
import org.exist.EXistException;
import org.exist.dom.QName;
import org.exist.security.PermissionDeniedException;
import org.exist.security.Subject;
import org.exist.source.DBSource;
import org.exist.source.Source;
import org.exist.source.SourceFactory;
import org.exist.storage.DBBroker;
import org.exist.storage.ProcessMonitor;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.exist.dom.NodeSet.EMPTY_SET;
import static org.exist.security.xacml.AccessContext.XMLDB;
import static org.exist.xmldb.XmldbURI.EMBEDDED_SERVER_URI_PREFIX;
import static org.exist.xquery.Cardinality.EXACTLY_ONE;
import static org.exist.xquery.Cardinality.ZERO_OR_MORE;
import static org.exist.xquery.value.Type.*;

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 *
 */
public class Execute extends Function {

    private final static Logger logger = Logger.getLogger(Execute.class);

    public final static FunctionSignature signatures[] = {
            new FunctionSignature(
                    new QName("execute", Module.NAMESPACE_URI, Module.PREFIX),
                    "Execute function of the module.",
                    new SequenceType[] {
                            new FunctionParameterSequenceType("uri", ANY_URI, EXACTLY_ONE, ""),
                            new FunctionParameterSequenceType("function", QNAME, EXACTLY_ONE, ""),
                            new FunctionParameterSequenceType("param", ITEM, ZERO_OR_MORE, ""),
                    },
                    new FunctionReturnSequenceType(ITEM, ZERO_OR_MORE, "")
            ),
    };

    public Execute(XQueryContext context, FunctionSignature signature) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence contextSequence, Item contextItem) throws XPathException {
        try {
            DBBroker broker = context.getBroker();
            Subject subject = broker.getSubject();
            Database db = broker.getDatabase();
            ProcessMonitor pm = db.getProcessMonitor();
            String uri = getArgument(0).eval(contextSequence, contextItem).itemAt(0).getStringValue();
            Source query = SourceFactory.getSource(db.get(subject), null, uri, false);
            XQuery service = broker.getXQueryService();
            XQueryContext context = new XQueryContext(db, XMLDB);
            if (query instanceof DBSource) {
                context.setModuleLoadPath(EMBEDDED_SERVER_URI_PREFIX + ((DBSource)query).getDocumentPath().removeLastSegment().toString());
            }
            CompiledXQuery compiledQuery = service.compile(context, query);
            try {
                QName f = ((QNameValue) getArgument(1).eval(contextSequence, contextItem).itemAt(0)).getQName();
                UserDefinedFunction function = context.resolveFunction(f, 1);
                context.getProfiler().traceQueryStart();
                pm.queryStarted(context.getWatchDog());
                FunctionCall call = new FunctionCall(context, function);
                List<Expression> a = new ArrayList<>(1);
                a.add(getArgument(2));
                call.setArguments(a);
                call.analyze(new AnalyzeContextInfo());
                return call.eval(EMPTY_SET);
            } finally {
                if (pm != null) {
                    context.getProfiler().traceQueryEnd(context);
                    pm.queryCompleted(context.getWatchDog());
                }
                compiledQuery.reset();
                context.reset();
            }
        } catch (IOException e) {
            throw new XPathException(this, e);
        } catch (PermissionDeniedException e) {
            throw new XPathException(this, e);
        } catch (EXistException e) {
            throw new XPathException(this, e);
        }
    }

}
