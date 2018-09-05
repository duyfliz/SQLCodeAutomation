package org.vgu.sql;

public class ElseClause {
	private ExceptionMsg exMsg;
	private String returnValue;
	private IfClause ifclause;
	
	public String getReturnValue() {
		return returnValue;
	}
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
	public IfClause getIfclause() {
		return ifclause;
	}
	public void setIfclause(IfClause ifclause) {
		this.ifclause = ifclause;
	}
	
	public ExceptionMsg getExMsg() {
		return exMsg;
	}
	public void setExMsg(ExceptionMsg exMsg) {
		this.exMsg = exMsg;
	}
	
	public String toString(){
		String s = "";
		if (exMsg != null || returnValue != null) {
			s += "ELSE\n";
		}
		
		if (ifclause != null) {
			s += "ELSE" + ifclause;
		}
		
		if (exMsg != null) {
			s += exMsg;
		}

		if (returnValue != null) {
			s += "RETURN " + "(" + returnValue + ");";
		}
		return s;	
	}


}
