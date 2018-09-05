package org.vgu.sql;

import java.util.List;

public class IfCondition {
	protected List<Parameter> Parameters; 
	protected String normalIf;
	protected String funcName;
	
	public String getFuncName() {
		return funcName;
	}

	public void setFuncName(String funcName) {
		this.funcName = funcName;
	}

	public List<Parameter> getParameters() {
		return Parameters;
	}

	public void setParameters(List<Parameter> parameters) {
		Parameters = parameters;
	}

	public String getNormalIf() {
		return normalIf;
	}

	public void setNormalIf(String normalIf) {
		this.normalIf = normalIf;
	}

	@Override
	public String toString() {
		String s = "";
		if (Parameters != null) {
			s += funcName + "(";
			for (int i = 0; i < Parameters.size() ; i++) {
				 if(Parameters.get(i).getIo() == "") {
					 if(i==Parameters.size()-1) {
						 s += Parameters.get(i).getName() + " " +Parameters.get(i).getType();
					 } else {
						 s += Parameters.get(i).getName() + " " +Parameters.get(i).getType() + ", ";
					 }
				 } else {
					 if(i==Parameters.size()-1) {
						 s += Parameters.get(i).getIo() + Parameters.get(i).getName() + " " +Parameters.get(i).getType();
					 } else {
						 s += Parameters.get(i).getIo() + Parameters.get(i).getName() + " " +Parameters.get(i).getType() + ", ";
					 }
				 }
			}
			s += ")";
		}
		if (normalIf != null) {
			s+= normalIf;
		}
		return s;
	}
}
