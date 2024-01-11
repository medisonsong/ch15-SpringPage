package kr.spring.member.vo;

import java.io.IOException;
import java.sql.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
//ToString이 재정의될 때 photo의 값을 반환하지 않음 
@ToString(exclude = {"photo"})
public class MemberVO {
	private int mem_num;
	@Pattern(regexp="^[A-Za-z0-9]{4,12}$") //[리스트]{min,max값}
	private String id;
	private String nick_name;
	private int auth;
	private String auto; // 자동로그인 시 사용하기 위해 (추가)
	private String au_id;
	@NotBlank
	private String name;
	@Pattern(regexp="^[A-Za-z0-9]{4,12}$")
	private String passwd;
	@NotBlank
	private String phone;
	@Email
	@NotBlank
	private String email;
	@Size(min=5,max=5)
	private String zipcode;
	@NotBlank
	private String address1;
	@NotEmpty
	private String address2;
	private byte[] photo;
	private String photo_name;
	private Date reg_date;
	private Date modify_date;
	private String now_passwd; //비밀번호 변경 시 사용하기 위해 (추가)
	
	/*=====================
	 * 비밀번호 일치 여부 체크 메서드
	  =====================*/
	public boolean isCheckedPassword(String userPasswd) {
		if(auth > 1 && passwd.equals(userPasswd)) { //0,1(탈퇴,정지)는 진입 자체를 막기위해
			return true;
		}
		return false;
	}
	
	/*=====================
	 * 이미지 BLOB 처리 메서드
	  =====================*/
	//(주의)폼에서 파일 업로드 파라미터네임은 반드시 upload로 지정해야 함
	public void setUpload(MultipartFile upload) throws IOException {
		//MultipartFile -> byte[]
		setPhoto(upload.getBytes());
		//파일명 지정
		setPhoto_name(upload.getOriginalFilename());
	}
	
}
