package kr.spring.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.member.dao.MemberMapper;
import kr.spring.member.vo.MemberVO;

@Service
@Transactional
public class MemberServiceImpl implements MemberService{
	@Autowired
	private MemberMapper memberMapper;

	@Override
	public void insertMember(MemberVO member) {
		// TODO Auto-generated method stub
	}

	@Override
	public MemberVO selectCheckMember(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MemberVO selectMember(int mem_num) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMember(MemberVO member) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePassword(MemberVO member) {
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteMember(int mem_num) {
		// TODO Auto-generated method stub
	}
	
}
