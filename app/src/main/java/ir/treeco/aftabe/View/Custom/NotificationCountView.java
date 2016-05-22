package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.UiUtil;

/**
 * Created by al on 4/27/16.
 */
public class NotificationCountView extends FrameLayout {


    private BadgeView mBadgeView;
    private ImageView mImageView;

    public NotificationCountView(Context context, int drawable, SizeConverter converter) {
        super(context);


        ImageManager imageManager = new ImageManager(context);


        mImageView = new ImageView(context);
        mImageView.setImageBitmap(imageManager.loadImageFromResource(drawable, converter.mWidth, converter.mHeight));

        addView(mImageView);


        mBadgeView = new BadgeView(context, mImageView);


        mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, SizeManager.getScreenWidth() * 0.04f);
        mBadgeView.setTypeface(FontsHolder.getNumeralSansMedium(context));

//        FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, -converter.mHeight / 2, 0, 0);
//        layoutParams.gravity = Gravity.CENTER;
//        mBadgeView.setLayoutParams(layoutParams);


    }

    public void setCount(int count) {
        mBadgeView.setText("" + count + "");
        if (count == 0)
            mBadgeView.hide();
        else
            mBadgeView.show();

    }

}
