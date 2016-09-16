package com.steven.nkuwlanlogin;

import java.io.Serializable;

/**
 * Created by stevensai on 16/9/14.
 */
public class User implements Serializable{

    String uid;
    String upwd;

    public User(String uid,String upwd) {
        this.uid = uid;
        this.upwd = upwd;
    }


}
