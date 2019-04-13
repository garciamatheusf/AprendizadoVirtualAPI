package controllers.question;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import models.entities.Question;
import play.Logger;

import java.util.ArrayList;

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

    public static QuestionResult questionNotFound() {
        QuestionResult questionResult = new QuestionResult(404, "Pergunta nao encontrada");
        return questionResult;
    }

    public static QuestionResult questionFound(Question question) {
        QuestionResult questionResult = new QuestionResult(200, "Operacao realizada com sucesso");
        questionResult.objectNode.putPOJO("lesson", question);

        return questionResult;
    }

    public static QuestionResult sucess(){
        QuestionResult questionResult = new QuestionResult(200, "Operacao realizada com sucesso");
        return questionResult;
    }

    public static QuestionResult sucess(ArrayList<Question> lessons) {
        QuestionResult result = new QuestionResult(200, "Operacao realizada com sucesso");
        result.objectNode.putPOJO("questions", lessons);

        return result;
    }
}
