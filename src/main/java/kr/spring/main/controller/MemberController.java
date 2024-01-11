package kr.spring.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	/*=======================
	 * 자바빈(VO) 초기화
	 *======================= */
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	/*=======================
	 * 회원가입
	 *======================= */
	//회원가입 폼 호출
	@GetMapping("/member/registerUser")
	public String form() {
		return "memberRegister"; //tiles설정명 (/등의 경로가 없기 때문에 jsp가 아니라는 걸 알 수 있음)
	}
	
}
