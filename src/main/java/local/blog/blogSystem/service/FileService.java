package local.blog.blogSystem.service;

import java.util.LinkedList;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.type.TFile;

@Service
public interface FileService {
	/**
	 * 直接向数据库添加一条记录
	 * @param name
	 * @param type
	 * @return 
	 */
	boolean addFile(String name,String type);
	/**
	 * 删除表file和filerel所有相关记录
	 * @param name
	 * @return 
	 */
	boolean delFile(String name);
	/**
	 * 删除原有rel
	 * 添加rel
	 * @param fnames
	 * @param aid
	 */
	void refreshRel(LinkedList<String> fnames,Integer aid);
	/**
	 * 所有文件列表
	 */
	LinkedList<TFile> getFiles();
	
	LinkedList<Integer> getUsage(String name);
}
