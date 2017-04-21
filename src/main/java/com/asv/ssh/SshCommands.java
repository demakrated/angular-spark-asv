package com.asv.ssh;

/**
 * Created by daniel on 20/04/2017.
 */
public class SshCommands {
    public static String AddUser(String name, String pass){
        String cmd = "useradd -m " + name + " -s /bin/bash && " +
                "echo " + name + ":" + pass + " | chpasswd";

        return cmd;
    }

    public static String DelUser(String name){
        String cmd = "userdel -r -f " + name;

        return cmd;
    }
}
