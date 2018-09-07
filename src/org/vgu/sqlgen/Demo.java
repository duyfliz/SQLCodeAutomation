package org.vgu.sqlgen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;

public class Demo {
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
		ifList.add(createIfCondition("caller_role = 2", "1" ,exMsg,sub_param_list,"auth_read_name_lecturer"));
		ifList.add(createIfCondition("caller_role = 3", "0" ,exMsg,sub_param_list,"auth_read_name_student"));
		ifList.add(createIfCondition("caller_role = 1", "0" ,exMsg,sub_param_list,"auth_read_name_admin"));
		
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
		
		
		create1.setReturnType("INT DETERMINISTIC");
		create1.setIfclause(ifList.get(0));
		create1.setParameters(param_list);
		System.out.println(Demo.auth_function(policy, 1, "given_name", create1));
		System.out.println(Demo.auth_function(policy, 2, "family_name", create1));
		System.out.println(Demo.auth_function(policy, 3, "middle_name", create1));
		return create1;
	}
	
	public static IfClause createIfCondition(String caller_role, String returnValue,ExceptionMsg exMsg,List<Parameter> sub_param_list,
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
	public static void main(String[] args) throws JSQLParserException, FileNotFoundException, IOException, ParseException {
		
		Object obj = new JSONParser().parse(new FileReader("E:\\Nam 3\\Thesis\\policy.json"));
		JSONObject jsonObject = (JSONObject) obj;
		JSONArray policy = (JSONArray) jsonObject.get("data");
		
		String s = "CREATE PROCEDURE GetCourseStudentList(IN course_id INT, IN caller_id INT, name VARCHAR)\r\n" + 
				"BEGIN\r\n" +
				"SELECT \r\n" + 
				"  DISTINCT\r\n" + 
				"  t.id,\r\n" + 
				"  t.tag, \r\n" + 
				"  c.title AS Category\r\n" + 
				"FROM\r\n" + 
				"  tags2Articles t2a \r\n" + 
				"  INNER JOIN tags t ON t.id = t2a.idTag\r\n" + 
				"  INNER JOIN categories c ON t.tagCategory = c.id\r\n" + 
				"  /* Subquery join returns article ids having all 3 tags you filtered */\r\n" + 
				"  /* Joining against tags2articles again will get the remaining tags for these articles */\r\n" + 
				"  INNER JOIN (\r\n" + 
				"    SELECT\r\n" + 
				"     a.id \r\n" + 
				"    FROM \r\n" + 
				"     articles AS a\r\n" + 
				"     JOIN tags2articles AS ta  ON a.id=ta.idArticle\r\n" + 
				"     JOIN tags AS tsub ON ta.idTag=tsub.id\r\n" + 
				"    WHERE \r\n" + 
				"      tsub.id IN (12,13,16) \r\n" + 
				"    GROUP BY a.id\r\n" + 
				"    HAVING COUNT(DISTINCT tsub.id)=3 \r\n" + 
				"  ) asub ON t2a.idArticle = asub.id;" + 
				"END //";
		
		Demo demo = new Demo();
		
		SQLCreateProcedure proc = demo.parseProcedure(s);
		SQLCreateProcedure new_proc = demo.createCustomProcedure(proc, policy);
		System.out.println(new_proc.toString());
	}
	
	public SQLCreateProcedure createCustomProcedure(SQLCreateProcedure proc, JSONArray policy) throws JSQLParserException {
		SQLVisitorSecure secure = new SQLVisitorSecure();
		SQLCreateProcedure procedure = new SQLCreateProcedure();
		procedure.setName(proc.getName());
		procedure.setParameters(proc.getParameters());
		procedure.setReturnType("INT DETERMINISTIC");
		procedure.setDeclareVariables("caller_role INT DEFAULT 0");
		
		CCJSqlParserManager parserManager = new CCJSqlParserManager();
		List<String> bodylist = new ArrayList<>();
		for(int i=0; i<proc.getBody().size(); i++) {
			Select select = (Select) parserManager.parse(new StringReader(proc.getBody().get(i)));
			PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
			
			
			//create first statement
			PlainSelect roleSelect = this.inputQuery(plainSelect);
			
			//create second statement
			SQLSelect sqlselect = new SQLSelect();
			sqlselect.setPlainSelect(plainSelect);
			sqlselect.setProc(proc);
			sqlselect.accept(secure);
			PlainSelect securedSelect = sqlselect.getPlainSelect();
			
			
			bodylist.add(roleSelect.toString().concat(";"));
			bodylist.add(securedSelect.toString().concat(";"));
		}
		
		procedure.setBody(bodylist);
		//System.out.println(role_checking_function(policy, procedure));
		System.out.println(role_checking_function(policy, procedure));
		return procedure;
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
		
		
		proc.setBody(this.getBodyProcedure(s));
		proc.setParameters(param_list);
		return proc;
	}
	
	public List<String> getBodyProcedure(String s) {
		List<String> list = new ArrayList<String>();
		String a = s.substring(s.indexOf("BEGIN")+6, s.indexOf("END"));
		String[] part = a.split(";");
		for(int i=0; i<part.length; i++) {
			list.add(part[i]);
		}
		return list;
	}
	
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
}
