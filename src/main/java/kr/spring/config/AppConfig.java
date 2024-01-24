package kr.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesConfigurer;
import org.springframework.web.servlet.view.tiles3.TilesView;
import org.springframework.web.servlet.view.tiles3.TilesViewResolver;

import kr.spring.interceptor.LoginCheckInterceptor;
//자바코드 기반 설정 클래스
@Configuration
public class AppConfig implements WebMvcConfigurer {
	private LoginCheckInterceptor loginCheck;
	
	@Bean
	public LoginCheckInterceptor interceptor2() {
		loginCheck = new LoginCheckInterceptor();
		return loginCheck;
	}
	
	//intercept 등록
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		//LoginCheckInterceptor 설정
		registry.addInterceptor(loginCheck)
				.addPathPatterns("/member/myPage")
				.addPathPatterns("/board/write")
				.addPathPatterns("/board/update")
				.addPathPatterns("/board/delete")
				.addPathPatterns("/talk/talkRoomWrite")
				.addPathPatterns("/talk/talkList")
				.addPathPatterns("/talk/talkDetail");
	}
	
	
	@Bean
	public TilesConfigurer tilesConfigurer() {
		final TilesConfigurer configurer = new TilesConfigurer();
		//tilesdef.xml의 경로와 파일명 지정
		configurer.setDefinitions(new String[] {"/WEB-INF/tiles-def/main.xml",
												"/WEB-INF/tiles-def/member.xml",
												"/WEB-INF/tiles-def/board.xml",
												"/WEB-INF/tiles-def/talk.xml"
												});
		configurer.setCheckRefresh(true);
		return configurer;
	} 
	
	
	@Bean
	public TilesViewResolver tilesViewResolver() {
		final TilesViewResolver tilesViewResolver = new TilesViewResolver();
		
		tilesViewResolver.setViewClass(TilesView.class);
		return tilesViewResolver;
	}
}
