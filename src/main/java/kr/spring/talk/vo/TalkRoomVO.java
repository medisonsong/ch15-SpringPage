package kr.spring.talk.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TalkRoomVO {
	private int talkroom_num;
	private String basic_name;
	private String talkroom_date;
	
	//추가 vo
	private int[] members; //채팅멤버 배열형태로 읽어오기 (멤버 多라서 s)
	private int mem_num;
	private int room_cnt; //읽지않은 메시지 수
	
	//다른 vo 읽어오기
	private TalkVO talkVO;
	private TalkMemberVO talkMemberVO;
	
}
