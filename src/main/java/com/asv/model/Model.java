package com.asv.model;

import java.util.List;

/**
 * Created by daniel on 20/04/2017.
 */
public interface Model {
    List getUsers();
    User getUser(String name);
    User addUser(String name, String pass);
    User updateUser(String name, String pass);
    boolean deleteUser(String name);
    Server getServer(int id);
    List getServers();
    Server addServer(String name, String ip,  String userId);
    Server updateServer(int id, String name, String ip,  String userId);
    boolean deleteServer(int id);
    com.asv.model.Connection addConnection(String name, int serverId);
    List<com.asv.model.Connection> getConnections();
    com.asv.model.Connection getConnection(int id);
    boolean deleteConnection(int id);
    int addUserConnection(String userId, int connectionId);
    User getServerAdmin(int serverId);
}