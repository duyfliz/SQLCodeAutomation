package org.vgu.sqlgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.vgu.sql.ElseClause;
import org.vgu.sql.ExceptionMsg;
import org.vgu.sql.IfClause;
import org.vgu.sql.IfCondition;
import org.vgu.sql.Parameter;
import org.vgu.sql.SQLCreateFunction;
import org.vgu.sql.SQLCreateProcedure;
import org.vgu.sql.ThenClause;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.JJTCCJSqlParserState;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SubSelect;

public class GenerateProcedure {
	/*Function for the creation of an SQL function*/
	public static SQLCreateFunction auth_function(JSONArray policy, int role, String property, SQLCreateFunction func) {
		Iterator<?> i = policy.iterator();
		SQLCreateFunction function = new SQLCreateFunction();
		while(i.hasNext()) {
			JSONObject object = (JSONObject) i.next();
			Long integer = (long) role;
			if(object.get("role").equals(integer) && object.get("property").equals(property)) {
				switch (role) {
				case 1:
					function.setName("auth_read_name_admin");
					break;
				case 2:
					function.setName("auth_read_name_lecturer");
					break;
				case 3:
					function.setName("auth_read_name_student");
					break;
				default:
					break;
				}
				List<Parameter> param_list = new ArrayList<Parameter>();
				for(int i1=0; i1<func.getParameters().size(); i1++) {
					if(!func.getParameters().get(i1).getName().trim().equals("caller_role")) {
						param_list.add(func.getParameters().get(i1));
					}
				}
				function.setParameters(param_list);
				function.setDeclareVariables("DECLARE var INT;");
				function.setReturnType("INT DETERMINISTIC");
				function.setReturnValue("var;");
				if(object.get("check").equals(true)) {
					function.setSetVariables("SET var=1;");
				} else {
					function.setSetVariables("SET var=0;");
				}
			}
		}
		return function;
	}
	
	/*This function is used to create the auth_read_name function*/
	public static SQLCreateFunction role_checking_function(JSONArray policy, SQLCreateProcedure proc) {
		List<Parameter> param_list = new ArrayList<Parameter>();

		param_list.add(new Parameter("self_id", "INT"));
		param_list.add(new Parameter("caller_id", "INT"));
		param_list.add(new Parameter("caller_role", "INT"));
		
		for(int i=0; i<proc.getParameters().size(); i++) {
			if(!proc.getParameters().get(i).getName().trim().equals("caller_id")
					&& !proc.getParameters().get(i).getName().trim().equals("self_id")
					&& !proc.getParameters().get(i).getName().trim().equals("caller_role")) {
				Parameter temp = new Parameter();
				temp.setName(proc.getParameters().get(i).getName());
				temp.setType(proc.getParameters().get(i).getType());
				param_list.add(temp);
			}
		}
		
		List<Parameter> sub_param_list = new ArrayList<Parameter>();
		for(int i1=0; i1<param_list.size(); i1++) {
			if(!param_list.get(i1).getName().trim().equals("caller_role")) {
				Parameter temp = new Parameter();
				temp.setName(param_list.get(i1).getName());
				temp.setType("");
				sub_param_list.add(temp);
			}
		}
		

		ExceptionMsg exMsg = new ExceptionMsg();
		exMsg.setMsgText("Security exception");
		exMsg.setSignalNo("45000");
		
		ElseClause else3 = new ElseClause();
		else3.setExMsg(exMsg);
		else3.setReturnValue("0");
		

		List <IfClause> ifList = new ArrayList<>();
		ifList.add(createRoleChecking("caller_role = 2", "1" ,exMsg,sub_param_list,"auth_read_name_lecturer"));
		ifList.add(createRoleChecking("caller_role = 3", "0" ,exMsg,sub_param_list,"auth_read_name_student"));
		ifList.add(createRoleChecking("caller_role = 1", "0" ,exMsg,sub_param_list,"auth_read_name_admin"));
		
		for (int i = 0 ; i < ifList.size() ; i++) {
			if (i < ifList.size()-1) {
				ElseClause tempElse = new ElseClause();
				tempElse.setIfclause(ifList.get(i+1));
				ifList.get(i).setElseclause(tempElse);
			}else {
				ifList.get(i).setElseclause(else3);
			}
		}
		
		SQLCreateFunction create1 = new SQLCreateFunction();
		create1.setName("auth_read_name");
		//create1.setParameters(proc.getParameters());
		//for(int i=0; i<plainSelect.get)
		//create1.getParameters().set(0, self_id);

//		System.out.println(proc.getParameters());
		
		create1.setReturnType("INT DETERMINISTIC");
		create1.setIfclause(ifList.get(0));
		create1.setParameters(param_list);
		System.out.println(GenerateProcedure.auth_function(policy, 1, "given_name", create1));
		System.out.println(GenerateProcedure.auth_function(policy, 2, "family_name", create1));
		System.out.println(GenerateProcedure.auth_function(policy, 3, "middle_name", create1));
		return create1;
	}
	
	public static IfClause createRoleChecking(String caller_role, String returnValue,ExceptionMsg exMsg,List<Parameter> sub_param_list,
			String funcName) {
		
		IfClause ifCls = new IfClause();
		IfCondition ifCond = new IfCondition();
		ifCond.setNormalIf(caller_role);
		ifCls.setCondition(ifCond);
		
		ThenClause thenCls = new ThenClause();
		thenCls.setReturnValue("1");
		
		IfCondition subifCond = new IfCondition();
		subifCond.setParameters(sub_param_list);
		subifCond.setFuncName(funcName);
		IfClause subIfCls = new IfClause();
		subIfCls.setCondition(subifCond);
		
		ThenClause subthenCls = new ThenClause();
		subthenCls.setReturnValue(returnValue);
		subIfCls.setThen(subthenCls);
		
		ElseClause subElseCls = new ElseClause();
		subElseCls.setExMsg(exMsg);
		subElseCls.setReturnValue("0");
		subIfCls.setElseclause(subElseCls);
		
		thenCls.setIf(subIfCls);
		ifCls.setThen(thenCls);
		
		return ifCls;
	}
	public SQLCreateProcedure createCustomProcedure(SQLCreateProcedure proc, JSONArray policy) throws JSQLParserException {
		SQLCreateProcedure procedure = new SQLCreateProcedure();
		procedure.setName(proc.getName());
		procedure.setParameters(proc.getParameters());
		procedure.setReturnType("INT DETERMINISTIC");
		procedure.setDeclareVariables("caller_role INT DEFAULT 0");
		
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		Select select = (Select) parserManager.parse(new StringReader(proc.getBody().get(0)));
		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
		
		
		//create first statement
		PlainSelect roleSelect = this.inputQuery(plainSelect);
		
		//create second statement
		PlainSelect caseSelect = this.modifiedQuery(plainSelect, proc);
		
		List<String> bodylist = new ArrayList<>();
		bodylist.add(roleSelect.toString().concat(";"));
		bodylist.add(caseSelect.toString().concat(";"));
		procedure.setBody(bodylist);
		System.out.println(role_checking_function(policy, procedure));
		return procedure;
	}
	
	public static void main(String[] args) throws JSQLParserException, FileNotFoundException, IOException, ParseException {
		Object obj = new JSONParser().parse(new FileReader("E:\\Nam 3\\Thesis\\policy.json"));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray policy = (JSONArray) jsonObject.get("data");
		String s = "CREATE PROCEDURE GetLecturerCourseStudentList(IN lecturer_id INT, course_id INT, IN caller_id INT)\r\n" + 
				" BEGIN\r\n" + 
				"    SELECT family_name \r\n" + 
				"    FROM reg_user\r\n" + 
				"    INNER JOIN\r\n" + 
				"       (SELECT reg_user \r\n" + 
				"      FROM student\r\n" + 
				"      INNER JOIN\r\n" + 
				"      (SELECT student FROM course_student\r\n" + 
				"      INNER JOIN \r\n" + 
				"      (SELECT course FROM course_lecturer \r\n" + 
				"      WHERE course = course_id AND lecturer = lecturer_id) AS TEMP_1\r\n" + 
				"      ON course_student.course = TEMP_1.course) as TEMP_2 \r\n" + 
				"      ON student_id = TEMP_2.student) AS TEMP_3\r\n" + 
				"      ON TEMP_3.reg_user = reg_user_id;\r\n" + 
				"      END //";
		
		GenerateProcedure gp = new GenerateProcedure();
		SQLCreateProcedure proc = gp.parseProcedure(s);
		SQLCreateProcedure new_proc = gp.createCustomProcedure(proc, policy);
		System.out.println(new_proc.toString());
	}
	
	public SQLCreateProcedure parseProcedure(String s) {
		SQLCreateProcedure proc = new SQLCreateProcedure();
		proc.setName(s.substring(s.indexOf("PROCEDURE")+10, s.indexOf("(")));
		String parameters = s.substring(s.indexOf("(")+1, s.indexOf(")"));
		List<Parameter> param_list = new ArrayList<Parameter>();
		
		if(parameters.length() != 0) {
			String[] part = parameters.split(",");	
			for(int i=0; i<part.length; i++) {
				Parameter temp = new Parameter();
				part[i] = part[i].trim();
				temp.parseParameter(part[i]);
				param_list.add(temp);
			}
		}
		
		List<String> body = new ArrayList<String>();
		body.add(this.getBodyProcedure(s));
		proc.setBody(body);
		proc.setParameters(param_list);
		return proc;
	}
	
	public String getBodyProcedure(String s) {
		String a = s.substring(s.indexOf("BEGIN")+6, s.indexOf("END"));
		return a;
	}
	
//	public List<Select> getSelectStatements(String a) throws JSQLParserException {
//		List<Select> list = new ArrayList<Select>();
//		CCJSqlParserManager parserManager = new CCJSqlParserManager();
//		String[] parts = a.split(";");
//		for(int i=0; i<parts.length; i++) {
//			Select sec = (Select) parserManager.parse(new StringReader(parts[i]));
//			list.add(sec);
//		}
//		return list;
//	}
	
	
	
	public PlainSelect inputQuery(PlainSelect plainSelect) {
		
		/*Create first select statement*/
		PlainSelect roleSelect = new PlainSelect();
		SelectExpressionItem item = new SelectExpressionItem();
		
		Column role = new Column();
		role.setColumnName("role");
		item.setExpression(role);
		
		Column caller_role = new Column();
		caller_role.setColumnName("caller_role");
		
		Column reg_user_id = new Column();
		reg_user_id.setColumnName("reg_user_id");
		
		Table tab = new Table();
		tab.setName("caller_role");
		List<Table> tab_list = new ArrayList<Table>();
		tab_list.add(tab);
		roleSelect.addSelectItems(item);;
		roleSelect.setIntoTables(tab_list);
		roleSelect.setFromItem(plainSelect.getFromItem());
		
		EqualsTo equalTo = new EqualsTo();
		equalTo.setLeftExpression(reg_user_id);
		equalTo.setRightExpression(caller_role);
		roleSelect.setWhere(equalTo);
		roleSelect.setFromItem(plainSelect.getFromItem());
		return roleSelect;
		
	}
	
	public PlainSelect modifiedQuery(PlainSelect plainSelect, SQLCreateProcedure proc) {
		PlainSelect caseSelect = new PlainSelect();
		caseSelect = plainSelect;
		if(caseSelect.getJoins() != null) {
			for(int i=0; i< caseSelect.getJoins().size(); i++) {
				if(caseSelect.getJoins().get(i).getRightItem().getClass().getName()
						== "net.sf.jsqlparser.statement.select.SubSelect") {
					SubSelect sub = (SubSelect)caseSelect.getJoins().get(i).getRightItem();
					PlainSelect ps = (PlainSelect) sub.getSelectBody();
					this.modifiedQuery(ps, proc);
				}
			}
		}
		
		List<SelectItem> list = new ArrayList<SelectItem>();
				
		Column caller_role = new Column();
		caller_role.setColumnName("caller_role");
		
		Column caller_id = new Column();
		caller_id.setColumnName("caller_id");
		
		Column self_id = new Column();
		self_id.setColumnName("self_id");
		
		Function func1 = new Function();
		func1.setName("auth_read_name");
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
		func1.setParameters(el1);
		LongValue val = new LongValue(1);
		

		Table table = (Table) plainSelect.getFromItem();
		for(int i=0; i<plainSelect.getSelectItems().size(); i++) {
			SelectExpressionItem example_item = this.createCASE((SelectExpressionItem)plainSelect.getSelectItems().get(i),
					table,func1, val);
			list.add(example_item);
		}
		
		caseSelect.setSelectItems(list);
		return caseSelect;
	}
	
	public SelectExpressionItem createCASE(SelectExpressionItem item, Table table, Function function, LongValue val) {
		SelectExpressionItem case_item = new SelectExpressionItem();
		CaseExpression caseExpression = new CaseExpression();
		if (item.getExpression().getClass().getName() != "net.sf.jsqlparser.expression.Function") {
			Column col = (Column) item.getExpression();
			Alias name = new Alias(col.getColumnName());
			case_item.setAlias(name);
			
			WhenClause when = new WhenClause();
			List<WhenClause> when_clauses = new ArrayList<WhenClause>();
			when_clauses.add(when);
			col.setTable(table);
			when.setWhenExpression(val);
			when.setThenExpression(col);
			caseExpression.setWhenClauses(when_clauses);
			caseExpression.setSwitchExpression(function);
			case_item.setExpression(caseExpression);
			return case_item;
		}
		else {
			Function func = (Function) (item.getExpression());		
			Column col = (Column) func.getParameters().getExpressions().get(0);
			Alias name = new Alias(col.getColumnName());
			case_item.setAlias(name);
			
			WhenClause when = new WhenClause();
			List<WhenClause> when_clauses = new ArrayList<WhenClause>();
			when_clauses.add(when);
			col.setTable(table);
			when.setWhenExpression(val);
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
			
			//Function a = new Function();
			
			
		return case_item;
		}
		//return item;
		
	}
}
