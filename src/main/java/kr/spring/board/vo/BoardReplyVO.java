package kr.spring.board.vo;

import kr.spring.util.DurationFromNow;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardReplyVO {
	private int re_num;
	private String re_content;
	private String re_date;
	private String re_mdate; //연월일시분초가 아니라 하루전 이틀전 이렇게 변환 작업할거라서 string 처리
	private String re_ip;
	private int board_num;
	private int mem_num;
	
	private String id;
	private String nick_name;
	
	//작성
	public void setRe_date(String re_date) {
		this.re_date = DurationFromNow.getTimeDiffLabel(re_date);
	}
	
	//수정
	public void setRe_mdate(String re_mdate) {
		this.re_mdate = DurationFromNow.getTimeDiffLabel(re_mdate);
	}
	
}