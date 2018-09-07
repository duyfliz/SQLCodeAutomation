package org.vgu.sqlgen;

import java.util.ArrayList;
import java.util.List;


import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;

public class SQLVisitorSecure implements SQLVisitor {

	@Override
	public SelectExpressionItem visit(SQLSelectExpressionItem ex) {
		Function function = ex.getFunction();
		SelectExpressionItem item = ex.getItem();
		Table table = ex.getTable();
		SelectExpressionItem case_item = new SelectExpressionItem();
		CaseExpression caseExpression = new CaseExpression();
		
		if (!(item.getExpression() instanceof Function)) {

			Column col = (Column) item.getExpression();
			
			if(col.getTable().getFullyQualifiedName().isEmpty()) {
				col.setTable(table);
				Alias name = new Alias(col.getColumnName());
				case_item.setAlias(name);
				
			} else {
				Alias name = new Alias(col.getFullyQualifiedName());
				case_item.setAlias(name);
			}
			
			
			
			
			WhenClause when = new WhenClause();
			List<WhenClause> when_clauses = new ArrayList<WhenClause>();
			when_clauses.add(when);
			when.setWhenExpression(new LongValue(1));
			when.setThenExpression(col);
			caseExpression.setWhenClauses(when_clauses);
			caseExpression.setSwitchExpression(function);
			case_item.setExpression(caseExpression);
		} else {
		
			Function func = (Function) (item.getExpression());		
			if(func.isAllColumns()) {
				this.visit(new AllColumns());
				case_item.setExpression(func);
			}
			else {
			Column col = (Column) func.getParameters().getExpressions().get(0);
			Alias name = new Alias(col.getColumnName());
			case_item.setAlias(name);
			
			WhenClause when = new WhenClause();
			List<WhenClause> when_clauses = new ArrayList<WhenClause>();
			when_clauses.add(when);
			col.setTable(table);
			when.setWhenExpression(new LongValue(1));
			when.setThenExpression(col);
			caseExpression.setWhenClauses(when_clauses);
			caseExpression.setSwitchExpression(function);
			case_item.setExpression(caseExpression);
			
			
			Function fu = new Function ();
			fu.setName(func.getName());
			ExpressionList ep = new ExpressionList();
			List<Expression> listE = new ArrayList<>();
			listE.add(case_item.getExpression());
			ep.setExpressions(listE);
			fu.setParameters(ep);
			case_item.setExpression(fu);
			}
		}
		return case_item;
	}

	@Override
	public AllColumns visit(AllColumns column) {
		
		return column;
	}

	@Override
	public void visit(SQLSelect select) {
		PlainSelect plainSelect = select.getPlainSelect();
		if(plainSelect.getJoins() == null) {
			
		}
		else {
			for(int i=0; i<plainSelect.getJoins().size(); i++) {
				if(plainSelect.getJoins().get(i).getRightItem() instanceof SubSelect) {
					SubSelect sub = (SubSelect) plainSelect.getJoins().get(i).getRightItem();
					PlainSelect plainSelect2 = (PlainSelect) sub.getSelectBody();
					SQLSelect select2 = new SQLSelect();
					select2.setPlainSelect(plainSelect2);
					select2.accept(this);
				}
				else {
					
				}
			}
		}
	}

}
