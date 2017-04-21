package com.asv;

import com.asv.model.Connection;
import com.asv.model.Server;
import com.asv.model.Sql2oModel;
import com.asv.model.User;
import com.asv.ssh.SSHManager;
import com.asv.ssh.SshCommands;
import com.google.gson.Gson;
import org.json.JSONObject;
import spark.QueryParamsMap;
import spark.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

/**
 * Created by daniel on 20/04/2017.
 */
public class Api {

    private static Sql2oModel db;
    private static Errors HttpError;

    private static void Init() throws FileNotFoundException {
        db = new Sql2oModel();
        HttpError = new Errors();

        // root is 'src/main/resources', so put files in 'src/main/resources/public'
        staticFiles.location("/public"); // Static files
    }

    public static void main(String[] args) throws FileNotFoundException {

        Init();

        get("prueba/users", (req, res) -> {
            String name = req.queryParams("name");

            Gson gson = new Gson();
            String json;

            if(name != null){
                User users = db.getUser(name);
                json = gson.toJson(users);
            }
            else{
                List<User> users = db.getUsers();
                json = gson.toJson(users);
            }

            return json;
        });

        put("prueba/users/:name", (req, res) -> {
            Set<String> ps = req.queryParams();

            String nameOrig = req.params("name");
            User user = null;

            if(ps.size() == 1){
                String jsonString = ps.toArray()[0].toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                String name = jsonObject.getString("name");
                String pass = jsonObject.getString("pass");
                user = db.updateUser(name, pass);
            }

            Gson gson = new Gson();
            String json = gson.toJson(user);

            return json;
        });

        post("prueba/users", (req, res) -> {

            Set<String> ps = req.queryParams();

            if(ps.size() == 1){
                String jsonString = ps.toArray()[0].toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                String name = jsonObject.getString("name");
                String pass = jsonObject.getString("pass");
                User user = db.addUser(name, pass);

                Gson gson = new Gson();
                String json = gson.toJson(user);

                return json;
            }

            res = HttpError.InternalServerError(res);
            return res.body();
        });

        delete("prueba/users", (req, res) -> {
            String name = req.queryParams("name");

            boolean deleted = db.deleteUser(name);

            if(!deleted){
                res = HttpError.InternalServerError(res);
                return res.body();

            }

            return 1;
        });

        post("/connection", (req, res) -> {
            String name = req.queryParams("name");
            int serverId = Integer.parseInt(req.queryParams("server_id"));

            Connection conn = db.addConnection(name, serverId);

            Gson gson = new Gson();
            String json = gson.toJson(conn);

            return json;

        });

        get("prueba/servers", (req, res) -> {
            String id = req.queryParams("id");

            Gson gson = new Gson();
            String json;

            List<Server> servers = db.getServers();
            json = gson.toJson(servers);


            return json;
        });

        get("prueba/servers/:id", (req, res) -> {
            String id = req.params("id");

            Gson gson = new Gson();
            String json;

            Server server = db.getServer(Integer.parseInt(id));
            json = gson.toJson(server);



            return json;
        });

        post("prueba/servers", (req, res) -> {

            Set<String> ps = req.queryParams();

            if(ps.size() == 1){
                String jsonString = ps.toArray()[0].toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                String name = jsonObject.getString("name");
                String ip = jsonObject.getString("ip");
                String userId = jsonObject.getString("admin_id");

                Server server = db.addServer(name, ip, userId);

                Gson gson = new Gson();
                String json = gson.toJson(server);

                return json;
            }

            res = HttpError.InternalServerError(res);
            return res.body();

        });

        put("prueba/servers/:id", (req, res) -> {
            Set<String> ps = req.queryParams();

            int id = Integer.parseInt(req.params("id"));
            Server server = null;

            if(ps.size() == 1){
                String jsonString = ps.toArray()[0].toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                String name = jsonObject.getString("name");
                String ip = jsonObject.getString("ip");
                String admin = jsonObject.getString("admin_id");
                server = db.updateServer(id, name, ip, admin);
            }

            Gson gson = new Gson();
            String json = gson.toJson(server);

            return json;
        });

        delete("prueba/servers/:id", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));

            boolean deleted = db.deleteServer(id);

            if(!deleted){
                res = HttpError.InternalServerError(res);
                return res.body();
            }

            return 1;
        });

        get("prueba/connections", (req, res) -> {

            Gson gson = new Gson();

            List<String> connectionsJsonParsed = new ArrayList<String>();
            List<Connection> connections = db.getConnections();

            for(Connection conn: connections){
                String userId = db.getUserConn(conn.id);
                String json = gson.toJson(conn);
                JSONObject jsonObject = new JSONObject(json);
                jsonObject.put("user_id", userId);
                connectionsJsonParsed.add(jsonObject.toString());
            }

            return connectionsJsonParsed;
        });

        get("prueba/connections/:id", (req, res) -> {

            int id = Integer.parseInt(req.params("id"));
            Gson gson = new Gson();

            Connection conn = db.getConnection(id);
            String userId = db.getUserConn(conn.id);
            String json = gson.toJson(conn);
            JSONObject jsonObject = new JSONObject(json);
            jsonObject.put("user_id", userId);

            return jsonObject;
        });

        delete("prueba/connections/:id", (req, res) -> {
            String userId = req.queryParams("user_id");
            int connId = Integer.parseInt(req.params("id"));

            //get the server admin access over ssh to grant access to the user
            Connection conn = db.getConnection(connId);
            User admin = db.getServerAdmin(conn.server_id);
            //get server and user data
            Server server = db.getServer(conn.server_id);
            User user = db.getUser(userId);

            //call the ssh command
            SSHManager ssh = new SSHManager(admin.name, admin.pass, server.ip, "");
            String cmd = SshCommands.DelUser(user.name);
            ssh.connect();
            ssh.sendCommand(cmd);
            ssh.close();

            boolean deleted = db.deleteConnection(connId);

            if(!deleted){
                res = HttpError.InternalServerError(res);
                return res.body();
            }

            return 1;
        });

        post("prueba/connections", (req, res) -> {
            Set<String> ps = req.queryParams();

            if(ps.size() == 1){
                String jsonString = ps.toArray()[0].toString();
                JSONObject jsonObject = new JSONObject(jsonString);
                String userId = jsonObject.getString("user_id");
                String name = jsonObject.getString("name");
                int serverId = jsonObject.getInt("server_id");

                Connection conn = db.addConnection(name, serverId);

                int connId = conn.id;

                //create ralation in db
                int insertCode = db.addUserConnection(userId, connId);

                if(insertCode <= 0){
                    res = HttpError.InternalServerError(res);
                    return res.body();
                }

                //get the server admin access over ssh to grant access to the user
                User admin = db.getServerAdmin(conn.server_id);
                //get server and user data
                Server server = db.getServer(conn.server_id);
                User user = db.getUser(userId);

                //call the ssh command
                SSHManager ssh = new SSHManager(admin.name, admin.pass, server.ip, "");
                String cmd = SshCommands.AddUser(user.name, user.pass);
                ssh.connect();
                ssh.sendCommand(cmd);
                ssh.close();

                return 1;
            }

            res = HttpError.InternalServerError(res);
            return res.body();
        });
    }
}
