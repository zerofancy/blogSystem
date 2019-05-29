package local.blog.blogSystem.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.sqlite.date.DateFormatUtils;

import local.blog.blogSystem.service.BlogService;
import local.blog.blogSystem.service.SettingService;

@Controller
public class SiteMapController {
	@Autowired
	private SettingService settingService;
	@Autowired
	private BlogService blogService;


	public String getxml(String loc, String lastmod, String changefreq, Float priority) {
		if(lastmod.length()>0) {
			String pattern = "yyyy-MM-dd'T'HH:mm:ss+08";
			try  
			{  
			    SimpleDateFormat sdf = new SimpleDateFormat();
			    sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
			    Date date = sdf.parse(lastmod);
			    sdf.applyPattern(pattern);
			    lastmod=sdf.format(date);
			}  
			catch (ParseException e)  
			{  
			    System.out.println(e.getMessage());  
			}  		}
		return "<url><loc>" + loc + "</loc><lastmod>" + lastmod + "</lastmod><changefreq>" + changefreq
				+ "</changefreq><priority>" + priority + "</priority></url>";
	}

	@RequestMapping(value = "/sitemap.xml")
	public void siteMap(Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		settingService.getConfig("sys", "url");
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
		xml += getxml(settingService.getConfig("sys", "url"), "", "daily", 1f);
		int tmp=blogService.getBlogListPageCount();
		for(int i=1;i<=tmp/20+(tmp%20>0?1:0);i++) {
			xml+=getxml(settingService.getConfig("sys", "url")+"/"+i, "", "daily", 1f);
		}
		xml += getxml(settingService.getConfig("sys", "url") + "/tags", "", "daily", 1f);
		xml += getxml(settingService.getConfig("sys", "url") + "/about", "", "monthly", 0.4f);
		xml += getxml(settingService.getConfig("sys", "url") + "/guest", "", "weekly", 0.4f);
		List<Map<String, Object>> tmpDatas = blogService.getBlogListForSiteMap();
		for(Map<String,Object>i:tmpDatas) {
			xml+=getxml(settingService.getConfig("sys", "url") + "/show/"+i.get("id").toString(),i.get("art_edtime").toString(),"monthly",0.5f);
		}
		xml += "</urlset>";
		ByteArrayInputStream bf1 = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		ServletContext context = request.getServletContext();
		String mimeType = context.getMimeType("text/xml");
		response.setContentType(mimeType);
		response.setContentLength(xml.length());
		try {
			IOUtils.copy(bf1, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value = "/robots.txt")
	public void robots(Long id, HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {
		settingService.getConfig("sys", "url");
		String txt = "User-agent: *\r\n" + 
				"Disallow: /adm/\r\n" + 
				"";
		ByteArrayInputStream bf1 = new ByteArrayInputStream(txt.getBytes("UTF-8"));
		ServletContext context = request.getServletContext();
		String mimeType = context.getMimeType("text/xml");
		response.setContentType(mimeType);
		response.setContentLength(txt.length());
		try {
			IOUtils.copy(bf1, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
