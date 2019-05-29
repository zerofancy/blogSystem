package local.blog.blogSystem.service;

public interface SettingService {
    void writeConfig(String section,String key,String content);
    
    String getConfig(String section,String key);
}