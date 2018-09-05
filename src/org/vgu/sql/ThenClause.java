package org.vgu.sql;

public class ThenClause {
	private String SetClause;
	private IfClause If;
	private String returnValue;
	
	public String getSetClause() {
		return SetClause;
	}
	public void setSetClause(String setClause) {
		SetClause = setClause;
	}
	public IfClause getIf() {
		return If;
	}
	public void setIf(IfClause if1) {
		If = if1;
	}
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
	public String toString(){
		String s = "RETURN ";
		s += "(" + returnValue + ");";
		if (If != null) {
			s = "";
			s += If;
		}
		return s;	
	}
}
