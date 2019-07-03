package local.blog.blogSystem.domain;

import java.util.Date;

public class TBlog {
	Integer id;
	String title;
	String abs;
	Date time;
	Date edtime;
	Integer hits;
	String md;
	String html;

	public TBlog() {

	}

	public TBlog(Integer id, String title, String abs, Date time, Integer hits, String md,String html,Date edtime) {
		this.id = id;
		this.title = title;
		this.abs = abs;
		this.time = time;
		this.hits = hits;
		this.md = md;
		this.html=html;
		this.edtime=edtime;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbs() {
		return abs;
	}

	public void setAbs(String abs) {
		this.abs = abs;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date date) {
		this.time = date;
	}

	public Integer getHits() {
		return hits;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}

	public String getMd() {
		return md;
	}

	public void setMd(String md) {
		this.md = md;
	}

	public Date getEdtime() {
		return edtime;
	}

	public void setEdtime(Date date) {
		this.edtime = date;
	}
	
	public String getHTML() {
		return html;
	}

	public void setHTML(String html) {
		this.html=html;
	}
}
