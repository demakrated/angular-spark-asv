package com.asv;

import lombok.Data;
import spark.Response;

/**
 * Created by daniel on 20/04/2017.
 */
@Data
public class Errors {
    public Response InternalServerError(Response res){
        res.status(500);
        res.type("application/json");
        res.body("{\"message\":\"Internal Server Error\"}");
        return res;
    }

    public Response NotFound(Response res){
        res.status(404);
        res.type("application/json");
        res.body("{\"message\":\"Item not found\"}");
        return res;
    }
}
