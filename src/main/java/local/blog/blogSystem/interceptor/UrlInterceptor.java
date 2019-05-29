package local.blog.blogSystem.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import local.blog.blogSystem.service.SettingService;

/**
 * 自定义拦截器1
 */
public class UrlInterceptor implements HandlerInterceptor {
	@Autowired
	private SettingService settingService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if(request.getRequestURL().toString().matches(settingService.getConfig("sys", "urlreg"))) {
			return true;
		}
		response.sendRedirect(settingService.getConfig("sys", "url"));
		return false;
		// return true;// 只有返回true才会继续向下执行，返回false取消当前请求
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// System.out.println(">>>MyInterceptor1>>>>>>>请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		// System.out.println(">>>MyInterceptor1>>>>>>>在整个请求结束之后被调用，也就是在DispatcherServlet
		// 渲染了对应的视图之后执行（主要是用于进行资源清理工作）");
	}

}