package ir.treeco.aftabe.View.Custom;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import ir.treeco.aftabe.API.Socket.Objects.UserAction.GameActionResult;
import ir.treeco.aftabe.MainApplication;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.R;
import ir.treeco.aftabe.Util.FontsHolder;
import ir.treeco.aftabe.Util.ImageManager;
import ir.treeco.aftabe.Util.LengthManager;
import ir.treeco.aftabe.Util.SizeConverter;
import ir.treeco.aftabe.Util.SizeManager;
import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.Util.UiUtil;
import ir.treeco.aftabe.View.Activity.MainActivity;
import ir.treeco.aftabe.View.Dialog.LeaderboardDialog;
import ir.treeco.aftabe.View.Dialog.RegistrationDialog;
import ir.treeco.aftabe.View.Dialog.UserViewDialog;

/**
 * TODO: document your custom view class.
 */
public class UserLevelView extends LinearLayout implements View.OnClickListener {
    private static final String TAG = "UserLevelView";
    private int mUserLevel;
    private int mUserExp;
    private float mDimension;
    private boolean isOnlineTop = false;

    private static final int TEXT_ALIGN_LEFT = 0;
    private static final int TEXT_ALIGN_BELOW = 1;
    private static final int TEXT_ALIGN_CENTER = 2;


    SizeConverter imageConverter;
    private boolean mClick = true;

    private boolean mFirstState = true;
    public User mUser;
    private int mTextAlign;
    private ImageView expView;
    private ImageView stateView;
    private ImageView baseView;
    private ImageView coverView;
    private MagicTextView mUserNameTextView;
    private MagicTextView mLevelTextView;
    private LengthManager lengthManager;
    private ImageManager imageManager;


    public UserLevelView(Context context) {

        super(context);
        init(context, null, 0);
        mUserLevelMarkView(context);
    }

    public UserLevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
        mUserLevelMarkView(context);
    }


    private void mUserLevelMarkView(Context context) {
        lengthManager = ((MainApplication) context.getApplicationContext()).getLengthManager();
        imageManager = ((MainApplication) context.getApplicationContext()).getImageManager();

        imageConverter = SizeConverter.SizeConvertorFromWidth(lengthManager.getScreenWidth() * mDimension, 824, 837);

        Log.d(TAG, "width and height is " + imageConverter.mWidth + " " + imageConverter.mHeight);
        FrameLayout imagesContainer = new FrameLayout(context);


        baseView = new ImageView(context);
        baseView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.base),
                imageConverter.mWidth, imageConverter.mHeight, ImageManager.ScalingLogic.FIT));


        coverView = new ImageView(context);
        coverView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.cover), imageConverter.mWidth, imageConverter.mHeight, ImageManager.ScalingLogic.FIT));


        mUserNameTextView = new MagicTextView(context);
        mUserNameTextView.setText("اسمته");

        UiUtil.setTextViewSize(mUserNameTextView, (int) (SizeManager.getScreenHeight() * mDimension), (isOnlineTop) ? 0.14f : 0.15f);


        mUserNameTextView.setTextColor(Color.WHITE);

        setShadowLayer(mUserNameTextView);
        mUserNameTextView.setTypeface(FontsHolder.getLond(getContext()));


        LayoutParams textLP = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        textLP.gravity = Gravity.CENTER;


        mLevelTextView = new MagicTextView(context);
        UiUtil.setTextViewSize(mLevelTextView, (int) (SizeManager.getScreenHeight() * mDimension), 0.22f);
        mLevelTextView.setTypeface(FontsHolder.getNumeralSansBold(context));
        mLevelTextView.setTextColor(Color.WHITE);
        mLevelTextView.setGravity(Gravity.CENTER);

//        mLevelTextView.setStroke(strokeSize, Color.BLACK);

        FrameLayout.LayoutParams levelTextViewLP = new FrameLayout.LayoutParams((int) (lengthManager.getScreenWidth() * (mDimension)),
                (int) (lengthManager.getScreenWidth() * (mDimension)));
//        levelTextViewLP.gravity = Gravity.CENTER_VERTICAL;
//        levelTextViewLP.topMargin = -(int) (SizeManager.getScreenHeight() * mDimension * 0.005);

        mLevelTextView.setLayoutParams(levelTextViewLP);
        setShadowLayer(mLevelTextView);


        imagesContainer.setLayoutParams(textLP);

        expView = new ImageView(context);

        stateView = new ImageView(context);

        imagesContainer.addView(baseView);
        imagesContainer.addView(expView);
        imagesContainer.addView(stateView);
        imagesContainer.addView(coverView);
        imagesContainer.addView(mLevelTextView);

        int orientation = VERTICAL;
        if (mTextAlign == TEXT_ALIGN_LEFT)
            orientation = HORIZONTAL;

        setOrientation(orientation);

        if (mTextAlign != TEXT_ALIGN_CENTER) {

            if (orientation == VERTICAL) {
                textLP.topMargin = (int) ((int) (SizeManager.getScreenHeight() * 0.02) * mDimension);
                mUserNameTextView.setLayoutParams(textLP);

                addView(imagesContainer);
                addView(mUserNameTextView);
            } else {
                textLP.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                mUserNameTextView.setLayoutParams(textLP);
                setUserNameTextSize(1.15f);
                addView(mUserNameTextView);
                addView(imagesContainer);
            }
        } else { //Text Align Center
            imagesContainer.addView(mUserNameTextView);

            mUserNameTextView.setText(mUserNameTextView.getText().subSequence(0, 2));
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mUserNameTextView.getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            mUserNameTextView.setGravity(Gravity.CENTER);

            addView(imagesContainer);
        }

        FontsHolder.setFont(mUserNameTextView, FontsHolder.LOND);

        setOnClickListener(this);


    }

    public void setLevelTextSize() {

        UiUtil.setTextViewSize(mLevelTextView, (int) (SizeManager.getScreenWidth() * mDimension), 0.42f);

    }

    public void setUserNameTextSize(float size) {

        UiUtil.setTextViewSize(mUserNameTextView, (int) (SizeManager.getScreenWidth() * mDimension), 0.275f * size);
    }

    public void setUserNameTextSizeHeightlyScaled(float size) {

        UiUtil.setTextViewSize(mUserNameTextView, (int) (SizeManager.getScreenHeight() * mDimension), 0.275f * size);
    }

    public void setShadowLayer(MagicTextView mLevelTextView) {

        float dpi = getContext().getResources().getDisplayMetrics().density;
        if (SizeManager.getScreenWidth() < 800) {
            int dropShadowColor = Color.parseColor("#393939");

            float size = (float) (0.5 * mDimension / 0.4);
            mLevelTextView.addOuterShadow(0.3f, size, size, dropShadowColor);
            mLevelTextView.addInnerShadow(0.3f, size, size, dropShadowColor);

            return;
        }

//        float shadowSize = (mDimension * 6 / (0.7f));
//        Log.d("LevelUserVIew", shadowSize + " is the shadow size");
        float shadowSize = (mDimension / 0.7f) * 6;
        int dropShadowColor = Color.parseColor("#393939");
        mLevelTextView.addOuterShadow(1, shadowSize, shadowSize, dropShadowColor);
        mLevelTextView.addInnerShadow(1, shadowSize, shadowSize, dropShadowColor);

        int strokeSize = (int) (SizeManager.getScreenWidth() * mDimension / 120);

        if (!isOnlineTop)
            mLevelTextView.setStroke(strokeSize, Color.parseColor("#c9c9c9"));

    }


    private MainActivity getActivity() {


        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof MainActivity) {
                return (MainActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    private void init(Context context, AttributeSet attrs, int defStyle) {

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.UserLevelView, defStyle, 0);

        if (a.hasValue(R.styleable.UserLevelView_customDimension))
            mDimension = a.getFloat(R.styleable.UserLevelView_customDimension, 0.5f);
        if (a.hasValue(R.styleable.UserLevelView_textAlign)) {
            mTextAlign = a.getInt(R.styleable.UserLevelView_textAlign, 1);
        } else
            mTextAlign = 1;
        if (a.hasValue(R.styleable.UserLevelView_isOnlineTop)) {
            isOnlineTop = a.getBoolean(R.styleable.UserLevelView_isOnlineTop, isOnlineTop);
        }

        a.recycle();


    }

    public void setUserExp(int userExp) {
        mUserExp = userExp;
        expView.setImageBitmap(imageManager.loadImageFromResource(getExpID(), imageConverter.mWidth, imageConverter.mHeight, ImageManager.ScalingLogic.FIT));

    }

    public void setUserLevel(int userMark) {
        mUserLevel = userMark;
        mLevelTextView.setText(Tools.numeralStringToPersianDigits(userMark + ""));
    }

    private int getExpID() {
        int expLevel = mUser.getExp() + 1;
        switch (expLevel) {
            case 1:
                return R.drawable.exp1;
            case 2:
                return R.drawable.exp2;
            case 3:
                return R.drawable.exp3;
            case 4:
                return R.drawable.exp4;
            case 5:
                return R.drawable.exp5;
            case 6:
                return R.drawable.exp6;
            case 7:
                return R.drawable.exp7;
            case 8:
                return R.drawable.exp8;
            case 9:
                return R.drawable.exp8;


        }
        return R.drawable.exp1;
    }

    public void setUser(User user) {
        this.mUser = user;
        setUserLevel(user.getLevel());
        setUserExp(user.getExp());
        setUserName(user.getName());

        setShadowLayer(mLevelTextView);
        setShadowLayer(mUserNameTextView);


    }

    public void setUserName(String userName) {

        if (mTextAlign != TEXT_ALIGN_CENTER) {
            mUserNameTextView.setText(userName);

        } else {
            mUserNameTextView.setText(userName.subSequence(0, 2));

        }

    }

    public void setUserGuest() {
        mUserNameTextView.setText("عضویت/ورود");
    }

    public void setForOnlineGame() {
        setLevelTextSize();
        coverView.setImageBitmap(imageManager.loadImageFromResource((R.drawable.coveronlinegame), imageConverter.mWidth, imageConverter.mHeight, ImageManager.ScalingLogic.FIT));

    }

    public int getRealWidth() {
        return imageConverter.mWidth;
    }

    public int getRealHeight() {
        return imageConverter.mHeight;
    }

    public int getHeightPlusTextView() {
        return (int) (getRealHeight() + UiUtil.getTextViewHeight(mUserNameTextView) + SizeManager.getScreenHeight() * 0.02 * mDimension);
    }

    public void setOnlineStateClear() {

        expView.setVisibility(View.GONE);
        stateView.setVisibility(View.GONE);
        mFirstState = true;
    }

    public void setOnlineState(GameActionResult gameActionResult) {

        Log.d(this.getClass().getSimpleName(), "got user action " + mFirstState);
        Log.d(this.getClass().getSimpleName(), "got user action " + gameActionResult.getResult().toString());


        if (mFirstState) {
            expView.setVisibility(View.VISIBLE);
            expView.setImageBitmap(imageManager.loadImageFromResource(getDrawableIdForRight(gameActionResult), imageConverter.mWidth, imageConverter.mHeight));
            mFirstState = false;
            return;
        }
        stateView.setVisibility(View.VISIBLE);
        stateView.setImageBitmap(imageManager.loadImageFromResource(getDrawableIdForLeft(gameActionResult), imageConverter.mWidth, imageConverter.mHeight));


    }


    private int getDrawableIdForLeft(GameActionResult gameActionResult) {
        if (gameActionResult.isWrong() || gameActionResult.isSkiped())
            return R.drawable.wrong1;
        return R.drawable.correct1;
    }

    private int getDrawableIdForRight(GameActionResult gameActionResult) {
        if (gameActionResult.isWrong() || gameActionResult.isSkiped())
            return R.drawable.wrong2;
        return R.drawable.correct2;
    }

    private long lastTimeClicked = 0;

    @Override
    public void onClick(View v) {

        Log.d(this.getClass().getSimpleName(), "on click");

        if (!mClick || System.currentTimeMillis() - lastTimeClicked < 1000)
            return;

        lastTimeClicked = System.currentTimeMillis();

        if (Tools.isUserRegistered()) {


            if (mUser == null)
                return;
            if (!mUser.isMe()) {
                new UserViewDialog(getContext(), mUser).show();
            } else {
                new LeaderboardDialog().show(getActivity().getSupportFragmentManager(), "leaderboard");
            }

            return;
        }

        new RegistrationDialog(getContext(), false).show();


    }


    public boolean isClick() {
        return mClick;
    }

    public void setClick(boolean mClick) {
        this.mClick = mClick;
    }

}
