package controllers.lesson;

import controllers.DefaultResult;
import controllers.DefaultStatusCodes;
import models.entities.Lesson;
import play.Logger;

import java.util.ArrayList;

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

    public static LessonResult lessonNotFound() {
        LessonResult lessonResult = new LessonResult(404, "Aula nao encontrada");
        return lessonResult;
    }

    public static LessonResult lessonFound(Lesson lesson) {
        LessonResult lessonResult = new LessonResult(200, "Operacao realizada com sucesso");
        lessonResult.objectNode.putPOJO("lesson", lesson);

        return lessonResult;
    }

    public static LessonResult sucess(){
        LessonResult issueNFeResult = new LessonResult(200, "Operacao realizada com sucesso");
        return issueNFeResult;
    }

    public static LessonResult sucess(ArrayList<Lesson> lessons) {
        LessonResult result = new LessonResult(200, "Operacao realizada com sucesso");
        result.objectNode.putPOJO("lessons", lessons);

        return result;
    }

    public static LessonResult studentCreatingLesson() {
        LessonResult result = new LessonResult(403, "Estudante nao pode criar aula");
        return result;
    }
}
