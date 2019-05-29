package local.blog.blogSystem.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import local.blog.blogSystem.service.BlogService;
import local.blog.blogSystem.service.GuestBookService;
import local.blog.blogSystem.service.SettingService;
import local.blog.blogSystem.service.TagService;
import local.blog.blogSystem.type.TBlog;
import local.blog.blogSystem.type.TypeResult;

@Controller
public class MainController {
	@Autowired
	private BlogService blogService;
	@Autowired
	private TagService tagService;
	@Autowired
	private SettingService settingService;
	@Autowired
	private GuestBookService guestBookService;
	
	@RequestMapping(value= {"/","/{page}"})
	String index(@PathVariable(name="page",required=false) Integer page,Model model) throws ParseException {
		if(page==null) {
			page=1;
		}
		if(page<1) {
			page=1;
		}
		List<Map<String, Object>> tmpDatas = blogService.getBlogList(page);
		List<TBlog> disDatas = new LinkedList<TBlog>();
		for (Map<String, Object> i : tmpDatas) {
			TBlog tmpData = new TBlog();
			tmpData.setId(Integer.parseInt(i.get("id").toString()));
			tmpData.setHits(Integer.parseInt(i.get("art_hits").toString()));
			tmpData.setTitle((String) i.get("art_title"));
			tmpData.setAbs((String) i.get("art_abs"));
			tmpData.setMd((String) i.get("art_md"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			tmpData.setTime(dateFormat.parse(i.get("art_time").toString()));
			tmpData.setEdtime(dateFormat.parse(i.get("art_edtime").toString()));
			disDatas.add(tmpData);
		}
		model.addAttribute("arts", disDatas);
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		model.addAttribute("page",page);
		int tmp=blogService.getBlogListPageCount();
		model.addAttribute("maxpage",tmp/20+(tmp%20>0?1:0));
		return "index";
	}

	@RequestMapping("/show/{id}")
	String show(@PathVariable("id") Integer id, Model model) {
		TBlog tmpBlog=blogService.getArticle(id);
		if(tmpBlog.getTitle().startsWith("@@")) {
			tmpBlog.setMd("#系统提示\n您请求的内容已经被管理员禁止！");
			tmpBlog.setHTML("拒绝访问！");
		}else {
			blogService.hitArticle(id);
		}
		tmpBlog.setTitle(tmpBlog.getTitle().replaceAll("^@*", ""));
		model.addAttribute("blog", tmpBlog);
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		return "show";
	}
	
	@RequestMapping("/rnd")
	void rndArticle(HttpServletResponse response) throws IOException {
		response.sendRedirect("/show/"+blogService.rndId());
	}
	
	@RequestMapping("/time")
	@ResponseBody
	TypeResult getTime() {
		return new TypeResult("0","获取系统日期时间。",new Date());
	}
	
	@RequestMapping("/tags")
	String tags(Model model) {
		model.addAttribute("tags", tagService.getTags());
		model.addAttribute("hdl",tagService);
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		return "tags";
	}
	
	@RequestMapping("/about")
	String about(Model model) {
		//model.addAttribute("blog", settingService.getConfig("sys", "about"));
		model.addAttribute("about_html", settingService.getConfig("sys", "about_html"));
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		return "about";
	}
	
	@RequestMapping("/guest")
	String guestBook(Model model) throws ParseException {
		List<Map<String, Object>> tmpDatas = guestBookService.getMessageList();
		List<TBlog> disDatas = new LinkedList<TBlog>();
		for (Map<String, Object> i : tmpDatas) {
			TBlog tmpData = new TBlog();
			tmpData.setId(Integer.parseInt(i.get("id").toString()));
			tmpData.setTitle((String) i.get("nickname"));
			tmpData.setMd((String) i.get("content"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			tmpData.setTime(dateFormat.parse(i.get("pubtime").toString()));
			disDatas.add(tmpData);
		}
		model.addAttribute("msgs", disDatas);
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		return "guest";
	}
	
	@RequestMapping("/guest/pub")
	@ResponseBody
	TypeResult pubGuestMessage(@RequestParam("nickname") String nickName,@RequestParam("content") String content) {
		if(nickName.length()>200) {
			nickName=nickName.substring(0, 199);
		}
		if(content.length()>1000) {
			content=content.substring(0, 999);
		}
		TypeResult res=new TypeResult();
		if(guestBookService.pubMessage(nickName, content)) {
			res.setResuCode("0");
			res.setResuMessage("保存成功");
		}else {
			res.setResuCode("1");
			res.setResuMessage("保存失败。");	
		}
		return res;
	}
}
