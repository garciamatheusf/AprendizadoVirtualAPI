package controllers.question;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.Question;
import models.entities.User;
import org.slf4j.Marker;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import util.JsonValidation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class QuestionController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;
    @Inject
    private JPAApi jpaApi;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                //RESTRINGIR ESTE MÉTODO
                appLogger.info("Listagem de todas as perguntas solicitada.");

                ArrayList<Question> questions = jpaApi.withTransaction(Question::getAll);
                return ok(QuestionResult.sucess(questions).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> getQuestion(Long id){
        return CompletableFuture.supplyAsync(() -> {
            try {
                appLogger.info("Informacoes de pergunta solicitada. Informacoes recebidas {}", id);

                if (id == 0) {
                    return badRequest(DefaultResult.forBadRequest("Id da aula nao informado").asJson());
                }

                Question question = jpaApi.withTransaction(em -> Question.getById(em, id));

                if (question == null) {
                    return notFound(QuestionResult.questionNotFound().asJson());
                }

                return ok(QuestionResult.questionFound(question).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> createQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Cadastro de pergunta solicitado. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "question", "author", "idlesson");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User user = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(user == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }

                if(!user.email.equals(bodyNode.get("author").asText())){
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                Question question = new Question();
                question.question = bodyNode.get("question").binaryValue();
                question.author = bodyNode.get("author").asText();
                question.idlesson = bodyNode.get("idlesson").asInt();

                jpaApi.withTransaction(() -> Question.insertWithObject(jpaApi.em(), question));

                return ok(QuestionResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> updateQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Alteracao de infos de pergunta solicitada. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "id");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                Question question = jpaApi.withTransaction(em -> Question.getById(em, bodyNode.get("id").asInt()));
                if(question == null){
                    return notFound(QuestionResult.questionNotFound().asJson());
                }
                if (!requester.email.equals(question.author)) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                if(bodyNode.has("question")){
                    question.question = bodyNode.get("question").binaryValue();
                }

                jpaApi.withTransaction(em -> Question.update(em, question));

                return ok(QuestionResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> deleteQuestion(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Delete de pergunta solicitado. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "id");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                Question question = jpaApi.withTransaction(em -> Question.getById(em, bodyNode.get("id").asInt()));
                if(question == null){
                    return notFound(QuestionResult.questionNotFound().asJson());
                }
                if (!requester.email.equals(question.author)){
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                jpaApi.withTransaction(em -> Question.remove(em, question));

                return ok(QuestionResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }
}
