package local.blog.blogSystem.dao;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.FileService;
import local.blog.blogSystem.service.SettingService;
import local.blog.blogSystem.type.TFile;
import local.blog.blogSystem.type.TypeResult;

@Service
public class FileServiceImpl implements FileService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private SettingService settingService;
	private int pgitems = 20;// 每页行数

	@Override
	public boolean addFile(String name, String type) {
		return jdbcTemplate.update("insert into file(file_type,file_name) values(?,?)", type, name) > 0;
	}

	@Override
	public boolean delFile(String name) {
		return jdbcTemplate.update("delete from filerel where rel_filename=?\ndelete from file where file_name=?", name,
				name) > 0;
	}

	@Override
	public void refreshRel(LinkedList<String> fnames, Integer aid) {
		jdbcTemplate.update("delete from filerel where rel_artid=?", aid);
		for (String i : fnames) {
			jdbcTemplate.update("insert into filerel(rel_artid,rel_filename) values(?,?)", aid, i);
		}
	}

	@Override
	public LinkedList<TFile> getFiles(int page) {
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select * from file limit ?,?", (page - 1) * pgitems,
				pgitems);
		LinkedList<TFile> tmpReturn = new LinkedList<>();
		if (tmp.isEmpty()) {
			return new LinkedList<TFile>();
		}
		for (Map<String, Object> i : tmp) {
			TFile tmpFile = new TFile();
			tmpFile.setName(i.get("file_name").toString());
			tmpFile.setType(i.get("file_type").toString());
			tmpFile.getUsage().addAll(getUsage(tmpFile.getName()));
			tmpReturn.add(tmpFile);
		}
		return tmpReturn;
	}

	@Override
	public LinkedList<Integer> getUsage(String name) {
		LinkedList<Integer> tmpReturn = new LinkedList<>();
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select rel_artid from filerel where rel_filename=?",
				name);
		for (Map<String, Object> i : tmp) {
			tmpReturn.add((Integer) i.get("rel_artid"));
		}
		return tmpReturn;
	}

	@Override
	public LinkedList<TFile> getUnusedFiles(int page) {
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList(
				"select * from file where file_name not in(select rel_filename from filerel) limit ?,?",
				(page - 1) * pgitems, pgitems);
		LinkedList<TFile> tmpReturn = new LinkedList<>();
		if (tmp.isEmpty()) {
			return new LinkedList<TFile>();
		}
		for (Map<String, Object> i : tmp) {
			TFile tmpFile = new TFile();
			tmpFile.setName(i.get("file_name").toString());
			tmpFile.setType(i.get("file_type").toString());
			tmpFile.getUsage().addAll(getUsage(tmpFile.getName()));
			tmpReturn.add(tmpFile);
		}
		return tmpReturn;
	}

	@Override
	public int getUnusedCount() {
		return (jdbcTemplate.queryForObject(
				"select count(*) from file where file_name not in(select rel_filename from filerel)", Integer.class)
				- 1) / 20 + 1;
	}

	@Override
	public int getCount() {
		return (jdbcTemplate.queryForObject("select count(*) from file", Integer.class) - 1) / pgitems + 1;
	}

	@Override
	public TypeResult deleteFile(String filename) {
		Integer relyCount=jdbcTemplate.queryForObject("select count(*) from filerel where rel_filename=?",new Object[]{filename}, Integer.class);
		if(relyCount>0){
			return new TypeResult("2","删除文件"+filename+"失败，因为文件被其他文章引用。");
		}
		if(jdbcTemplate.update("delete from file where file_name=?",filename)>0){
			;
		}else{
			return new TypeResult("3","删除文件"+filename+"失败，从数据库中移除记录失败。");
		}
		String path=settingService.getConfig("sys", "uppath");
		File file=new File(path+filename);
		file.delete();
		return new TypeResult("0","删除文件"+filename+"成功");
	}

}
