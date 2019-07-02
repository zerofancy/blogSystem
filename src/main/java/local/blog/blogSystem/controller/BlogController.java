package local.blog.blogSystem.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import local.blog.blogSystem.service.AdminService;
import local.blog.blogSystem.service.BlogService;
import local.blog.blogSystem.service.FileService;
import local.blog.blogSystem.service.SettingService;
import local.blog.blogSystem.service.TagService;
import local.blog.blogSystem.type.TBlog;
import local.blog.blogSystem.type.TypeResult;
import local.blog.blogSystem.type.UsAdmin;

@Controller
@RequestMapping("/adm")
public class BlogController {
	@Autowired
	private BlogService blogService;
	@Autowired
	private AdminService adminSerivce;
	@Autowired
	private FileService fileService;
	@Autowired
	private TagService tagService;
	@Autowired
	private SettingService settingService;

	@RequestMapping("/")
	String index() {
		return "adm/index";
	}
	@RequestMapping("/add")
	String addArt() {
		return "adm/add";
	}
	@RequestMapping("/addHandle")
	@ResponseBody
	TypeResult addHandle(@RequestParam("title")String title,@RequestParam("abs")String abs,@RequestParam("md")String md,@RequestParam("html")String html) {
		if(blogService.addBlog(title, abs, md,html)) {
			LinkedList<String> fnames=new LinkedList<>();
			Matcher m=Pattern.compile(".*!\\[.*?\\]\\((.*?)( \".*?\")*?\\).*").matcher(md);
			while(m.find()) {
				if(m.group(1).startsWith("/file/down/")) {
					fnames.add(m.group(1).replaceAll("/file/down/", ""));
				}
			}
			fileService.refreshRel(fnames, blogService.maId());
			
			Matcher m2=Pattern.compile("\\[tag\\]:(.*?)\\n").matcher(md);
			if(m2.find()) {
				LinkedList<String> ll1=new LinkedList<String>();
				for(String i:m2.group(1).split("\\|")){
					ll1.add(i);
				}
				tagService.RefreshTags(blogService.maId(),ll1);
			}
			return new TypeResult("0","保存成功！");
		}
		return new TypeResult("1","保存失败！");
	}
	@RequestMapping(value= {"/arts","/arts/{page}"})
	String getBlogList(@PathVariable(name="page",required=false) Integer page,Model model) throws ParseException{
		if(page==null) {
			page=1;
		}
		if(page<1) {
			page=1;
		}
		List<Map<String, Object>> tmpDatas = blogService.getBlogListAllowHidden(page);
		List<TBlog> disDatas = new LinkedList<TBlog>();
		for (Map<String, Object> i : tmpDatas) {
			TBlog tmpData = new TBlog();
			tmpData.setId(Integer.parseInt(i.get("id").toString()));
			tmpData.setHits(Integer.parseInt(i.get("art_hits").toString()));
			tmpData.setTitle((String) i.get("art_title"));
			tmpData.setAbs((String) i.get("art_abs"));
			tmpData.setMd((String) i.get("art_md"));
			tmpData.setHTML((String) i.get("art_html"));
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			tmpData.setTime(dateFormat.parse(i.get("art_time").toString()));
			tmpData.setEdtime(dateFormat.parse(i.get("art_edtime").toString()));
			disDatas.add(tmpData);
		}
		model.addAttribute("arts", disDatas);
		model.addAttribute("page",page);
		int tmp=blogService.getBlogListAllowHiddenPageCount();
		model.addAttribute("maxpage",tmp/20+(tmp%20>0?1:0));
		return "adm/arts";
	}
	
	@RequestMapping("/edit")
	String edit(@RequestParam("id") Integer id,Model model) {
		model.addAttribute("blog", blogService.getArticle(id));
		return "adm/edit";
	}
	
	@RequestMapping("/editHandle")
	@ResponseBody
	TypeResult editHandle(@RequestParam("id") Integer id,@RequestParam("title")String title,@RequestParam("abs")String abs,@RequestParam("md")String md,@RequestParam("html")String html) {
		if(blogService.editBlog(id,title, abs, md,html)) {
			LinkedList<String> fnames=new LinkedList<>();
			Matcher m=Pattern.compile(".*!\\[.*?\\]\\((.*?)( \".*?\")*?\\).*").matcher(md);
			while(m.find()) {
				if(m.group(1).startsWith("/file/down/")) {
					fnames.add(m.group(1).replaceAll("/file/down/", ""));
				}
			}
			fileService.refreshRel(fnames, id);
			Matcher m2=Pattern.compile("\\[tag\\]:(.*?)\\n").matcher(md);
			if(m2.find()) {
				LinkedList<String> ll1=new LinkedList<String>();
				for(String i:m2.group(1).split("\\|")){
					ll1.add(i);
				}
				tagService.RefreshTags(id,ll1);
			}
			return new TypeResult("0","保存成功！");
		}
		return new TypeResult("1","保存失败！");
	}
	@RequestMapping("/delHandle")
	@ResponseBody
	TypeResult delHandle(@RequestParam("id") Integer id) {
		if(blogService.delBlog(id)) {
			fileService.refreshRel(new LinkedList<String>(), id);
			return new TypeResult("0","删除成功！");
		}
		return new TypeResult("1","删除失败！");
	}
	/**
	 * 登录页面
	 */
	@RequestMapping("/login")
	public String loginPage() {
		return "adm/login";
	}
	/**
	 * 登录提交信息的处理
	 * 
	 * @param usr 用户名
	 * @param pwd 密码
	 * @param rnd 随机字符串
	 */
	@RequestMapping("/login/handle")
	@ResponseBody
	UsAdmin loginHandle(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "usr", required = false) String usr,
			@RequestParam(name = "pwd", required = false) String pwd,
			@RequestParam(name = "rnd", required = false) String rnd) {
		Random ran=new Random();
		HttpSession session = request.getSession();
		UsAdmin tmpUs = adminSerivce.admLogin(usr, rnd, pwd);
		if (tmpUs == null) {
			return new UsAdmin();
		} else {
			tmpUs.setToken(ran.nextLong()+"");
			adminSerivce.refreshToken(tmpUs.getId(), tmpUs.getToken());
			System.out.println(tmpUs.getToken());
			session.setAttribute("user", tmpUs);
			return tmpUs;
		}
	}
	@RequestMapping("/logout")
	void logout(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		session.setAttribute("user", null);
		try {
			response.sendRedirect("/adm/login");
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}
	}
	/**
	 * 用户管理页面
	 * @param model
	 * @return
	 */
	@RequestMapping("/user")
	String userManagePage(Model model) {
		List<Map<String,Object>> tmpUsers= adminSerivce.getAllUsers();
		List<UsAdmin> disUsers=new LinkedList<>();
		for(Map<String,Object> i:tmpUsers) {
			UsAdmin tmpUser=new UsAdmin();
			tmpUser.setId((int)i.get("id"));
			tmpUser.setName((String) i.get("ad_name"));
			disUsers.add(tmpUser);
		}
		model.addAttribute("users", disUsers);
		return "adm/user";
	}
	
	@RequestMapping("/adduser")
	@ResponseBody
	Map<String,Integer> userAdd(@RequestParam("name") String name,@RequestParam("pwd") String pwd) {
		Map<String,Integer> tmpReturn=new HashMap<String,Integer>();
		tmpReturn.put("status", adminSerivce.addUser(name, pwd)?1:0);
		return tmpReturn;
	}
	@RequestMapping("/setPassword")
	@ResponseBody
	boolean rndPassword(@RequestParam("id") Long id,@RequestParam("pwd") String pwd) {
		return adminSerivce.setPwd(id, pwd);
	}
	@RequestMapping("/deluser")
	@ResponseBody
	boolean userDel(@RequestParam("id") Long id) {
		return adminSerivce.delUser(id);
	}
	@RequestMapping("/help")
	String help(Model model) {
		model.addAttribute("blog", settingService.getConfig("sys", "helpmd"));
		return "adm/help";
	}
	
	@RequestMapping("/helpHandle")
	@ResponseBody
	TypeResult editHandle(@RequestParam("md")String md) {
		settingService.writeConfig("sys", "helpmd", md);
		return new TypeResult("0","保存成功！");
	}

	@RequestMapping("/set")
	String settings(Model model) {
		model.addAttribute("beian", settingService.getConfig("sys", "beian"));
		model.addAttribute("urlreg", settingService.getConfig("sys", "urlreg"));
		model.addAttribute("url", settingService.getConfig("sys", "url"));
		model.addAttribute("headercode", settingService.getConfig("sys", "headercode"));
		return "adm/set";
	}
	
	@RequestMapping("/setHandle")
	void setHandle(@RequestParam("beian") String beian,@RequestParam("url") String url,@RequestParam("urlreg") String urlreg,@RequestParam("headercode") String headercode,HttpServletResponse response) throws IOException {
		settingService.writeConfig("sys", "beian", beian);
		settingService.writeConfig("sys", "urlreg", urlreg);
		settingService.writeConfig("sys", "url", url);
		settingService.writeConfig("sys", "headercode", headercode);
		response.sendRedirect("/adm/set");
	}
	@RequestMapping("/about")
	String about(Model model) {
		model.addAttribute("blog", settingService.getConfig("sys", "about"));
		return "adm/about";
	}
	
	@RequestMapping("/aboutHandle")
	@ResponseBody
	TypeResult aboutHandle(@RequestParam("md")String md,@RequestParam("html")String html) {
		settingService.writeConfig("sys", "about", md);
		settingService.writeConfig("sys", "about_html", html);
		return new TypeResult("0","保存成功！");
	}

	@RequestMapping("/plause")
	@ResponseBody
	TypeResult plause(HttpServletRequest request,HttpServletResponse response){
		UsAdmin user=(UsAdmin) request.getSession().getAttribute("user");
		System.out.println(user);
		System.out.println(user.getToken());
		if(user!=null&&adminSerivce.refreshToken(user.getId(),user.getToken())){
			return new TypeResult("0","success");
		}else{
			System.out.println(user.getToken());
			request.getSession().setAttribute("user", null);
			return new TypeResult("4","你的账户在其他设备登录。");
		}
	}
}
