package kr.spring.talk.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.talk.service.TalkService;
import kr.spring.talk.vo.TalkMemberVO;
import kr.spring.talk.vo.TalkRoomVO;
import kr.spring.talk.vo.TalkVO;
import kr.spring.util.PageUtil;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class TalkController {
	@Autowired
	private TalkService talkService;
	@Autowired
	private MemberService memberService;
	
	/* ============================
	 * 채팅방 목록
	 * ============================*/
	@RequestMapping("/talk/talkList")
	public String chatList(@RequestParam(value="pageNum",defaultValue="1") int currentPage,
						   String keyword, HttpSession session, Model model) {
		
		MemberVO user = (MemberVO)session.getAttribute("user");
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyword", keyword);
		map.put("mem_num", user.getMem_num()); //사용자 본인 채팅목록만 빼내기 위해
		
		int count = talkService.selectRowCount(map);
		
		//keyfield가 없기 때문에 그냥 null처리하면 됨 (30개10페이지)
		PageUtil page = new PageUtil(null,keyword,currentPage,count,30,10,"talkList");
		
		List<TalkRoomVO> list = null;
		if(count>0) {
			map.put("start", page.getStartRow());
			map.put("end", page.getEndRow());
			list = talkService.selectTalkRoomList(map);
		}
		model.addAttribute("count", count);
		model.addAttribute("list", list);
		model.addAttribute("page", page.getPage());
		
		return "talkList";
	}
	
	
	
	/* ============================
	 * 채팅방 생성
	 * ============================*/
	//채팅방 생성폼 호출
	@GetMapping("/talk/talkRoomWrite")
	public String talkRoomWrite() {
		return "talkRoomWrite"; //tiles설정
	}
	//전송된 데이터 처리
	@PostMapping("/talk/talkRoomWrite")
	public String talkRoomSubmit(TalkRoomVO vo, HttpSession session) {
		log.debug("<<채팅방 생성 TalkRoomVO>> : " + vo);
		
		MemberVO user = (MemberVO)session.getAttribute("user");
		
		//채팅멤버 초대 시 문구설정
		vo.setTalkVO(new TalkVO());
		vo.getTalkVO().setMem_num(user.getMem_num()); //session에서 가져온 멤버 정보 넣기
		vo.getTalkVO().setMessage(user.getId()+"님이 "
								+findMemberId(vo,user)+"님을 초대했습니다.@{member}@");
		talkService.insertTalkRoom(vo);

		return "redirect:/talk/talkList";
	}
	
	
	
	//채팅 회원 검색
	@RequestMapping("/talk/memberSearchAjax")
	@ResponseBody
	public Map<String,Object> memberSearchAjax(@RequestParam String id,
											   HttpSession session){
		Map<String,Object> mapJson = new HashMap<String,Object>();
		
		//로그인이 되어있어야 하기 때문에 session에서 정보 꺼내오기
		MemberVO user = (MemberVO)session.getAttribute("user");
		if(user==null) {//로그인이 되지 않은 경우
			mapJson.put("result", "logout");
		}else {//로그인이 된 경우
			List<MemberVO> member = memberService.selectSearchMember(id); //id를 목록으로 읽어옴
			mapJson.put("result", "success");
			mapJson.put("member", member);
		}
		return mapJson;
	}
	
	
	
	
	
	
	/* ============================
	 * 채팅 메시지 처리
	 * ============================*/
	//채팅 메시지 페이지 호출
	@RequestMapping("/talk/talkDetail")
	public String talkDetail(@RequestParam int talkroom_num,
							 Model model, HttpSession session) {
		String chatMember = ""; //빈문자열에 붙이기 위해 변수 생성+초기화
		String room_name = "";
		
		MemberVO user = (MemberVO)session.getAttribute("user");
		
		List<TalkMemberVO> list = talkService.selectTalkMember(talkroom_num);
		for(int i=0; i<list.size(); i++) {
			TalkMemberVO vo = list.get(i);
			//로그인한 회원의 채팅방 이름 셋팅
			if(user.getMem_num() == vo.getMem_num()) {
				room_name = vo.getRoom_name();
			}
			//채팅 멤버 저장
			if(i>0) chatMember += ", ";
			chatMember += list.get(i).getId();
		}
		
		//채팅 멤버 id
		model.addAttribute("chatMember", chatMember);
		//채팅 멤버 수
		model.addAttribute("chatCount", list.size());
		//로그인한 회원의 채팅방 이름
		model.addAttribute("room_name", room_name);
		
		return "talkDetail";
	}
	
	//채팅 메시지 읽기 (ajax통신)
	@RequestMapping("/talk/talkDetailAjax")
	@ResponseBody
	public Map<String,Object> talkDetailAjax(@RequestParam int talkroom_num,
											 HttpSession session){
		
		Map<String,Object> mapAjax = new HashMap<String,Object>();
		
		MemberVO user = (MemberVO)session.getAttribute("user");
		if(user==null) { //로그인이 되지 않은 경우
			mapAjax.put("result", "logout"); 
		}else {//로그인된 경우
			Map<String,Integer> map = new HashMap<String,Integer>(); //데이터 담아서 보낼 준비
			map.put("talkroom_num", talkroom_num);
			map.put("mem_num", user.getMem_num());
			List<TalkVO> list = talkService.selectTalkDetail(map);
			
			mapAjax.put("result", "success");
			mapAjax.put("list", list);
			mapAjax.put("user_num", user.getMem_num());
		}
		return mapAjax;
	}
	
	
	
	
	/* ============================
	 * 초대한 회원의 id구하기
	 * ============================*/	
	//내부적으로만 호출되기 때문에 private으로 호출
	private String findMemberId(TalkRoomVO vo, MemberVO user) {
		String member_id = "";
		int[] members = vo.getMembers();
		for(int i=0;i<members.length;i++) {
			String temp_id = memberService.selectMember(members[i]).getId();
			//초대한 사람의 아이디는 제외
			if(!user.getId().equals(temp_id)) {
				member_id += temp_id;
				if(i < members.length-1) member_id += ", ";
			}
		}
		return member_id;
	}
	
	
	
	
	
	
	
	
}
