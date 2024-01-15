package kr.spring.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MemberAjaxController {
   //주입받기
   @Autowired
   private MemberService memberService;
   
    /*=======================
	 *     아이디 중복 체크
	 *======================= */
   @RequestMapping("/member/confirmId")
   @ResponseBody //ajax를 자동으로 만들어줌
   public Map<String,String> process(@RequestParam String id){
      log.debug("<<아이디 중복 체크>> : " + id);
      
      Map<String,String> mapAjax = new HashMap<String,String>();
      
      MemberVO member = memberService.selectCheckMember(id); //중복 체크
      
      if(member!=null) {
         //아이디 중복
         mapAjax.put("result", "idDuplicated");
      }else {
         //아이디 미중복
         if(!Pattern.matches("^[A-Za-z0-9]{4,12}$", id)) {
            //패턴 불일치
            mapAjax.put("result", "notMatchPattern");
         }else {
            //패턴 일치하면서 아이디 미중복
            mapAjax.put("result", "idNotFound");
         }
      }
      
      return mapAjax;
   }
   
    /*=======================
	 *     프로필 사진 업로드
	 *======================= */
   @RequestMapping("/member/updateMyPhoto")
   @ResponseBody
   public Map<String,String> processProfile(MemberVO memberVO,HttpSession session){
	   Map<String,String> mapAjax = new HashMap<String,String>();
	   
	   //로그인 유무 체크 후 처리
	   MemberVO user = (MemberVO)session.getAttribute("user");
	   if(user==null) {
		   mapAjax.put("result", "logout");
	   }else {
		   memberVO.setMem_num(user.getMem_num());
		   memberService.updateProfile(memberVO);
		   
		   mapAjax.put("result", "success");
	   }
	   return mapAjax;
   }
   
}