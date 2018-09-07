package org.vgu.sqlgen;

import net.sf.jsqlparser.JSQLParserException;

public interface SQLStatement {
	public void accept(SQLVisitor visitor) throws JSQLParserException;
}
