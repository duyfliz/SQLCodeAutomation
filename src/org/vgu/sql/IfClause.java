package org.vgu.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IfClause {

	private ThenClause Then;
	private IfCondition condition;
	private String SetClause;
	//private List<ElseClause> ElseList = new ArrayList<ElseClause>();;
	private ElseClause elseclause = new ElseClause();
	private IfClause anotherIf;
	private SQLCreateFunction func;
	
	public IfCondition getCondition() {
		return condition;
	}

	public void setCondition(IfCondition condition) {
		this.condition = condition;
	}
	
	public SQLCreateFunction getFunc() {
		return func;
	}
	
	public void setFunc(SQLCreateFunction func) {
		this.func = func;
	}
	
	public ThenClause getThen() {
		return Then;
	}
	public void setThen(ThenClause then) {
		Then = then;
	}

//	public String getReturnValue() {
//		return returnValue;
//	}
//	public void setReturnValue(String returnValue) {
//		this.returnValue = returnValue;
//	}
	public String getSetClause() {
		return SetClause;
	}
	public void setSetClause(String setClause) {
		SetClause = setClause;
	}
	
	public IfClause getAnotherIf() {
		return anotherIf;
	}
	public void setAnotherIf(IfClause anotherIf) {
		this.anotherIf = anotherIf;
	}
//	public List<ElseClause> getElseList() {
//		return ElseList;
//	}
//	public void setElseList(List<ElseClause> elseList) {
//		ElseList = elseList;
//	}
	
	public ElseClause getElseclause() {
		return elseclause;
	}
	public void setElseclause(ElseClause elseclause) {
		this.elseclause = elseclause;
	}
	
	public String toString(){
		String str = "";
		str += "IF " + "(" + condition + ") " + "THEN\n";
		if (Then != null) {
			str +=Then.toString() + "\n";
		}
//        if (ElseList != null) {
//            Iterator<ElseClause> it = ElseList.iterator();
//            while (it.hasNext()) {
//                ElseClause els = it.next();
//                str +=els + "\n";
//                
//            }
//        }
		if (elseclause != null || elseclause.toString() != "") {
			str += elseclause.toString();
		}
		str += "\nEND IF;";
		return str;
	}
}
