package DB;

import java.util.HashMap;
import java.util.Map;

public class Login {
    private int id; //개발사가 발급한 고유 아이디
    private String username;  // 구매자 명
    private String passWord;

    public Login(){} //생성자

    public Login(int id, String username, String passWord){

        this.id = id;
        this.username = username;
        this.passWord = passWord;
    }

    public void setUserId(int id) {
        this.id = id;
    }

    public void setNickName(String username) {
        this.username = username;
    }

    //파이어베이스 RealTime-Database 데이터 추가.
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nickname", username);
        result.put("passWord", passWord);

        return result;
    }
}