package org.vgu.sqlgen;

import java.util.ArrayList;
import java.util.List;

import org.vgu.sql.SQLCreateProcedure;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;


public class SQLSelect implements SQLStatement {

	PlainSelect plainSelect;
	static SQLCreateProcedure proc;
	Function function;
	public PlainSelect getPlainSelect() {
		return plainSelect;
	}



	public void setPlainSelect(PlainSelect plainSelect) {
		this.plainSelect = plainSelect;
	}

	

	public SQLCreateProcedure getProc() {
		return proc;
	}



	public void setProc(SQLCreateProcedure proc1) {
		proc = proc1;
	}

	@Override
	public void accept(SQLVisitor visitor) {
		List<SelectItem> list = new ArrayList<SelectItem>();
		this.createFunction();
		for(int i=0; i<plainSelect.getSelectItems().size(); i++) {
			if(plainSelect.getSelectItems().get(i) instanceof SelectExpressionItem) {
				
				
				SelectExpressionItem sei = (SelectExpressionItem) plainSelect.getSelectItems().get(i);
				Table table = (Table) plainSelect.getFromItem();
				SelectExpressionItem temp = visitor.visit(sei, table, function);
				list.add(temp);
			}
			else if (plainSelect.getSelectItems().get(i) instanceof AllColumns) {
				AllColumns all = (AllColumns) plainSelect.getSelectItems().get(i);
				AllColumns cols = visitor.visit(all);
				list.add(cols);
			}
		}
		visitor.visit(plainSelect);
		plainSelect.setSelectItems(list);
	}
	
	public void createFunction() {
		function = new Function();
		function.setName("auth_read_name");
		
		Column caller_role = new Column();
		caller_role.setColumnName("caller_role");
		
		Column caller_id = new Column();
		caller_id.setColumnName("caller_id");
		
		Column self_id = new Column();
		self_id.setColumnName("self_id");
		
		List<Expression> column_list = new ArrayList<Expression>();
		column_list.add(self_id);
		column_list.add(caller_id);
		column_list.add(caller_role);
		
		
		for(int i=0; i<proc.getParameters().size(); i++) {
			if(!proc.getParameters().get(i).getName().trim().equals("caller_id")
					&& !proc.getParameters().get(i).getName().trim().equals("self_id")
					&& !proc.getParameters().get(i).getName().trim().equals("caller_role")) {
				String col_name = proc.getParameters().get(i).getName().trim();
				Column ct = new Column();
				ct.setColumnName(col_name);
				column_list.add(ct);
			}
		}
		
		ExpressionList el1 = new ExpressionList();
		el1.setExpressions(column_list);
		function.setParameters(el1);
	}
}
