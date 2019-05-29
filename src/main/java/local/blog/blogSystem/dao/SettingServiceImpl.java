package local.blog.blogSystem.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.SettingService;

@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public void writeConfig(String section, String key, String content) {
		List<String> tmp = null;
		try {
			 tmp= jdbcTemplate.queryForList("select strcontent from settings where strsection=? and strkey=?",String.class,new Object[]{section,key});
		}
		catch(Exception e){
			
		}
		if(tmp.isEmpty()) {
			 jdbcTemplate.update("insert into settings(strsection,strkey,strcontent,vistime) values(?,?,?,?)",section,key, content,new Timestamp(System.currentTimeMillis()));
		}
		jdbcTemplate.update("update settings set strcontent=?,vistime=? where strsection=? and strkey=?", content,new Timestamp(System.currentTimeMillis()) ,section,key);
	}

	@Override
	public String getConfig(String section, String key) {
		List<String> tmp = null;
		try {
			 tmp= jdbcTemplate.queryForList("select strcontent from settings where strsection=? and strkey=?",String.class,new Object[]{section,key});
		}
		catch(Exception e){
			
		}
		if(tmp.isEmpty()) {
			tmp.add("");
		}
		return tmp.get(0);
	}
}