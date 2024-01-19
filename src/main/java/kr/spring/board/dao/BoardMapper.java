package kr.spring.board.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.board.vo.BoardFavVO;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;

@Mapper
public interface BoardMapper {
	//부모글
	public List<BoardVO> selectList(Map<String,Object> map);
	public int selectRowCount(Map<String,Object> map);
	public void insertBoard(BoardVO board);
	public BoardVO selectBoard(int board_num); //한 건의 레코드 반환
	@Update("UPDATE spboard SET hit=hit+1 WHERE board_num=#{board_num}")
	public void updateHit(int board_num);
	public void updateBoard(BoardVO board);
	@Delete("DELETE FROM spboard WHERE board_num=#{board_num}")
	public void deleteBoard(int board_num);
	@Update("UPDATE spboard SET filename='' WHERE board_num=#{board_num}")
	public void deleteFile(int board_num);
	
	
	//좋아요
	@Select("SELECT * FROM spboard_fav WHERE board_num=#{board_num} AND mem_num=#{mem_num}")
	public BoardFavVO selectFav(BoardFavVO fav); //BoardFavVO에 담기
	@Select("SELECT COUNT(*) FROM spboard_fav WHERE board_num=#{board_num}")
	public int selectFavCount(int board_num);
	@Insert("INSERT INTO spboard_fav (board_num,mem_num) VALUES (#{board_num},#{mem_num})")
	public void insertFav(BoardFavVO fav);
	@Delete("DELETE FROM spboard_fav WHERE board_num=#{board_num} AND mem_num=#{mem_num}")
	public void deleteFav(BoardFavVO boardFav);
	@Delete("DELETE FROM spboard_fav WHERE board_num=#{board_num}")
	public void deleteFavByBoardNum(int board_num); //부모글 삭제 시 좋아요가 존재하면 부모글 삭제 전 좋아요 삭제
	
	
	//댓글
	public List<BoardReplyVO> selectListReply(Map<String,Object> map);
	public int selectRowCountReply(Map<String,Object> map);
	public BoardReplyVO selectReply(int re_num); //한건의 데이터
	public void insertReply(BoardReplyVO boardReply);
	public void updateReply(BoardReplyVO boardReply);
	public void deleteReply(int re_num);
	@Delete("DELETE FROM spboard_reply WHERE board_num=#{board_num}")
	public void deleteReplyByBoardNum(int board_num); //부모글 삭제 시 댓글이 존재하면 부모글 삭제 전 댓글 삭제
	
	
	
}
