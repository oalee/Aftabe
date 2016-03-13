package ir.treeco.aftabe.API;

import android.util.Log;

import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.okhttp.OkHttpClient;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ir.treeco.aftabe.API.Utils.CoinDiffHolder;
import ir.treeco.aftabe.API.Utils.GoogleToken;
import ir.treeco.aftabe.API.Utils.GuestCreateToken;
import ir.treeco.aftabe.API.Utils.LeaderboardContainer;
import ir.treeco.aftabe.API.Utils.SMSCodeHolder;
import ir.treeco.aftabe.API.Utils.SMSRequestToken;
import ir.treeco.aftabe.API.Utils.SMSToken;
import ir.treeco.aftabe.API.Utils.SMSValidateToken;
import ir.treeco.aftabe.API.Utils.UsernameCheck;
import ir.treeco.aftabe.Adapter.CoinAdapter;
import ir.treeco.aftabe.Object.TokenHolder;
import ir.treeco.aftabe.Object.User;
import ir.treeco.aftabe.API.Utils.LoginInfo;
import ir.treeco.aftabe.Util.RandomString;
import ir.treeco.aftabe.Util.Tools;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by al on 3/4/16.
 */
public class AftabeAPIAdapter {

    private static Retrofit retrofit;
    private static AftabeService aftabeService;
    private final static String baseUrl = "https://aftabe2.com:2020";
    private static final String TAG = "AftabeAPIAdapter";

    public static boolean isNull(){
        return retrofit == null;
    }

    private static void init() {
        if (retrofit == null) {

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
            okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

            aftabeService = retrofit.create(AftabeService.class);
        }

    }

    public static void createGuestUser(final UserFoundListener userFoundListener) {

        init();

        final GuestCreateToken guestCreateToken = new GuestCreateToken(RandomString.nextString());

        getGuestUser(guestCreateToken, userFoundListener);

    }

    public static void getGuestUser(final GuestCreateToken guestCreateToken, final UserFoundListener userFoundListener) {

        Call<LoginInfo> call = aftabeService.getGuestUserLogin(guestCreateToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {


                if (!response.isSuccess()) {
                    userFoundListener.onGetError();
                    return;
                }

                final LoginInfo loginInfo = response.body();

                getMyUserByAccessToken(loginInfo, userFoundListener);

            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void getUser(User myUser, String otherUserId, final UserFoundListener userFoundListener) {

        init();

        Call<User> call = aftabeService.getUser(myUser.getLoginInfo().accessToken, otherUserId);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {


                if (!response.isSuccess()) {
                    userFoundListener.onGetError();
                    return;
                }

                if (userFoundListener != null) userFoundListener.onGetUser(response.body());
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();

            }
        });
    }

    public static void getSMSActivatedUser(final SMSToken smsToken, final UserFoundListener userFoundListener) {
        Call<LoginInfo> call = aftabeService.getSMSUserLogin(smsToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {


                if (!response.isSuccess()) {
                    userFoundListener.onGetError();
                    return;
                }

                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);

            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void submitSMSActivationCode(SMSValidateToken smsValidateToken, String userName,
                                               final UserFoundListener userFoundListener) {

        SMSToken smsToken = new SMSToken();
        smsToken.update(smsValidateToken, userName);
        getSMSActivatedUser(smsToken, userFoundListener);
    }

    public static void checkSMSActivationCode(SMSValidateToken smsToken, SMSCodeHolder codeHolder, final SMSValidationListener smsValidationListener) {

        init();

        Call<SMSValidateToken> call = aftabeService.checkSMSCodeReq(codeHolder, smsToken.getId());
        call.enqueue(new Callback<SMSValidateToken>() {
            @Override
            public void onResponse(Response<SMSValidateToken> response) {

                smsValidationListener.onValidatedCode(response.body());
            }

            @Override
            public void onFailure(Throwable t) {

                smsValidationListener.onSMSValidationCodeFail();
            }
        });
    }

    public static void requestSMSActivation(SMSRequestToken smsRequestToken, final SMSValidationListener smsValidationListener) {


        init();

        Log.d(TAG, "request sms activation");
        Call<SMSValidateToken> smsTokenCall = aftabeService.getSMSToken(smsRequestToken);
        smsTokenCall.enqueue(new Callback<SMSValidateToken>() {
            @Override
            public void onResponse(Response<SMSValidateToken> response) {

                smsValidationListener.onSMSValidateSent(response.body());

                Log.d(TAG, "request sms activation on response " + response.isSuccess());
            }

            @Override
            public void onFailure(Throwable t) {
                smsValidationListener.onSMSValidationFail();

            }
        });
    }

    private static void getMyUserByAccessToken(final LoginInfo loginInfo,
                                               final UserFoundListener userFoundListener) {

        Log.d(TAG, "get user by access token");
        Call<User> c = aftabeService.getMyUser(loginInfo.accessToken);
        c.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {

                if (!response.isSuccess()) {
                    userFoundListener.onGetError();

                    Log.d(TAG, " is not sucess");
                    return;
                }

                Log.d(TAG, " is  sucess");
                Log.d(TAG, (userFoundListener == null) + " is null ?");

                response.body().setLoginInfo(loginInfo);
                response.body().setOwnerMe();
                if (userFoundListener != null) userFoundListener.onGetUser(response.body());
                Tools.updateSharedPrefsToken(response.body(), new TokenHolder(response.body()));
            }

            @Override
            public void onFailure(Throwable t) {
                Log.d(TAG, "failure at get user by access token");
                Log.d(TAG, t.toString());
                t.printStackTrace();

                if (userFoundListener != null) userFoundListener.onGetError();
            }
        });
    }

    public static void getMyUserByGoogle(final GoogleToken googleToken, final UserFoundListener userFoundListener) {

        init();

        Call<LoginInfo> call = aftabeService.getMyUserLogin(googleToken);
        call.enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Response<LoginInfo> response) {
                Log.d(TAG, response.toString());
                Log.d(TAG, response.body().accessToken + " " + response.body().created);
                final LoginInfo loginInfo = response.body();
                getMyUserByAccessToken(loginInfo, userFoundListener);
            }

            @Override
            public void onFailure(Throwable t) {
                if (userFoundListener != null) userFoundListener.onGetError();
                Log.d("TAG", "Fail");
            }
        });
    }

    public static void checkUsername(final String username, final UsernameCheckListener usernameCheckListener) {

        init();
        Call<UsernameCheck> checkCall = aftabeService.checkUserName(username);
        checkCall.enqueue(new Callback<UsernameCheck>() {
            @Override
            public void onResponse(Response<UsernameCheck> response) {

                if (!response.isSuccess()) {
                    usernameCheckListener.onCheckedUsername(false, username);
                    return;
                }

                if (response.body().isUsernameAccessible())
                    usernameCheckListener.onCheckedUsername(true, username);
                else
                    usernameCheckListener.onCheckedUsername(false, username);
            }

            @Override
            public void onFailure(Throwable t) {
                usernameCheckListener.onCheckedUsername(false, username);
            }
        });
    }

    public static void tryToLogin(final UserFoundListener userFoundListener) {

        init();
        Log.d("AftabeAPIAdapter", "try to login");
        if (!Prefs.contains(Tools.ENCRYPT_KEY) || !Prefs.contains(Tools.SHARED_PREFS_TOKEN))
            return;
        try {
            Gson gson = new Gson();
            TokenHolder tokenHolder = gson.fromJson(Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""), TokenHolder.class);
            if (tokenHolder.getLoginInfo() == null)
                return;
            Log.d(TAG, Prefs.getString(Tools.SHARED_PREFS_TOKEN, ""));

            getMyUserByAccessToken(tokenHolder.getLoginInfo(), userFoundListener);

        } catch (Exception e) {
            userFoundListener.onGetError();
            Log.d("AftabeAPIAdapter", "exception accoured in try to login ");
            e.printStackTrace();
        }
    }

    public static void searchForUser(User myUser, String search, UserFoundListener userFoundListener) {
        if (Tools.isAPhoneNumber(search))
            searchUserByNumber(myUser, (search.length() == 10) ? search : search.substring(1),
                    userFoundListener);
        else if (Tools.isAEmail(search))
            searchUserByMail(myUser, search, userFoundListener);
        else
            searchUserByName(myUser, search, userFoundListener);


    }

    public static void searchUserByNumber(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByPhoneNumber(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {


                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();

            }
        });
    }

    public static void searchUserByMail(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByEmail(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {


                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();
            }
        });
    }

    public static void searchUserByName(User myUser, String search, final UserFoundListener userFoundListener) {

        Call<User[]> call =
                aftabeService.searchByUsername(myUser.getLoginInfo().getAccessToken(), search);
        call.enqueue(new Callback<User[]>() {
            @Override
            public void onResponse(Response<User[]> response) {

                if (response.body().length == 0)
                    userFoundListener.onGetError();

                for (User user : response.body())
                    userFoundListener.onGetUser(user);
            }

            @Override
            public void onFailure(Throwable t) {
                userFoundListener.onGetError();
            }
        });
    }

    public static void getLoaderboard(User myUser, final LeaderboardUserListener leaderboardUserListener) {
        init();
        Call<LeaderboardContainer> call = aftabeService.getLeaderboard(myUser.getLoginInfo().getAccessToken(), "score");
        call.enqueue(new Callback<LeaderboardContainer>() {
            @Override
            public void onResponse(Response<LeaderboardContainer> response) {

                if (!response.isSuccess())
                    leaderboardUserListener.onGotError();
                leaderboardUserListener.onGotLeaderboard(response.body().getBoard());
            }

            @Override
            public void onFailure(Throwable t) {

                leaderboardUserListener.onGotError();
            }
        });
    }

    public static void updateCoin(User myUser) {
        init();
        int diff = Prefs.getInt(CoinAdapter.SHARED_PREF_COIN_DIFF, 0);
        CoinDiffHolder coinDiffHolder = new CoinDiffHolder(diff);
        Call<User> call = aftabeService.updateCoin(coinDiffHolder, myUser.getId(), myUser.getLoginInfo().getAccessToken());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Response<User> response) {
                if (response.isSuccess())
                    return;
                Prefs.putInt(CoinAdapter.SHARED_PREF_COIN_DIFF, 0);

            }

            @Override
            public void onFailure(Throwable t) {
                Prefs.putInt(CoinAdapter.SHARED_PREF_COIN_DIFF, 0);

            }
        });
    }

}
