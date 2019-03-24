package controllers.user;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import play.Logger;

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
        UserResult issueNFeResult = new UserResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }

    public static UserResult mailAlreadyRegistered() {
        UserResult issueNFeResult = new UserResult(403, "E-mail ja cadastrado");
        return issueNFeResult;
    }
}
