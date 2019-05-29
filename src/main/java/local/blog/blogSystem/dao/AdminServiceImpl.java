package local.blog.blogSystem.dao;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.AdminService;
import local.blog.blogSystem.type.UsAdmin;
import local.blog.blogSystem.util.StringUtils;

@Service
public class AdminServiceImpl implements AdminService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public UsAdmin admLogin(String UserName, String randKey, String hashedKey) {
		// 
		if(UserName==null||randKey==null||hashedKey==null) {
			return null;
		}
		UsAdmin tmpUsr = new UsAdmin();
		String sql = "select * from adm where ad_name=?";
		Map<String, Object> tmp = null;
		try {
			tmp = jdbcTemplate.queryForMap(sql, UserName);
		} catch (Exception e) {
			return null;
		}
		if (StringUtils.getMD5(tmp.get("ad_pwd") + randKey).equals(hashedKey.toUpperCase())) {
			tmpUsr.setName(tmp.get("ad_name").toString());
			tmpUsr.setLevel(Integer.parseInt( tmp.get("ad_level").toString()));
			return tmpUsr;
		}
		return null;
	}

	@Override
	public boolean addUser(String name, String pwd) {
		// 
		return jdbcTemplate.update("insert into adm(ad_name,ad_pwd,ad_level) values(?,?,1)",name, pwd)>0;
	}

	@Override
	public boolean delUser(Long id) {
		// 
		return jdbcTemplate.update("delete from adm where id = ?", id)>0;
	}

	@Override
	public List<Map<String, Object>> getAllUsers() {
 		return jdbcTemplate.queryForList("select * from adm");
	}

	@Override
	public boolean setPwd(Long id, String pwd) {
		return jdbcTemplate.update("UPDATE adm SET ad_pwd=? WHERE id = ?",pwd, id)>0;
	}	

}
