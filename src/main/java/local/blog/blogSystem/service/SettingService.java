package local.blog.blogSystem.service;

import java.util.List;
import java.util.Map;

public interface SettingService {
    void writeConfig(String section,String key,String content);
    
    String getConfig(String section,String key);
}