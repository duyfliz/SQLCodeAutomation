package org.vgu.sqlgen;

import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public interface SQLVisitor {
	public SelectExpressionItem visit(SQLSelectExpressionItem ex);
	public AllColumns visit(AllColumns column);
	public void visit(SQLSelect sqlSelect);
}
