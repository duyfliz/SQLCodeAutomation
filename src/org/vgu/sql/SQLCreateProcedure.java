package org.vgu.sql;

import java.util.Iterator;

public class SQLCreateProcedure extends SQLCreateFunction {
	@Override
	public String toString() {
		String s = "";
		s += "CREATE PROCEDURE " + name;
		s += "(";
		if (Parameters != null) {
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
		}
		s += ")";
//		if (returnType != null) {
//			s+= "RETURNS " + returnType;
//		}
		s += "\nBEGIN\n";
		if (declareVariables != null) {
			s+= "DECLARE " + declareVariables.concat(";") + "\n";
		}
		if (setVariables != null) {
			s+= setVariables + "\n";
		}
		if (returnValue != null) {
			s+= "RETURN " + returnValue + "\n";
		}
		if (body != null) {
			int i = 0;
			while (i < body.size()) {
				if (i+1 == body.size()) {
					s+= body.get(i);
				}else {
					s+= body.get(i) + "\n";
				}				
				i++;
			}
		}
		if (ifclause != null) {
			s+= ifclause + "\n";
		}
		s += "\nEND\n";
		return s;
	}
}
