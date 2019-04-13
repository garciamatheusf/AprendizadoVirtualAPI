package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.User;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Marker;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.EmailSender;
import util.Formatter;
import util.JsonValidation;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class UserController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");

    @Inject
    HttpExecutionContext ec;
    @Inject
    private JPAApi jpaApi;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                //RESTRINGIR ESTE MÉTODO
                appLogger.info("Listagem de todos usuarios solicitada.");

                ArrayList<User> users = jpaApi.withTransaction(User::getAll);
                return ok(UserResult.sucess(users).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> getUserByEmail(String email){
        return CompletableFuture.supplyAsync(() -> {
            try {
                appLogger.info("Informacoes de usuario solicitado. Informacoes recebidas {}", email);

                if (StringUtils.isBlank(email)) {
                    return badRequest(DefaultResult.forBadRequest("Email nao informado").asJson());
                }

                User user = jpaApi.withTransaction(em -> User.getByEmail(em, email));

                if (user == null) {
                    return notFound(UserResult.userNotFound().asJson());
                }
                user.password = null;

                return ok(UserResult.userFound(user).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> getUserByToken(String token){
        return CompletableFuture.supplyAsync(() -> {
            try {
                appLogger.info("Informacoes de usuario solicitado. Informacoes recebidas {}", token);

                if (StringUtils.isBlank(token)) {
                    return badRequest(DefaultResult.forBadRequest("Token nao informado").asJson());
                }

                User user = jpaApi.withTransaction(em -> User.getByToken(em, token));

                if (user == null) {
                    return notFound(UserResult.userNotFound().asJson());
                }
                user.password = null;

                return ok(UserResult.userFound(user).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> createUser(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();
                appLogger.info("Cadastro solicitado. Informacoes recebidas {}", bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "password", "name", "lastname", "borndate", "student");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                if (jpaApi.withTransaction(em -> User.getByEmail(em, bodyNode.get("email").asText())) != null) {
                    return forbidden(UserResult.mailAlreadyRegistered().asJson(reqIdMarker, true));
                }

                User user = new User();
                try {
                    user.borndate = Formatter.stringToDate(bodyNode.get("borndate").asText());
                } catch (ParseException e) {
                    appLogger.error("Erro na conversao da data de nascimento");
                    return badRequest(UserResult.bornDateInvalid().asJson());
                }
                user.email = bodyNode.get("email").asText();
                user.password = DigestUtils.sha1Hex(bodyNode.get("password").asText());
                user.name = bodyNode.get("name").asText();
                user.lastname = bodyNode.get("lastname").asText();

                user.student = bodyNode.get("student").asBoolean();

                jpaApi.withTransaction(() -> User.insertWithObject(jpaApi.em(), user));

                EmailSender.welcomeEmail(user.email);

                return ok(UserResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> updateUser(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Alteracao de infos de usuario solicitada. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                if (!requester.email.equals(bodyNode.get("email").asText())) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                try {
                    updateInfos(bodyNode, requester);
                } catch (ParseException ex) {
                    appLogger.error("Erro na conversao da data de nascimento");
                    return badRequest(UserResult.bornDateInvalid().asJson());
                }

                jpaApi.withTransaction(em -> User.update(em, requester));

                return ok(UserResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    private void updateInfos(JsonNode bodyNode, User solicitante) throws ParseException {
        if(bodyNode.has("name")){
            solicitante.name = bodyNode.get("name").asText();
        }
        if(bodyNode.has("lastname")){
            solicitante.lastname = bodyNode.get("lastname").asText();
        }
        if(bodyNode.has("password")){
            solicitante.password = DigestUtils.sha1Hex(bodyNode.get("password").asText());
        }
        if(bodyNode.has("student")){
            solicitante.student = bodyNode.get("student").asBoolean();
        }
        if(bodyNode.has("borndate")){
            solicitante.borndate = Formatter.stringToDate(bodyNode.get("borndate").asText());
        }
    }

    public CompletionStage<Result> deleteUser(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!request().getHeaders().contains("token")) {
                    return badRequest(DefaultResult.requestWithoutToken().asJson());
                }
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                String token = request().getHeaders().get("token").get();
                appLogger.info("Delete de usuario solicitado. Informacoes recebidas {} - {}", token, bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
                if(requester == null){
                    return forbidden(DefaultResult.invalidToken().asJson());
                }
                if (!requester.email.equals(bodyNode.get("email").asText())) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                jpaApi.withTransaction(em -> User.remove(em, requester));

                return ok(UserResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }
}
