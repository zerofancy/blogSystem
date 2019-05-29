package local.blog.blogSystem.dao;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vdurmont.emoji.EmojiParser;

import local.blog.blogSystem.service.BlogService;
import local.blog.blogSystem.type.TBlog;

@Service
public class BlogServiceImpl implements BlogService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private int pgitems=20;//每页行数

	@Override
	public boolean addBlog(String title, String abs, String md,String html) {
		title=EmojiParser.removeAllEmojis(title);//这是为了过滤emoj表情
		abs=EmojiParser.removeAllEmojis(abs);
		md=EmojiParser.removeAllEmojis(md);
		return jdbcTemplate.update("insert into art(art_title,art_abs,art_md,art_html) values(?,?,?,?)", title, abs, md,html) > 0;
	}

	@Override
	public List<Map<String, Object>> getBlogList(int page) {
		return jdbcTemplate.queryForList("select id,art_title,art_abs,art_time,art_edtime,art_hits from art where not(art_title like '@%') order by art_edtime desc limit ?,?",(page-1)*pgitems,pgitems);
	}
	
	@Override
	public List<Map<String, Object>> getBlogListAllowHidden(int page) {
		return jdbcTemplate.queryForList("select id,art_title,art_abs,art_time,art_edtime,art_hits from art order by art_edtime desc limit ?,?",(page-1)*pgitems,pgitems);
	}
	
	@Override
	public TBlog getArticle(Integer id) {
		List<Map<String, Object>> tmp = jdbcTemplate.queryForList("select * from art where id=?", id);
		if (tmp.isEmpty()) {
			return null;
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Map<String, Object> tmpMap = tmp.get(0);
		try {
			return new TBlog(Integer.parseInt(tmpMap.get("id").toString()), tmpMap.get("art_title").toString(),
					(tmpMap.get("art_abs").toString()),
					new Timestamp(dateFormat.parse(tmpMap.get("art_time").toString()).getTime()),
					Integer.parseInt(tmpMap.get("art_hits").toString()), tmpMap.get("art_md").toString(),tmpMap.get("art_html").toString(),
					new Timestamp(dateFormat.parse(tmpMap.get("art_edtime").toString()).getTime()));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean editBlog(Integer id, String title, String abs, String md,String html) {
		title=EmojiParser.removeAllEmojis(title);
		abs=EmojiParser.removeAllEmojis(abs);
		md=EmojiParser.removeAllEmojis(md);
		return jdbcTemplate.update("update art set art_title=?,art_abs=?,art_md=?,art_html=?,art_edtime=CURRENT_TIMESTAMP where id=?", title,abs,md,html, id) > 0;
	}

	@Override
	public void hitArticle(Integer id) {
		jdbcTemplate.update("update art set art_hits=art_hits+1 where id=?",id);
	}

	@Override
	public boolean delBlog(Integer id) {
		return jdbcTemplate.update("delete from art where id=?", id) > 0;
	}

	@Override
	public Integer maId() {
		return jdbcTemplate.queryForObject("select max(id) from art",Integer.class);
	}

	@Override
	public Integer rndId() {
		Random ran=new Random();
		return jdbcTemplate.queryForObject("select id from art where id>? and not(art_title like '@%') limit 1",new Object[] {ran.nextInt(maId())},Integer.class);
	}

	@Override
	public String getArticleTitle(Integer id) {
		return jdbcTemplate.queryForObject("select art_title from art where id=?",new Object[] {id},String.class);
	}

	@Override
	public int getBlogListPageCount() {
		return jdbcTemplate.queryForObject("select count(*) from art where not(art_title like '@%')",Integer.class);
	}

	@Override
	public int getBlogListAllowHiddenPageCount() {
		return jdbcTemplate.queryForObject("select count(*) from art",Integer.class);
	}

	@Override
	public List<Map<String, Object>> getBlogListForSiteMap() {
		return jdbcTemplate.queryForList("select id,art_edtime from art where not(art_title like '@%')");
	}

}
