package controllers.recovery;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import play.Logger;

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
}
