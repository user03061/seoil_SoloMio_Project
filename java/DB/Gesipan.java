package DB;

import java.util.HashMap;
import java.util.Map;

public class Gesipan {
    public String id; // 사용자 아이디 (DB에 구별을 위함)
    public String nickName; // 사용자 닉네임
    public String writeText; // 게시글 내용
    public String title; // 게시글 제목
    public long writeTime; // 게시글이 올라온 날짜
    public int Views; // 조회 수
    public int likeCount; // 좋아요 수
    public int pageNum; // 페이지 번호
    public int starCount; // 사용자 제한 좋아요 수
    public boolean zzim; // 찜 설정
    public Map<String, Boolean> stars; // 추천한 사람 확인 (사용자 id가 key)
    public  String category; // 게시글 카테고리
    public String photoUrl; // 사진 URL


    public Gesipan() {}

    public Gesipan(String id, String nickName, String writeText,
                   String title, long writeTime, int views, int likeCount,
                   int pageNum, String category, String photoUrl, boolean zzim) {
        this.id = id;
        this.nickName = nickName;
        this.writeText = writeText;
        this.title = title;
        this.writeTime = writeTime;
        this.Views = views;
        this.likeCount = likeCount;
        this.pageNum = pageNum;
        this.category = category;
        this.photoUrl = photoUrl;
        this.zzim = zzim;
        this.stars = new HashMap<>();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("nickName", nickName);
        result.put("writeText", writeText);
        result.put("title", title);
        result.put("writeTime", writeTime);
        result.put("like", likeCount);
        result.put("views", Views);
        result.put("pageNum", pageNum);
        result.put("stars", stars);
        result.put("category", category);
        result.put("photoUrl", photoUrl);
        result.put("zzim", zzim);
        return result;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isZzim() {
        return zzim;
    }

    public void setZzim(boolean zzim) {
        this.zzim = zzim;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public int getPageNum() {
        return pageNum;
    }

}
