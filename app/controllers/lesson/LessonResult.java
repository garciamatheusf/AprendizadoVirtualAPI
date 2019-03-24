package controllers.lesson;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import play.Logger;

public class LessonResult extends DefaultResult {
    private final static Logger.ALogger appLogger = Logger.of("app");

    protected LessonResult(int status) {
        super(status);
    }

    protected LessonResult(int status, String msg) {
        super(status, msg);
    }

    public LessonResult(DefaultStatusCodes defaultStatusCodes) {
        super(defaultStatusCodes);
    }

    public static LessonResult sucess(){
        LessonResult issueNFeResult = new LessonResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }
}
