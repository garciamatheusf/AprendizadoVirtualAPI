package controllers.user;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import models.entities.User;
import play.Logger;

import java.util.ArrayList;

public class UserResult extends DefaultResult {
    private final static Logger.ALogger appLogger = Logger.of("app");

    protected UserResult(int status) {
        super(status);
    }

    protected UserResult(int status, String msg) {
        super(status, msg);
    }

    public UserResult(DefaultStatusCodes defaultStatusCodes) {
        super(defaultStatusCodes);
    }

    public static UserResult sucess(){
        UserResult user = new UserResult(200, "Operacao realizada com sucesso");
        return user;
    }

    public static UserResult mailAlreadyRegistered() {
        UserResult user = new UserResult(403, "E-mail ja cadastrado");
        return user;
    }

    public static UserResult userNotFound() {
        UserResult user = new UserResult(404, "Usuario nao encontrado");
        return user;
    }

    public static UserResult userFound(User user) {
        UserResult userResult = new UserResult(200, "Operacao realizada com sucesso");
        userResult.objectNode.putPOJO("user", user);

        return userResult;
    }

    public static UserResult bornDateInvalid() {
        UserResult user = new UserResult(401, "Data de nascimento invalida");
        return user;
    }

    public static UserResult sucess(ArrayList<User> users) {
        UserResult result = new UserResult(200, "Operacao realizada com sucesso");
        result.objectNode.putPOJO("users", users);

        return result;
    }
}
