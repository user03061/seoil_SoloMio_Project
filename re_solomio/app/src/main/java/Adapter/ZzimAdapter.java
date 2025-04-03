package Adapter;

import com.bumptech.glide.Glide;
import com.example.re_solomio.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ZzimAdapter extends BaseAdapter {

    private Context context;
    private List<ZzimItem> zzimItems;

    public ZzimAdapter(Context context, List<ZzimItem> zzimItems) {
        this.context = context;
        this.zzimItems = zzimItems;
    }

    @Override
    public int getCount() {
        return zzimItems.size();
    }

    @Override
    public Object getItem(int position) {
        return zzimItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.zzim_card_item, parent, false);
        }

        ImageView zzimImage = convertView.findViewById(R.id.zzim_image);
        TextView zzimText = convertView.findViewById(R.id.zzim_text);

        ZzimItem item = zzimItems.get(position);

        Glide.with(context)
                .load(item.getPhotoUrl())
                .into(zzimImage);

        zzimText.setText(item.getTitle()); // 데이터 설정

        return convertView;
    }

}
