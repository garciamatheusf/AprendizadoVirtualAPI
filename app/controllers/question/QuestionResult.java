package controllers.question;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import play.Logger;

public class QuestionResult extends DefaultResult {
    private final static Logger.ALogger appLogger = Logger.of("app");

    protected QuestionResult(int status) {
        super(status);
    }

    protected QuestionResult(int status, String msg) {
        super(status, msg);
    }

    public QuestionResult(DefaultStatusCodes defaultStatusCodes) {
        super(defaultStatusCodes);
    }

    public static QuestionResult sucess(){
        QuestionResult issueNFeResult = new QuestionResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }
}
