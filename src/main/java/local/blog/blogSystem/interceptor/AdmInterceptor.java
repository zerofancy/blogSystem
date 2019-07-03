package local.blog.blogSystem.interceptor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import local.blog.blogSystem.domain.UsAdmin;
import local.blog.blogSystem.service.AdminService;

/**
 * 自定义拦截器1
 */
public class AdmInterceptor implements HandlerInterceptor {
	@Autowired
	private AdminService adminService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		// System.out.println(">>>MyInterceptor1>>>>>>>在请求处理之前进行调用（Controller方法调用之前）");

		Map<String, Integer> ris = new HashMap<String, Integer>();
		ris.put("/admin/index", 1);
		//管理首页权限值
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute("user") != null) {
			for (String k : ris.keySet()) {
				if (request.getServletPath().toLowerCase().equals(k.toLowerCase()))//.toLowerCase()实现不区分大小写
					if ((int) ris.get(k) > ((UsAdmin) session.getAttribute("user")).getLevel()) {
						PrintWriter printWriter = response.getWriter();
						printWriter.write("{code:1,message:\"You are not permitted to this page!\"}");
						return false;
					}
			}
			UsAdmin user= (UsAdmin) session.getAttribute("user");
			if(adminService.refreshToken(user.getId(), user.getToken())){
				return true;
			}else{
				session.setAttribute("user", null);
				response.sendRedirect("/adm/");
				return false;
			}
		} else {

			PrintWriter printWriter = response.getWriter();
			printWriter.write("{code:0,message:\"not login!\"}");
			response.sendRedirect("/adm/login");
			return false;
		}
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