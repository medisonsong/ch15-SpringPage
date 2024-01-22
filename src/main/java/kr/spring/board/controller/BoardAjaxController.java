package kr.spring.board.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.FileUtil;
import kr.spring.util.PageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardAjaxController {
   @Autowired
   private BoardService boardService;
   
   /*=============================
    *    부모글 업로드 파일 삭제
    ============================*/
   @RequestMapping("/board/deleteFile")
   @ResponseBody
   public Map<String,String> processFile(int board_num, HttpSession session, HttpServletRequest request){
      Map<String,String> mapJson = new HashMap<String,String>();
      
      MemberVO user = (MemberVO)session.getAttribute("user");
      if(user==null) {
         mapJson.put("result", "logout");
      }else {
         //파일명 추출
         BoardVO vo = boardService.selectBoard(board_num);
         //삭제(DB)
         boardService.deleteFile(board_num);
         //업로드 경로에 있는 파일 삭제
         FileUtil.removeFile(request, vo.getFilename());
         
         mapJson.put("result", "success");
      }
      return mapJson;
   }
   
   
   /*=============================
    *    부모글 좋아요 읽기
    ============================*/
   @RequestMapping("/board/getFav")
   @ResponseBody
   public Map<String,Object> getFav(BoardFavVO fav, HttpSession session){
	   log.debug("<<게시판 좋아요 BoardFavVO>> : " + fav);
	   
	   Map<String,Object> mapJson = new HashMap<String,Object>();

	   //유저 구하기
	   MemberVO user = (MemberVO)session.getAttribute("user");
	   if(user==null) {
		   mapJson.put("status", "noFav");
	   }else {
		   //로그인된 회원번호 셋팅
		   fav.setMem_num(user.getMem_num());
		   
		   BoardFavVO boardFav = boardService.selectFav(fav); //user에게 있는지 없는지 알려줌
		   if(boardFav!=null) { //해당 user가 좋아요를 눌렀을 때
			   mapJson.put("status", "yesFav");
		   }else {
			   mapJson.put("status", "noFav");
		   }
	   }
	   //좋아요 수
	   mapJson.put("count", boardService.selectFavCount(fav.getBoard_num()));
	   
	   return mapJson;
   }
   
   
   /*=============================
    *    부모글 좋아요 등록/ 삭제
    * ============================*/
   @RequestMapping("/board/writeFav")
   @ResponseBody
   public Map<String,Object> writeFav(BoardFavVO fav, HttpSession session){
	   log.debug("<<부모글 좋아요 등록/삭제 BoardFavVO>> : " + fav);
	   
	   Map<String,Object> mapJson = new HashMap<String,Object>();
	   
	   //로그인 상태 확인 후 동작 처리
	   MemberVO user = (MemberVO)session.getAttribute("user");
	   if(user==null) {
		   mapJson.put("result", "logout");
	   }else {
		   //로그인된 회원번호 셋팅
		   fav.setMem_num(user.getMem_num());
		   
		   //이전에 좋아요를 등록했는지 여부 확인 (토글 형태)
		   BoardFavVO boardFavVO = boardService.selectFav(fav);
		   if(boardFavVO!=null) { //좋아요를 이미 등록
			   boardService.deleteFav(fav);
			   mapJson.put("status", "noFav");
		   }else {//좋아요 미등록
			   boardService.insertFav(fav);
			   mapJson.put("status", "yesFav");
		   }
		   mapJson.put("result", "success");
		   mapJson.put("count", boardService.selectFavCount(fav.getBoard_num()));
	   }
	   return mapJson;
   }
   
   
   /*=============================
    *    댓글 등록
    ============================*/
   @RequestMapping("/board/writeReply")
   @ResponseBody
   public Map<String,String> writeReply(BoardReplyVO boardReplyVO,
		   								HttpSession session,
		   								HttpServletRequest request){
	   log.debug("<<댓글 등록 BoardReplyVO>> : " + boardReplyVO);
	   
	   Map<String,String> mapJson = new HashMap<String,String>();
	   
	   MemberVO user = (MemberVO)session.getAttribute("user");
	   if(user==null) {
		   //로그인 안됨
		   mapJson.put("result", "logout");
	   }else {
		   //회원번호 등록
		   boardReplyVO.setMem_num(user.getMem_num());
		   //ip 등록
		   boardReplyVO.setRe_ip(request.getRemoteAddr());
		   //댓글 등록
		   boardService.insertReply(boardReplyVO);
		   mapJson.put("result", "success");
	   }
	   return mapJson;
   }
   
   
   /*=============================
    *    댓글 목록
    ============================*/   
   @RequestMapping("/board/listReply")
   @ResponseBody
   public Map<String,Object> getList(@RequestParam(value="pageNum", defaultValue="1") int currentPage,
		   							 @RequestParam(value="rowCount", defaultValue="10") int rowCount,
		   							 @RequestParam int board_num, HttpSession session){
	   log.debug("<<댓글 목록 board_num>> : " + board_num);
	   
	   Map<String,Object> map = new HashMap<String,Object>();
	   map.put("board_num", board_num);
	   
	   //전체 레코드 수
	   int count = boardService.selectRowCountReply(map);
	   //페이지 처리 (더보기를 누를때 페이지가 보이는 형태 -> pageUtil은 start, end를 구하기 위해 연산하는 형태로만 쓰임)
	   PageUtil page = new PageUtil(currentPage,count,rowCount); 
	   
	   List<BoardReplyVO> list = null;
	   if(count > 0) { // count>0 일 경우에만 연산 작업
		   map.put("start", page.getStartRow());
		   map.put("end", page.getEndRow());
		   list = boardService.selectListReply(map);
	   }else {
		   list = Collections.emptyList();
	   }
	   
	   Map<String,Object> mapJson = new HashMap<String,Object>();
	   mapJson.put("count", count);
	   mapJson.put("list", list);
	   
	   //로그인한 회원정보 셋팅
	   MemberVO user = (MemberVO)session.getAttribute("user");
	   if(user!=null) {
		   mapJson.put("user_num", user.getMem_num());
	   }
	   return mapJson;
   }
   
   
   
   
   
   
   
   
   
   
}

