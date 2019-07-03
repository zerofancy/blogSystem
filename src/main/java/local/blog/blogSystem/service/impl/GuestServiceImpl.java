package local.blog.blogSystem.service.impl;

import java.util.List;
import java.util.Map;

import com.vdurmont.emoji.EmojiParser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import local.blog.blogSystem.service.GuestBookService;

@Service
public class GuestServiceImpl implements GuestBookService {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public boolean pubMessage(String nickName, String content) {
		nickName=EmojiParser.removeAllEmojis(nickName);//这是为了过滤emoj表情
		content=EmojiParser.removeAllEmojis(content);
		return jdbcTemplate.update("insert into guestbook(nickname,content,pubtime) values(?,?,CURRENT_TIMESTAMP)", nickName, content)>0;
	}

	@Override
	public List<Map<String, Object>> getMessageList() {
		return jdbcTemplate.queryForList("select * from guestbook order by pubtime desc");
	}

}
