package controllers.lesson;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.Lesson;
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
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LessonController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;
    @Inject
    private JPAApi jpaApi;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                //RESTRINGIR ESTE MÉTODO
                appLogger.info("Listagem de todas as aulas solicitada.");

                ArrayList<Lesson> lessons = jpaApi.withTransaction(Lesson::getAll);

                return ok(LessonResult.sucess(lessons).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> getLesson(long id){
        return CompletableFuture.supplyAsync(() -> {
            try {
                appLogger.info("Informacoes de aula solicitada. Informacoes recebidas {}", id);

                if (id == 0) {
                    return badRequest(DefaultResult.forBadRequest("Id da aula nao informado").asJson());
                }

                Lesson lesson = jpaApi.withTransaction(em -> Lesson.getById(em, id));

                if (lesson == null) {
                    return notFound(LessonResult.lessonNotFound().asJson());
                }

                return ok(LessonResult.lessonFound(lesson).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> getLastTen(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                appLogger.info("Informacoes das ultimas dez aulas solicitado");

                ArrayList<Lesson> lessons = jpaApi.withTransaction(Lesson::getLastTen);

                if (lessons.isEmpty()) {
                    return notFound(LessonResult.lessonNotFound().asJson());
                }

                return ok(LessonResult.sucess(lessons).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }

    public CompletionStage<Result> createLesson(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Cadastro de nova aula solicitado. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "name", "url");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User user = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(user == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                if(user.student){
                    return unauthorized(LessonResult.studentCreatingLesson().asJson());
                }

                Lesson lesson = new Lesson();
                updateInfos(bodyNode, lesson);
                lesson.date = new Date();
                lesson.author = user.email;
                lesson.views = 0;

                jpaApi.withTransaction(() -> Lesson.insertWithObject(jpaApi.em(), lesson));

                return ok(LessonResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> updateLesson(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Alteracao de infos de aula solicitada. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "id");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                Lesson lesson = jpaApi.withTransaction(em -> Lesson.getById(em, bodyNode.get("id").asInt()));
                if(lesson == null){
                    return notFound(LessonResult.lessonNotFound().asJson());
                }
                if (!requester.email.equals(lesson.author)) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                updateInfos(bodyNode, lesson);

                jpaApi.withTransaction(em -> Lesson.update(em, lesson));

                return ok(LessonResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    private void updateInfos(JsonNode bodyNode, Lesson solicitante) {
        if(bodyNode.has("name")){
            solicitante.name = bodyNode.get("name").asText();
        }
        if(bodyNode.has("url")){
            solicitante.url = bodyNode.get("url").asText();
        }
        if(bodyNode.has("description")){
            solicitante.description = bodyNode.get("description").asText();
        }
        if(bodyNode.has("live")){
            solicitante.live = bodyNode.get("live").asBoolean();
        }
        if(bodyNode.has("views")){
            solicitante.views = bodyNode.get("views").asInt();
        }
        if(bodyNode.has("image")){
            solicitante.image = bodyNode.get("image").asText();
        }
    }

    public CompletionStage<Result> deleteLesson(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Delete de aula solicitado. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "id");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                Lesson lesson = jpaApi.withTransaction(em -> Lesson.getById(em, bodyNode.get("id").asInt()));
                if(lesson == null){
                    return notFound(LessonResult.lessonNotFound().asJson());
                }
                if (!requester.email.equals(lesson.author)){
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                jpaApi.withTransaction(em -> Lesson.remove(em, lesson));

                return ok(LessonResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }

        }, ec.current());
    }
}
