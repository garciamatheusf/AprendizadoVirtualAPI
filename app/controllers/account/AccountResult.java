package controllers.account;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import play.Logger;

public class AccountResult extends DefaultResult {
    private final static Logger.ALogger appLogger = Logger.of("app");

    protected AccountResult(int status) {
        super(status);
    }

    protected AccountResult(int status, String msg) {
        super(status, msg);
    }

    public AccountResult(DefaultStatusCodes defaultStatusCodes) {
        super(defaultStatusCodes);
    }

    public static AccountResult sucess(){
        AccountResult issueNFeResult = new AccountResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }

    public static AccountResult loginError() {
        AccountResult issueNFeResult = new AccountResult(401, "Email ou senha inválido");
        return issueNFeResult;
    }

    public static AccountResult solicitationNotFound() {
        AccountResult issueNFeResult = new AccountResult(401, "Nao existe solicitao de reset para este e-mail");
        return issueNFeResult;
    }

    public static AccountResult tempPasswordWrong() {
        AccountResult issueNFeResult = new AccountResult(403, "Senha temporaria invalida, verifique seu e-mail");
        return issueNFeResult;
    }
}
