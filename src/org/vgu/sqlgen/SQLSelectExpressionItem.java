package org.vgu.sqlgen;

import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class SQLSelectExpressionItem implements SQLStatement {
	
	SelectExpressionItem item;
	Table table;
	Function function;
	
	public SelectExpressionItem getItem() {
		return item;
	}

	public void setItem(SelectExpressionItem item) {
		this.item = item;
	}

	@Override
	public void accept(SQLVisitor visitor) {
		visitor.visit(item, table, function);
	}

}
