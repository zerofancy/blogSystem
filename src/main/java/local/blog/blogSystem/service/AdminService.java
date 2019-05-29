package local.blog.blogSystem.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import local.blog.blogSystem.type.UsAdmin;

@Service
public interface AdminService {
	public UsAdmin admLogin(String UserName,String randKey,String hashedKey);
	/**
	 * 添加用户
	 * @param name 用户名
	 * @param pwd 密码
	 */
	boolean addUser(String name,String pwd);
	
	/**
	 * 删除用户
	 */
	boolean delUser(Long id);
	
	/**
	 * 所有用户
	 */
	List<Map<String, Object>> getAllUsers();
	
	public boolean setPwd(Long id,String pwd);

}
