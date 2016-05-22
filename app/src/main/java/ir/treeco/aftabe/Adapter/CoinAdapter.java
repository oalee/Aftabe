package ir.treeco.aftabe.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;

import ir.treeco.aftabe.Util.Tools;
import ir.treeco.aftabe.View.Dialog.SkipAlertDialog;

public class CoinAdapter {
    public static final int LEVEL_COMPELETED_PRIZE = 30;
    public static final int ALPHABET_HIDING_COST = 40;
    public static final int LETTER_REVEAL_COST = 50;
    public static final int SKIP_LEVEL_COST = 130;
    public static final String SHARED_PREF_COIN_DIFF = "aftabe_wc";
    private DBAdapter db;
    private Context context;
    private Tools tools;
    private Activity mActivity;

    public CoinAdapter(Context context, Activity activity) {
        this.context = context;
        db = DBAdapter.getInstance(context);
        tools = new Tools(context);
        mActivity = activity;
    }

    public interface CoinsChangedListener {
        void changed(int newAmount);
    }

    public boolean spendCoins(int amount) {
        int nextAmount = getCoinsCount() - amount;
        if (nextAmount < 0) {
//            ToastMaker.show(context, context.getString(R.string.not_enought_coins), Toast.LENGTH_SHORT);
            new SkipAlertDialog(context, "یک ویدیو ببینید ۲۰ سکه بگیرید" + "\n" + "سکه کافی ندارید", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    TODO add a link to video ad
                }
            }, null).show();

            return false;
        }
        addCoinDiff(-amount);
        setCoinsCount(nextAmount);
        return true;
    }

    public void earnCoins(int amount) {
        int nextAmount = getCoinsCount() + amount;
        addCoinDiff(amount);
        setCoinsCount(nextAmount);
    }

    public int getCoinDiff() {
        return Prefs.getInt(SHARED_PREF_COIN_DIFF, 0);
    }

    private void addCoinDiff(int diff) {
        Prefs.putInt(SHARED_PREF_COIN_DIFF, getCoinDiff() + diff);
    }

    public int getCoinsCount() {
        return db.getCoins();
    }

    public void setCoinsCount(int nextAmount) {
        db.updateCoins(nextAmount);
        tools.backUpDB();

        for (CoinsChangedListener listener : listeners)
            listener.changed(nextAmount);
    }

    private static ArrayList<CoinsChangedListener> listeners = new ArrayList<>();

    public void setCoinsChangedListener(CoinsChangedListener listener) {
        listeners.add(listener);
        listener.changed(getCoinsCount());
    }
}
