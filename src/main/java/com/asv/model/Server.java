package com.asv.model;

import lombok.Data;

/**
 * Created by daniel on 20/04/2017.
 */
@Data
public class Server {
    private int id;
    private String name;
    private String admin_id;
    public String ip;
}
