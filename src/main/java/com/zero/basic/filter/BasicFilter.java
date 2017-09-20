package com.zero.basic.filter;
import com.zero.logic.util.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 基础拦截器
 * @auther Deram Zhao
 * @creatTime 2017/6/1
 */
@WebFilter(filterName = "BasicFilter",urlPatterns = "/*")
public class BasicFilter implements Filter {
    private static  final Logger LOGGER= LoggerFactory.getLogger(BasicFilter.class);
    public  static String user_id ="";
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        HttpServletResponse response = (HttpServletResponse) servletResponse;

        LOGGER.info(request.getRequestURI()+"执行过滤操作");
        user_id = request.getHeader("user_id");
        String method = request.getMethod();
        filterChain.doFilter(servletRequest, servletResponse);
       /* if ("null".equals(user_id)){
            filterChain.doFilter(servletRequest, servletResponse);
        }else{
            servletResponse.getWriter().write("验证失败");
        }*/
        //当用户未登录时  获取的查询请求、获取分类树请求（book:/getByPage,/queryByTypeIdAndKeyword,/getBookByBookId;bookType:/getByPage,）
      /*  boolean checkUrl =false;
        switch (request.getRequestURI()){
            case "/book/getByPage":checkUrl =true;
                break;
            case "/book/queryByTypeIdAndKeyword":checkUrl=true;
                break;
            case "/book/getBookByBookId":checkUrl=true;
                break;
            case "/book/getBooksByTypeId":checkUrl=true;
                break;
            case "/book/getDiscount":checkUrl=true;
                break;
            case "/bookType/getByPage":checkUrl=true;
                break;
            case "/bookType/getTree":checkUrl=true;
                break;
            case "/bookType/getBookTypeByTypeId":checkUrl=true;
                break;
            case "/user/resetPsw":checkUrl=true;
                break;
            case "/user/addUser":checkUrl=true;
                break;
            case "/user/existsUserCode":checkUrl=true;
                break;
            case "/email/sendVerifyCode":checkUrl=true;
                break;
            default:checkUrl=false;
        }

        //静态资源的过滤要单独处理不能喝其他的请求混合处理
        if (request.getRequestURI().startsWith("/bookpicture/")){
            filterChain.doFilter(servletRequest, servletResponse);
        }else if (request.getRequestURI().startsWith("/swagger") || request.getRequestURI().startsWith("/webjars/") ||request.getRequestURI().startsWith("/v2/")){//不过滤swagger插件请求 发布时记得注释掉
            filterChain.doFilter(servletRequest, servletResponse);
        }else if (checkUrl || "/user/login".equals(request.getRequestURI())){
            filterChain.doFilter(servletRequest, servletResponse);
        }else {
            boolean checkToken = TokenUtil.checkToken(user_id, request.getHeader("token"));
            if (checkToken){
                System.out.println("校验成功");
                System.out.println("token 的值是："+TokenUtil.getToken(user_id));
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }*/

       /* //不是登录的请求都要进行过滤
        if (!request.getRequestURI().equals("/user/login")){
            boolean checkToken = TokenUtil.checkToken(user_id, request.getHeader("token"));
            if (checkToken){
                System.out.println("校验成功");
                System.out.println("token 的值是："+TokenUtil.getToken(user_id));
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }else {
                System.out.println("校验失败");
                return;
            }
        }*/
    }

    @Override
    public void destroy() {
        System.out.println("过滤器销毁");
    }
}
