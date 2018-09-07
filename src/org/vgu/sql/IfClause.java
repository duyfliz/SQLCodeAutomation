package org.vgu.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IfClause {

	private ThenClause Then;
	private IfCondition condition;
	private String SetClause;
	private List<ElseClause> ElseList = new ArrayList<ElseClause>();

	private ElseClause elseclause = new ElseClause();

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
	
	public List<ElseClause> getElseList() {
		return ElseList;
	}

	public void setElseList(List<ElseClause> elseList) {
		ElseList = elseList;
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
//		else if (elseIfClause != null) {
//			str += elseIfClause.toString();
//		}
		
		if (ElseList != null){
			for (int i = 0; i < ElseList.size(); i++) {
				str += ElseList.get(i).toString();
//				if (i == ElseList.size()) {
//					str += "\nENDIF;";
//				}
			}
		}
//		}else 

//		if (elseclause != null && elseclause.toString() != "" && ElseList != null){
//			str += "\nENDIF;";
//		}
		
		return str;
	}
}
