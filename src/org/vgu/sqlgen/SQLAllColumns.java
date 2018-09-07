package org.vgu.sqlgen;

import net.sf.jsqlparser.statement.select.AllColumns;

public class SQLAllColumns implements SQLStatement {

	AllColumns all;
	
	public AllColumns getAll() {
		return all;
	}


	public void setAll(AllColumns all) {
		this.all = all;
	}

	@Override
	public void accept(SQLVisitor visitor) {
		visitor.visit(all);	
	}

}
