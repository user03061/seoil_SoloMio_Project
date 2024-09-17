package DB;

import java.util.HashMap;
import java.util.Map;

public class items {

    public String itemId;
    public String itemName;
    public int Qty;
    public double itemPrice; // 변경된 부분

    public items(){} // 기본 생성자

    public items(String itemId, String itemName, int Qty, double itemPrice) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.Qty = Qty;
        this.itemPrice = itemPrice;
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
        return result;
    }
}
