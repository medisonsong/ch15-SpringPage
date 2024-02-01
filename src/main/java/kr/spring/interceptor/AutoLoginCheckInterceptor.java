package kr.spring.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AutoLoginCheckInterceptor implements HandlerInterceptor{

	@Autowired
	MemberService memberService;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response,
							 Object handler)throws Exception{
		
		log.debug("<<AutoLoginCheckInterceptor 진입>>");
		
		HttpSession session = request.getSession();
		MemberVO user = (MemberVO)session.getAttribute("user");
		
		//쿠키를 받고 au-log가 있으면 쿠키를 받고 없으면 자동로그인 처리 x
		//다만, 자동로그인 시에도 서버의 au-log 데이터와 일치해야함 
		if(user==null) {
			Cookie now_cookie = findCookie(request.getCookies(),"au-log");
			if(now_cookie!=null) {
				MemberVO memberVO = memberService.selectAu_id(now_cookie.getValue());
				
				log.debug("<<자동로그인 여부 체크 MemberVO>> : " + memberVO);
				if(memberVO != null && memberVO.getAuth()>=2) { //정지,탈퇴회원을 제외
					//일반회원부터 자동 로그인 처리
					session.setAttribute("user", memberVO);
					log.debug("<<자동로그인 성공>>");
				}
			}
		}
		return true;
	}
	
	//쿠키 받는 함수 밖에서 생성 (복잡도를 낮추기 위해)
	private Cookie findCookie(Cookie[] cookies, String name) {
		if(cookies == null || cookies.length == 0) { //쿠키 값이 없을 때
			return null;
		}else {
			for(int i=0;i<cookies.length;i++) {
				String cookie_name = cookies[i].getName();
				if(cookie_name.equals(name)) {
					return cookies[i];
				}
			}
			return null;
		}
	}
}
