package org.vgu.sql;

public class Parameter {
	protected String name;
	protected String type;
	protected String io = "";
	
	public Parameter() {
		
	}
	
	public Parameter(String name, String type) {
		this.name = name;
		this.type = type;
	}
	
	public Parameter(String name, String type, String io) {
		this.name = name;
		this.type = type;
		this.io = io;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	public String getIo() {
		return io;
	}
	public void setIo(String io) {
		this.io = io;
	}
	
	public Parameter parseParameter(String s) {
		if(s.startsWith("IN")) {
			this.name = s.substring(s.indexOf(" "), s.lastIndexOf(" "));
			this.io = s.substring(0, 2);
			this.type = s.substring(s.lastIndexOf(" ")+1);
		} else if (s.startsWith("OUT")){
			this.name = s.substring(s.indexOf(" "), s.lastIndexOf(" "));
			this.io = s.substring(0, 3);
			this.type = s.substring(s.lastIndexOf(" ")+1);
		}
		  else	
		{
			this.name = s.substring(0, s.indexOf(" "));
			this.type = s.substring(s.lastIndexOf(" ")+1);
		}
		return this;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String s = "";
		s += io + name + " " + type;
		return s;
	}
}
