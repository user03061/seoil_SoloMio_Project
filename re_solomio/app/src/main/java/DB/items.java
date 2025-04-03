package DB;

import java.util.HashMap;
import java.util.Map;

public class items {

    public String itemId;
    public String itemName;
    public int Qty;
    public double itemPrice;
    public double itemCount = 1;

    public items(){} // 기본 생성자

    public items(String itemId, String itemName, int Qty, double itemPrice,double itemCount) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.Qty = Qty;
        this.itemPrice = itemPrice;
        this.itemCount = itemCount;
    }

    public void setItemCount(int count) { //장바구니 수량 설정 적용하려고 선언함.
        this.itemCount = count;
    }


    public String getItemName() {
        return itemName;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("itemId", itemId);
        result.put("itemName", itemName);
        result.put("Qty", Qty);
        result.put("itemPrice", itemPrice);
        result.put("itemCount", itemCount);
        return result;
    }
}
