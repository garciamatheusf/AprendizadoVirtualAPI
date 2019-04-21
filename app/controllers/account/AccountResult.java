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

    public static AccountResult sucessLogin(String token){
        AccountResult issueNFeResult = new AccountResult(200, "Operacao realizada com sucesso");
        issueNFeResult.objectNode.put("token", token);
        return issueNFeResult;
    }

    public static AccountResult accountNotFound() {
        AccountResult issueNFeResult = new AccountResult(401, "Email nao cadastrado");
        return issueNFeResult;
    }

    public static AccountResult wrongPassword() {
        AccountResult issueNFeResult = new AccountResult(401, "Senha inválido");
        return issueNFeResult;
    }

    public static AccountResult wrongTempPassword() {
        AccountResult issueNFeResult = new AccountResult(401, "Senha temporária inválido");
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

    public static AccountResult sucessRequestReset() {
        AccountResult issueNFeResult = new AccountResult(200, "Reset solicitado com sucesso");
        return issueNFeResult;
    }
}
