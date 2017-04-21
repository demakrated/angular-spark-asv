package com.asv.model;

import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.Sql2o;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.io.InputStream;

/**
 * Created by daniel on 20/04/2017.
 */
public class Sql2oModel implements Model {

    private Sql2o sql2o;

    public Sql2oModel() throws FileNotFoundException {
        Properties prop = new Properties();
        String propFileName = "config.properties";

        InputStream input = getClass().getClassLoader().getResourceAsStream(propFileName);

        try {
            if (input != null) {
                prop.load(input);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            String host = prop.getProperty("db.host");
            String port = prop.getProperty("db.port");
            String db = prop.getProperty("db.database");
            String user = prop.getProperty("db.user");
            String pass = prop.getProperty("db.pass");

            this.sql2o = new Sql2o("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false",
                    user, pass);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    @Override
    public List<User> getUsers(){
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery("select * from user")
                    .executeAndFetch(User.class);
            return users;
        }
    }

    @Override
    public List<Server> getServers(){
        try (Connection conn = sql2o.open()) {
            List<Server> servers = conn.createQuery("select * from server")
                    .executeAndFetch(Server.class);
            return servers;
        }
    }

    @Override
    public User getUser(String name){
        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery("select * from user where name = :name")
                    .addParameter("name", name)
                    .executeAndFetch(User.class);
            if(users.size() == 1){
                return users.get(0);
            }
            return null;
        }
    }

    @Override
    public com.asv.model.Connection getConnection(int id){
        try (Connection conn = sql2o.open()) {
            List<com.asv.model.Connection> connections = conn.createQuery("select * from connection where id = :id")
                    .addParameter("id", id)
                    .executeAndFetch(com.asv.model.Connection.class);
            if(connections.size() == 1){
                return connections.get(0);
            }
            return null;
        }
    }

    @Override
    public List<com.asv.model.Connection> getConnections(){

        String sql = "select * from connection";

        try (Connection conn = sql2o.open()) {
            List<com.asv.model.Connection> conns = conn.createQuery(sql)
                    .executeAndFetch(com.asv.model.Connection.class);

            return conns;
        }
    }

    public String getUserConn(int conn_id){
        String sql = "select * from conn_user where conn_id = :conn_id";

        try (Connection conn = sql2o.open()) {
            return conn.createQuery(sql)
                    .addParameter("conn_id", conn_id)
                    .executeAndFetchFirst(ConnectionUser.class).user_id;

        }
    }

    @Override
    public User addUser(String name, String pass){

        String sql = "insert into user(name, pass) VALUES (:name, :pass)";

        try (Connection conn = sql2o.open()) {

            int id = conn.createQuery(sql, true)
                .addParameter("name", name)
                .addParameter("pass", pass)
                .executeUpdate().getResult();

            return getUser(name);

        }
    }

    @Override
    public int addUserConnection(String userId, int connectionId){

        String sql = "insert into conn_user(user_id, conn_id) VALUES (:userId, :connectionId)";

        try (Connection conn = sql2o.open()) {

            int inserted = conn.createQuery(sql, true)
                    .addParameter("userId", userId)
                    .addParameter("connectionId", connectionId)
                    .executeUpdate()
                    .getResult();

            return inserted;
        }
    }

    @Override
    public Server addServer(String name, String ip, String userId){

        String sql = "insert into server(name, ip, admin_id) VALUES (:name, :ip, :admin_id)";

        try (Connection conn = sql2o.open()) {

            int id = conn.createQuery(sql, true)
                    .addParameter("name", name)
                    .addParameter("ip", ip)
                    .addParameter("admin_id", userId)
                    .executeUpdate()
                    .getKey(int.class);

            return getServer(id);

        }
    }

    @Override
    public com.asv.model.Connection addConnection(String name, int serverId){

        String sql = "insert into connection(name, server_id) VALUES (:name, :id)";

        try (Connection conn = sql2o.open()) {

            int id = conn.createQuery(sql, true)
                    .addParameter("name", name)
                    .addParameter("id", serverId)
                    .executeUpdate()
                    .getKey(int.class);

            return getConnection(id);
        }
    }

    @Override
    public Server getServer(int id){
        try (Connection conn = sql2o.open()) {
            List<Server> servers = conn.createQuery("select * from server where id = :id")
                    .addParameter("id", id)
                    .executeAndFetch(Server.class);
            if(servers.size() == 1){
                return servers.get(0);
            }
            return null;
        }
    }

    @Override
    public User getServerAdmin(int serverId){

        String sql = "select user.* from user join server on user.name = server.admin_id where server.id = :id";

        try (Connection conn = sql2o.open()) {
            List<User> users = conn.createQuery(sql)
                    .addParameter("id", serverId)
                    .throwOnMappingFailure(false)
                    .executeAndFetch(User.class);
            if(users.size() == 1){
                return users.get(0);
            }
            return null;
        }
    }

    @Override
    public User updateUser(String name, String pass){
        String sql = "update user set name = :name, pass = :pass where name = :name";
        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("name", name)
                    .addParameter("pass", pass)
                    .executeUpdate();

            return getUser(name);
        }
    }

    @Override
    public Server updateServer(int id, String name, String ip, String admin_id){
        String sql = "update server set name = :name, ip = :ip, admin_id = :admin_id where id = :id";
        try (Connection conn = sql2o.open()) {
            conn.createQuery(sql)
                    .addParameter("id", id)
                    .addParameter("name", name)
                    .addParameter("ip", ip)
                    .addParameter("admin_id", admin_id)
                    .executeUpdate();

            return getServer(id);
        }
    }

    @Override
    public boolean deleteUser(String name){
        String sql = "delete from user where name = :name";
        try (Connection conn = sql2o.open()) {
            int result = conn.createQuery(sql)
                    .addParameter("name", name)
                    .executeUpdate()
            .getResult();

            if(result == 1){
                return true;
            }

            return false;
        }
    }

    @Override
    public boolean deleteConnection(int conn_id){
        String sql = "delete from conn_user where conn_id = :conn_id";
        try (Connection conn = sql2o.open()) {
            int result = conn.createQuery(sql)
                    .addParameter("conn_id", conn_id)
                    .executeUpdate()
                    .getResult();

            if(result == 1){
                sql = "delete from connection where id = :conn_id";
                result = conn.createQuery(sql)
                    .addParameter("conn_id", conn_id)
                    .executeUpdate()
                    .getResult();

                if(result == 1){
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    public boolean deleteServer(int id){
        String sql = "delete from server where id = :id";
        try (Connection conn = sql2o.open()) {
            int result = conn.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate()
                    .getResult();

            if(result == 1){
                return true;
            }

            return false;
        }
    }
}
