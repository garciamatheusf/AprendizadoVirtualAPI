package controllers.account;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import controllers.recovery.RecoveryResult;
import models.entities.RecoveryPassword;
import models.entities.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Marker;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import util.JsonValidation;

import javax.inject.Inject;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AccountController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;
    @Inject
    private JPAApi jpaApi;

    public CompletionStage<Result> login(){
        return CompletableFuture.supplyAsync(() -> {
            try{
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();
                appLogger.info("Login solicitado. Informacoes recebidas {}", bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "password");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                String email = bodyNode.get("email").asText();
                String password = bodyNode.get("password").asText();

                User user = jpaApi.withTransaction(em -> User.getByEmail(em, email));
                if(user == null){
                    return unauthorized(AccountResult.accountNotFound().asJson(reqIdMarker, true));
                }

                if(!user.password.equals(DigestUtils.sha1Hex(password))){
                    return unauthorized(AccountResult.wrongPassword().asJson(reqIdMarker, true));
                }

                String token = DigestUtils.sha1Hex(email + "cl45SR0oM" + new Date());
                user.token = token;
                jpaApi.withTransaction(em -> User.update(em, user));

                return ok(AccountResult.sucessLogin(token).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> resetPassword(){
        return CompletableFuture.supplyAsync(() -> {
            try{
                Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
                JsonNode bodyNode = request().body().asJson();

                appLogger.info("Resetar senha solicitado. Informacoes recebidas {}", bodyNode);

                try {
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "temppassword", "newpassword");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                RecoveryPassword recoveryPassword = jpaApi.withTransaction(em -> RecoveryPassword.getByEmail(em, bodyNode.get("email").asText()));
                if(recoveryPassword == null){
                    return notFound(RecoveryResult.recoveryNotFound().asJson());
                }

                if(!recoveryPassword.temppassword.equals(bodyNode.get("temppassword").asText())){
                    return forbidden(AccountResult.wrongTempPassword().asJson());
                }

                User requester = jpaApi.withTransaction(em -> User.getByEmail(em, bodyNode.get("email").asText()));
                requester.password = DigestUtils.sha1Hex(bodyNode.get("newpassword").asText());

                jpaApi.withTransaction(em -> User.update(em, requester));
                jpaApi.withTransaction(em -> RecoveryPassword.remove(em, recoveryPassword));

                return ok(AccountResult.sucessRequestReset().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }
}
