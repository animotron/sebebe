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
import org.exist.dom.QName;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;

import static org.exist.sebebe.Module.NAMESPACE_URI;
import static org.exist.sebebe.Module.PREFIX;
import static org.exist.xquery.Cardinality.EXACTLY_ONE;
import static org.exist.xquery.Cardinality.ZERO_OR_MORE;
import static org.exist.xquery.value.Sequence.EMPTY_SEQUENCE;
import static org.exist.xquery.value.Type.*;

/**
 * @author <a href="mailto:gazdovsky@gmail.com">Evgeny Gazdovsky</a>
 *
 */
public class Execute extends BasicFunction {

    private final static Logger logger = Logger.getLogger(Execute.class);

    public final static FunctionSignature signatures[] = {
            new FunctionSignature(
                    new QName("cancel", NAMESPACE_URI, PREFIX),
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
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        return EMPTY_SEQUENCE;
    }

}
