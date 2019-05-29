package local.blog.blogSystem.type;

import java.util.LinkedList;

public class TFile {
	String name;
	String type;
	
	LinkedList<Integer> usage;
	
	public TFile() {
		usage=new LinkedList<>();
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

	public LinkedList<Integer> getUsage() {
		return usage;
	}

	public void setUsage(LinkedList<Integer> usage) {
		this.usage = usage;
	}
}
