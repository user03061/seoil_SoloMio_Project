package DB;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id; //개발사가 발급한 고유 아이디
    private String username;  // 구매자 명
    private String birth; //생년월일
    private String email; //구매자의 이메일 정보
    private int gender = -1; //1:남자 0:여자
    private String phone; //구매자의 전화번호 (페이앱 필수)
    private String addr;

    public User(){} //생성자

    public User(String id, String username, String birth, String email, int gender,
                String phone, String addr){

        this.id = id;
        this.username = username;
        this.birth = birth;
        this.email = email;
        this.gender = gender;
        this.phone = phone;
        this.addr = addr;

    }

    //파이어베이스 RealTime-Database 데이터 추가.
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nickname", username);
        result.put("birth", birth);
        result.put("email", email);
        result.put("gender", gender);
        result.put("phone", phone);
        result.put("addr", addr);

        return result;
    }
}