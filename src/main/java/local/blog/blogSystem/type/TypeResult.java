package local.blog.blogSystem.type;

public class TypeResult {
	String resuCode;
	String resuMessage;
	Object detMessage=null;
	
	public TypeResult() {
		
	}
	
	public TypeResult(String code,String msg) {
		resuCode=code;
		resuMessage=msg;
	}
	
	public TypeResult(String code,String msg,Object obj) {
		resuCode=code;
		resuMessage=msg;
		detMessage=obj;
	}

	public String getResuCode() {
		return resuCode;
	}

	public void setResuCode(String resuCode) {
		this.resuCode = resuCode;
	}

	public String getResuMessage() {
		return resuMessage;
	}

	public void setResuMessage(String resuMessage) {
		this.resuMessage = resuMessage;
	}

	public Object getDetMessage() {
		return detMessage;
	}

	public void setDetMessage(Object detMessage) {
		this.detMessage = detMessage;
	}
	
}
