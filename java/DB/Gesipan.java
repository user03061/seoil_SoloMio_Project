package DB;

import java.util.HashMap;
import java.util.Map;

public class Gesipan {

    public String id; // 사용자 아이디 Db에 구별을 위함
    public String nickName; // 사용자 닉네임
    public String writeText; // 게시글 내용
    public String title; // 게시글 제목
    public long writeTime; // 게시글이 올라온 날짜
    public int Views = 0; // 조회 수
    public int pageNum = 1;

    public int starCount = 0; // 추천 수
    public Map<String, Boolean> stars = new HashMap<>(); // 추천한 사람 확인

    public String category; // 게시글 카테고리 추가
    public String photoUrl; // 사진 URL 추가

    // 기본 생성자
    public Gesipan() {
    }

    // 매개변수가 있는 생성자
    public Gesipan(String id, String nickName, String writeText,
                   String title, long writeTime, int Views, int pageNum, String category, String photoUrl) {
        this.id = id;
        this.nickName = nickName;
        this.writeText = writeText;
        this.title = title;
        this.writeTime = writeTime;
        this.Views = Views;
        this.pageNum = pageNum;
        this.category = category;
        this.photoUrl = photoUrl;
    }

    // Firestore RealTime-Database 데이터 추가.
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nickName", nickName);
        result.put("writeText", writeText);
        result.put("title", title);
        result.put("writeTime", writeTime);
        result.put("starCount", starCount);
        result.put("pageNum", pageNum);
        result.put("stars", stars);
        result.put("category", category); // 카테고리 추가
        result.put("photoUrl", photoUrl); //사진 url

        return result;
    }
}
