package org.vgu.sql;

public class ExceptionMsg {
	private String signalNo;
	private String msgText;
	public String getMsgText() {
		return msgText;
	}
	public void setMsgText(String msgText) {
		this.msgText = msgText;
	}
	public String getSignalNo() {
		return signalNo;
	}
	public void setSignalNo(String signalNo) {
		this.signalNo = signalNo;
	}
	
	public String toString() {
		String s = "";
		s += "SIGNAL SQLSTATE'" + signalNo + "'" + "\n" + "SET MESSAGE_TEXT = " + "'" 
		+ msgText + "';\n";
		return s;
		
	}
}
