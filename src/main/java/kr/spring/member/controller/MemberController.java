package kr.spring.member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.AuthCheckException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberController {
	@Autowired
	private MemberService memberService;
	
	/*=======================
	 *     자바빈(VO) 초기화
	 *======================= */
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	/*=======================
	 *        회원가입
	 *======================= */
	//회원가입 폼 호출
	@GetMapping("/member/registerUser")
	public String form() {
		return "memberRegister"; //tiles설정명 (/등의 경로가 없기 때문에 jsp가 아니라는 걸 알 수 있음)
	}
	//전송된 회원 데이터 처리
	@PostMapping("/member/registerUser")
	public String submit(@Valid MemberVO memberVO, BindingResult result, Model model, HttpServletRequest request) {
		log.debug("<<회원가입>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}
		
		//회원가입
		memberService.insertMember(memberVO);
		
		model.addAttribute("accessTitle", "회원가입");
		model.addAttribute("accessMsg", "회원가입이 완료되었습니다.");
		model.addAttribute("accessUrl", request.getContextPath()+"/main/main");
		
		return "common/resultView"; //jsp(경로정보O)
	}
	
	/*=======================
	 *       회원 로그인
	 *======================= */
	//로그인 폼 호출
	@GetMapping("/member/login")
	public String formLogin() {
		return "memberLogin"; //tiles(경로정보X)
	}
	//전송된 데이터 처리
	@PostMapping("/member/login")
	public String submitLogin(@Valid MemberVO memberVO,BindingResult result,HttpSession session,HttpServletResponse response) {
		log.debug("<<회원로그인>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		//id와 passwd 필드만 유효성 체크
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			return formLogin();
		}
		
		//로그인 체크(id,비밀번호 일치 여부 체크)
		MemberVO member = null;
		try {
			member = memberService.selectCheckMember(memberVO.getId()); //id를 읽어와서 해당 아이디랑 매핑되는 정보가 있는지 없는지 체크
			boolean check = false;
			
			//id를 넣어서 membervo가 있다 -> 비밀번호 체크
			if(member!=null) {
				//비밀번호 일치 여부 체크
				check = member.isCheckedPassword(memberVO.getPasswd());
			}
			if(check) {//인증 성공시
				//=====자동 로그인 체크 시작=====//
				
				
				//=====자동 로그인 체크 끝=====//
				
				//인증 성공, 로그인 처리
				session.setAttribute("user", member); //user란 이름으로 통째로 객체 넣기 (저장할 데이터 多 경우)
				log.debug("<<인증 성공>>");
				log.debug("<<id>> : " + member.getId());
				log.debug("<<auth>> : " + member.getAuth());
				log.debug("<<au_id>> : " + member.getAu_id());
				
				if(member.getAuth()==9) { //관리자는 관리자 메인으로 이동
					return "redirect:/main/admin";
				}else {//일반 사용자는 사용자 메인으로 이동
					return "redirect:/main/main";
				}
			}
			//인증 실패
			throw new AuthCheckException();
			
		}catch(AuthCheckException e) {
			log.debug("<<인증 실패>>");
			//인증 실패로 로그인 폼 호출
			if(member!=null && member.getAuth()==1) { //정지회원
				result.reject("noAuthority");
			}else {
				result.reject("invalidIdOrPassword");
			}
			return formLogin();
		}
	}
	
	/*=======================
	 *       회원 로그아웃
	 *======================= */
	@RequestMapping("/member/logout")
	public String processLogout(HttpSession session,HttpServletResponse response) {
		//로그아웃
		session.invalidate();
		//======= 자동로그인 처리 시작(쿠키 삭제) =========//
		
		
		//======= 자동로그인 처리 끝(쿠키 삭제) =========//
		
		return "redirect:/main/main";
	}
	
	
	/*=======================
	 *       MyPage
	 *======================= */
	@RequestMapping("/member/myPage")
	public String process() {
		return "myPage";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
