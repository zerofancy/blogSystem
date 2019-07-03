package local.blog.blogSystem.service.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.TagService;

@Service

public class TagServiceImpl implements TagService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void RefreshTags(Integer id, LinkedList<String> tags) {
		jdbcTemplate.update("delete from tagrel where art_id=?", id);
		for (String i : tags) {
			jdbcTemplate.update("insert into tagrel(art_id,tag_name) values(?,?)", id, i);
			jdbcTemplate.update("insert ignore into tag(tagname) values(?)", i);
		}
	}

	@Override
	public LinkedList<String> getTags() {
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select * from tag");
		LinkedList<String> tmpReturn = new LinkedList<>();
		if (tmp.isEmpty()) {
			return new LinkedList<String>();
		}
		for (Map<String, Object> i : tmp) {
			tmpReturn.add(i.get("tagname").toString());
		}
		return tmpReturn;
	}

	@Override
	public List<Map<String, Object>> getArtByTag(String tag) {
		List<Map<String, Object>> tmpReturn= jdbcTemplate.queryForList(
				"select id,art_title,art_abs,art_time,art_edtime,art_hits from art where id in (select art_id from tagrel where tag_name=?) order by art_edtime desc",
				tag);
		if(tmpReturn.size()==0) {
			jdbcTemplate.update("delete from tag where tagname=?", tag);
		}
		return tmpReturn;
	}

}
