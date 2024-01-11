package kr.spring.member.dao;

import org.apache.ibatis.annotations.Mapper;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	//회원관리 - 사용자
	public int selectMem_num(); //pk 구하기
	
	public void insertMember(MemberVO member);
	public void insertMember_detail(MemberVO member); 
	
	public MemberVO selectCheckMember(String id);
	public MemberVO selectMember(int mem_num);//한건의 레코드 반환
	
	public void updateMember(MemberVO member);
	public void updateMember_detail(MemberVO member);
	public void updatePassword(MemberVO member);
	
	public void deleteMember(int mem_num);
	public void deleteMember_detail(int mem_num);
	
	//회원관리 - 관리자
	
}