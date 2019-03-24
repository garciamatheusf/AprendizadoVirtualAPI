package controllers.question;

import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class QuestionController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(QuestionResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> getQuestion(Integer id){
        return CompletableFuture.supplyAsync(() -> {
            return ok(QuestionResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> createQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(QuestionResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> updateQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(QuestionResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> deleteQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            return ok(QuestionResult.sucess().asJson());

        }, ec.current());
    }
}
