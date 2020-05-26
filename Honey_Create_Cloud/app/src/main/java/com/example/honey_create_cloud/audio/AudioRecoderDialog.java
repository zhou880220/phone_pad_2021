package com.example.honey_create_cloud.audio;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.honey_create_cloud.R;


/**
 * Created  on 2017/9/19.
 */

public class AudioRecoderDialog extends BasePopupWindow {

    private ImageView imageView;
    private TextView textView;

    public AudioRecoderDialog(Context context) {
        super(context);
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_recoder_dialog, null);
        imageView = contentView.findViewById(android.R.id.progress);
        textView = contentView.findViewById(android.R.id.text1);
        setContentView(contentView);
    }

    public void setLevel(int level) {

        Drawable drawable = imageView.getDrawable();
        drawable.setLevel(3000 + 6000 * level / 100);

    }

    public void setTime(long time) {
        textView.setText(ProgressTextUtils.getProgressText(time));
    }
}
