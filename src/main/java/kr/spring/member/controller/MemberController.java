package kr.spring.member.controller;

import java.util.UUID;

import javax.servlet.http.Cookie;
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
import org.springframework.web.bind.annotation.RequestParam;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.AuthCheckException;
import kr.spring.util.FileUtil;
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
				boolean autoLogin = memberVO.getAuto() != null && memberVO.getAuto().equals("on");
				if(autoLogin) {
					//자동로그인 체크를 한 경우 (true)
					String au_id = member.getAu_id();
					if(au_id==null) {
						//자동로그인 체크 식별값 생성
						au_id = UUID.randomUUID().toString(); //식별값 생성
						log.debug("<<au_id>> : " + au_id);
						member.setAu_id(au_id);
						memberService.updateAu_id(member.getAu_id(), member.getMem_num());
					}
					
					//쿠키를 이용해서 값을 넣고 식별을 하는 때문에 쿠키 저장
					Cookie auto_cookie = new Cookie("au-log", au_id);
					auto_cookie.setMaxAge(60*60*24*7); // 쿠키 저장기간 설정 (1주일)
					auto_cookie.setPath("/"); //하위경로 없이 root밑에 만들어지기 때문에 root밑으로 들어오게 된 경우에만 가능하도록 함
					
					response.addCookie(auto_cookie);
				}
				//=====자동 로그인 체크 끝=====//
				
				//인증 성공, 로그인 처리
				session.setAttribute("user", member); //user란 이름으로 통째로 객체 넣기 (저장할 데이터 多 경우)
				log.debug("<<인증 성공>>");
				log.debug("<<id>> : " + member.getId());
				log.debug("<<mem_num>> : " + member.getMem_num());
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
		//클라이언트 쿠키 처리
		Cookie auto_cookie = new Cookie("au-log","");
		 //덮어씌우면서 기존 쿠키 삭제
		auto_cookie.setMaxAge(0);
		auto_cookie.setPath("/");
		
		response.addCookie(auto_cookie);
		//======= 자동로그인 처리 끝(쿠키 삭제) =========//
		
		return "redirect:/main/main";
	}
	
	
	/*=======================
	 *       MyPage
	 *======================= */
	@RequestMapping("/member/myPage")
	public String process(HttpSession session,Model model) {
		MemberVO user = (MemberVO)session.getAttribute("user");
		log.debug("<<mem_num>> : " + user.getMem_num());
		//회원 정보
		MemberVO member = memberService.selectMember(user.getMem_num()); //한건의 레코드 읽어오기
		
		log.debug("<<회원 상세 정보>> : " + member); //member -> toString 동작 (내용 출력)
		model.addAttribute("member", member);
		
		return "myPage";
	}
	
	
	/*=======================
	 *      프로필 사진 출력
	 *======================= */
	//1. 프로필 사진 출력(로그인 전용/ 마이페이지, 헤더)
	@RequestMapping("/member/photoView")
	public String getProfile(HttpSession session, HttpServletRequest request, Model model) {
		MemberVO user = (MemberVO)session.getAttribute("user");
		log.debug("<<프로필 사진 읽기>> : " + user);
		if(user==null) { //로그인이 되지 않은 경우
			getBasicProfileImage(request, model);
		}else { //로그인된 경우
			MemberVO memberVO = memberService.selectMember(user.getMem_num());
			viewProfile(memberVO, request, model);
		}
		//빈의 이름이 imageView인 ImageView 객체를 호출
		return "imageView";
	}
	
	
	//2. 프로필 사진 출력(회원번호 지정/ 게시판)
	@RequestMapping("/member/viewProfile")
	public String getProfileByMem_num(@RequestParam int mem_num,HttpServletRequest request, Model model) {
		MemberVO memberVO = memberService.selectMember(mem_num);
		
		viewProfile(memberVO, request, model); //3번
		
		return "imageView";
	}
	
	
	//3. 프로필 사진 처리를 위한 공통 코드 (1,2에 사용됨)
	public void viewProfile(MemberVO memberVO, HttpServletRequest request, Model model) {
		if(memberVO==null || memberVO.getPhoto_name()==null) {
			//업로드한 프로필 사진 정보가 없어서 기본 이미지 표시
			getBasicProfileImage(request, model);
		}else {//업로드한 이미지 읽기
			model.addAttribute("imageFile", memberVO.getPhoto());
			model.addAttribute("filename", memberVO.getPhoto_name());
		}
	}
	
	
	//4. 기본 이미지 읽기 (1,2에 사용됨)
	public void getBasicProfileImage(HttpServletRequest request, Model model) {
		//바이트 배열로 변환해서 읽어오기
		byte[] readbyte = FileUtil.getBytes(request.getServletContext().getRealPath("/image_bundle/face.png"));
		model.addAttribute("imageFile", readbyte);
		model.addAttribute("filename", "face.png");
	}
}
