package local.blog.blogSystem.service;

import java.util.List;
import java.util.Map;

public interface GuestBookService {
	boolean pubMessage(String nickName,String content);
	
	public List<Map<String, Object>> getMessageList();
}