package local.blog.blogSystem.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import local.blog.blogSystem.domain.TBlog;

@Service
public interface BlogService {
	boolean addBlog(String title,String abs,String md,String html);
	
	boolean editBlog(Integer id,String title,String abs,String md,String html);

	List<Map<String, Object>> getBlogList(int page);
	
	List<Map<String, Object>> getBlogListForSiteMap();
	
	int getBlogListPageCount();
	
	List<Map<String, Object>> getBlogListAllowHidden(int page);
	
	int getBlogListAllowHiddenPageCount();

	TBlog getArticle(Integer id);
	
	String getArticleTitle(Integer id);
	
	void hitArticle(Integer id);
	
	boolean delBlog(Integer id);
	
	Integer maId();
	
	Integer rndId();
}
