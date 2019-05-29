package local.blog.blogSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import local.blog.blogSystem.interceptor.AdmInterceptor;
import local.blog.blogSystem.interceptor.UrlInterceptor;

@Configuration

public class MyWebAppConfigurer implements WebMvcConfigurer {
	 @Bean
	    public HandlerInterceptor getUrlInterceptor(){
	        return new UrlInterceptor();
	    }
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 多个拦截器组成一个拦截器链
		// addPathPatterns 用于添加拦截规则
		// excludePathPatterns 用户排除拦截

		registry.addInterceptor(new AdmInterceptor()).addPathPatterns("/adm/**")
													.excludePathPatterns("/adm/login")
													.excludePathPatterns("/adm/login/**")
													.addPathPatterns("/file/**")
													.excludePathPatterns("/file/down/**")
													;
		// 可以在此定义多个拦截器
		 registry.addInterceptor(getUrlInterceptor()).addPathPatterns("/**")
		 											.excludePathPatterns("/adm/**")
		 											.excludePathPatterns("/file/down/**")
		 											.excludePathPatterns("/js/**")
		 											.excludePathPatterns("/css/**")
		 											.excludePathPatterns("/fonts/**")
		 											.excludePathPatterns("/lib/**")
		 											.excludePathPatterns("/plugins/**")
		 											;
	}

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		AntPathMatcher matcher = new AntPathMatcher();
		matcher.setCaseSensitive(false);// 大小写不敏感
		configurer.setPathMatcher(matcher);
	}
}
