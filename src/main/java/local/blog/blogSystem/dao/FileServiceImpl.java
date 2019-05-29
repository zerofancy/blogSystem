package local.blog.blogSystem.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.BlogService;
import local.blog.blogSystem.service.FileService;
import local.blog.blogSystem.type.TBlog;
import local.blog.blogSystem.type.TFile;
import local.blog.blogSystem.type.TFileRel;

@Service
public class FileServiceImpl implements FileService {
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public boolean addFile(String name, String type) {
		return jdbcTemplate.update("insert into file(file_type,file_name) values(?,?)", type,name) > 0;
	}

	@Override
	public boolean delFile(String name) {
		return jdbcTemplate.update("delete from filerel where rel_filename=?\ndelete from file where file_name=?", name,name) > 0;
	}

	@Override
	public void refreshRel(LinkedList<String> fnames, Integer aid) {
		jdbcTemplate.update("delete from filerel where rel_artid=?", aid);
		for (String i :fnames) {
			jdbcTemplate.update("insert into filerel(rel_artid,rel_filename) values(?,?)", aid,i) ;
		}
	}

	@Override
	public LinkedList<TFile> getFiles() {
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select * from file");
		LinkedList<TFile> tmpReturn=new LinkedList<>();
		if (tmp.isEmpty()) {
			return new LinkedList<TFile>();
		}
		for(Map<String,Object> i:tmp) {
			TFile tmpFile=new TFile();
			tmpFile.setName(i.get("file_name").toString());
			tmpFile.setType(i.get("file_type").toString());
			tmpFile.getUsage().addAll(getUsage(tmpFile.getName()));
			tmpReturn.add(tmpFile);
		}
		return tmpReturn;
	}

	@Override
	public LinkedList<Integer> getUsage(String name) {
		LinkedList<Integer> tmpReturn=new LinkedList<>();
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select rel_artid from filerel where rel_filename=?",name);
		for(Map<String,Object> i:tmp) {
			tmpReturn.add((Integer) i.get("rel_artid"));
		}
		return tmpReturn;
		
	}

}
