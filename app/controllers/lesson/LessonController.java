package controllers.lesson;

import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LessonController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> getLesson(Integer id){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> getLastTen(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> createLesson(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> updateLesson(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> deleteLesson(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(LessonResult.sucess().asJson());

        }, ec.current());
    }
}
