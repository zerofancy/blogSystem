package local.blog.blogSystem.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface TagService {
	void RefreshTags(Integer id,LinkedList<String> tags);
	
	LinkedList<String> getTags();
	
	List<Map<String, Object>> getArtByTag(String tag);
}
