package ir.treeco.aftabe.API.Socket.Objects.Result;

import com.google.gson.annotations.Expose;

/**
 * Created by al on 3/14/16.
 */
public class ScoreResult {

    @Expose
    String userId;

    @Expose
    int score;


    public int getScore() {
        return score;
    }

    public String getUserId() {
        return userId;
    }


}