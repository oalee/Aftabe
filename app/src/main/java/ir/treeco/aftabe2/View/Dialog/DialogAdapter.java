package ir.treeco.aftabe2.View.Dialog;

import android.content.Context;
import android.view.View;

import com.pixplicity.easyprefs.library.Prefs;

import ir.treeco.aftabe2.Object.User;

/**
 * Created by root on 5/11/16.
 */
public class DialogAdapter {

    public static void makeFriendRequestDialog(Context context, View.OnClickListener yesClick) {
        String msg = "درخواست دوستی";
        String yes = "بفرست";
        String no = "نفرست";
        new SkipAlertDialog(context, msg, yesClick, null).show();
    }

    public static void makeFriendRemoveDialog(Context context, View.OnClickListener yesClick) {
        String msg = "حذف دوستی";
        String yes = "بکن";
        String no = "نکن";
        new SkipAlertDialog(context, msg, yesClick, null).show();

    }

    public static void makeMatchRequestDialog(Context context, User user, View.OnClickListener yesClick) {

        new MatchRequestDialog(context, user, true, yesClick).show();

    }


    public static boolean makeTutorialDialog(Context context, String firstLine, String secondLine) {


        String text = (secondLine.equals("")) ? firstLine : String.format("%s\n%s", firstLine, secondLine);

        if (Prefs.contains(text.hashCode() + ""))
            return false;
        Prefs.putBoolean(text.hashCode() + "", true);
        new SkipAlertDialog(context, text).show();

        return true;

    }
}
