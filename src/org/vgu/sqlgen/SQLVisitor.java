package org.vgu.sqlgen;

import org.vgu.sql.SQLCreateProcedure;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public interface SQLVisitor {
	public SelectExpressionItem visit(SelectExpressionItem item, Table table, Function function);
	public AllColumns visit(AllColumns column);
	public void visit(PlainSelect plainSelect);
}
