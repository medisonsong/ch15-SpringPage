package kr.spring.talk.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import kr.spring.talk.vo.TalkMemberVO;
import kr.spring.talk.vo.TalkRoomVO;
import kr.spring.talk.vo.TalkVO;

@Mapper
public interface TalkMapper {
	//채팅방 목록
	public List<TalkRoomVO> selectTalkRoomList(Map<String,Object> map);
	public int selectRowCount(Map<String,Object> map);
	//채팅방 번호 생성 (PK)
	@Select("SELECT sptalkroom_seq.nextval FROM dual")
	public int selectTalkRoomNum();
	//채팅방 생성
	@Insert("INSERT INTO sptalkroom (talkroom_num,basic_name) VALUES (#{talkroom_num},#{basic_name})")
	public void insertTalkRoom(TalkRoomVO talkRoomVO);
	//채팅방 멤버 등록
	@Insert("INSERT INTO sptalk_member (talkroom_num,room_name,mem_num) VALUES (#{talkroom_num},#{room_name},#{mem_num})")
	public void insertTalkRoomMember(@Param(value="talkroom_num") int talkroom_num,
									 @Param(value="room_name") String room_name,
									 @Param(value="mem_num") int mem_num);
	//채팅 멤버 읽기
	public List<TalkMemberVO> selectTalkMember(int talkroom_num);
	
	//채팅 메시지 번호 생성
	@Select("SELECT sptalk_seq.nextval FROM dual")
	public int selectTalkNum();
	//채팅 메시지 등록
	@Insert("INSERT INTO sptalk (talk_num,talkroom_num,mem_num,message) VALUES (#{talk_num},#{talkroom_num},#{mem_num},#{message})")
	public void insertTalk(TalkVO talkVO);
	//읽지 않은 채팅 기록 저장
	public void insertTalkRead(@Param(value="talkroom_num") int talkroom_num,
							   @Param(value="talk_num") int talk_num,
							   @Param(value="mem_num") int mem_num);
	//채팅 메시지 읽기
	public List<TalkVO> selectTalkDetail(Map<String,Integer> map);
	//읽은 채팅 기록 삭제
	public void deleteTalkRead(Map<String,Integer> map);
}
