package ir.treeco.aftabe.API.Rest.Interfaces;

import ir.treeco.aftabe.Object.User;

/**
 * Created by al on 3/13/16.
 */
public interface BatchUserFoundListener {

    void onGotUserList(User[] users);

    void onGotError();

}