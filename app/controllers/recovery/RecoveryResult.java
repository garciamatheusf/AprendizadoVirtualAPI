package controllers.recovery;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import models.entities.RecoveryPassword;
import play.Logger;

import java.util.ArrayList;

public class RecoveryResult extends DefaultResult {
    private final static Logger.ALogger appLogger = Logger.of("app");

    protected RecoveryResult(int status) {
        super(status);
    }

    protected RecoveryResult(int status, String msg) {
        super(status, msg);
    }

    public RecoveryResult(DefaultStatusCodes defaultStatusCodes) {
        super(defaultStatusCodes);
    }

    public static RecoveryResult sucess(){
        RecoveryResult issueNFeResult = new RecoveryResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }

    public static RecoveryResult sucess(ArrayList<RecoveryPassword> lessons) {
        RecoveryResult result = new RecoveryResult(200, "Operacao realizada com sucesso");
        result.objectNode.putPOJO("recoveries", lessons);

        return result;
    }

    public static RecoveryResult recoveryAlreadySolicited(){
        RecoveryResult result = new RecoveryResult(403, "Ja existe solicitacao para este e-mail");
        return result;
    }

    public static RecoveryResult recoveryNotFound() {
        RecoveryResult result = new RecoveryResult(404, "Solicitacao de recuperacao de senha nao encontrado");
        return result;
    }
}
