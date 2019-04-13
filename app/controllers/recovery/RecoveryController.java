package controllers.recovery;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.RecoveryPassword;
import models.entities.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Marker;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import services.EmailSender;
import util.JsonValidation;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class RecoveryController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;
    @Inject
    private JPAApi jpaApi;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            try {
                //RESTRINGIR ESTE MÉTODO
                appLogger.info("Listagem de todas as solicitacoes de recuperacao de senha.");

                ArrayList<RecoveryPassword> recoveryPasswords = jpaApi.withTransaction(RecoveryPassword::getAll);
                return ok(RecoveryResult.sucess(recoveryPasswords).asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    public CompletionStage<Result> createRecovery(){
        return CompletableFuture.supplyAsync(() -> {
            if (!request().getHeaders().contains("token")) {
                return badRequest(DefaultResult.requestWithoutToken().asJson());
            }
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            String token = request().getHeaders().get("token").get();
            appLogger.info("Pedido de recuperacao de senha solicitado. Informacoes recebidas {} - {}", token, bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
            if(requester == null){
                return forbidden(DefaultResult.invalidToken().asJson());
            }
            if(!requester.email.equals(bodyNode.get("email").asText())){
                return unauthorized(DefaultResult.withoutPermission().asJson());
            }

            RecoveryPassword recoveryPassword = new RecoveryPassword();
            recoveryPassword.email = bodyNode.get("email").asText();
            recoveryPassword.temppassword = createTempPassword();

            if(jpaApi.withTransaction(em-> RecoveryPassword.getByEmail(em, recoveryPassword.email)) != null){
                return forbidden(RecoveryResult.recoveryAlreadySolicited().asJson());
            }

            jpaApi.withTransaction(() -> RecoveryPassword.insertWithObject(jpaApi.em(), recoveryPassword));

            EmailSender.recoveryEmail(recoveryPassword.email, recoveryPassword.temppassword);

            return ok(RecoveryResult.sucess().asJson());

        }, ec.current());
    }

    private String createTempPassword(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().substring(0, 8);
    }

    public CompletionStage<Result> deleteRecovery(){
        return CompletableFuture.supplyAsync(() -> {
            if (!request().getHeaders().contains("token")) {
                return badRequest(DefaultResult.requestWithoutToken().asJson());
            }
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            String token = request().getHeaders().get("token").get();
            appLogger.info("Delete de recuperacao de senha solicitado. Informacoes recebidas {} - {}", token, bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "temppassword", "newpassword");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            User requester = jpaApi.withTransaction(em -> User.getByToken(em, token));
            if(requester == null){
                return forbidden(DefaultResult.invalidToken().asJson());
            }
            if(!requester.email.equals(bodyNode.get("email").asText())){
                return unauthorized(DefaultResult.withoutPermission().asJson());
            }

            RecoveryPassword recoveryPassword = jpaApi.withTransaction(em -> RecoveryPassword.getByEmail(em, bodyNode.get("email").asText()));
            if(recoveryPassword == null){
                return notFound(RecoveryResult.recoveryNotFound().asJson());
            }

            requester.password = DigestUtils.sha1Hex(bodyNode.get("newpassword").asText());
            jpaApi.withTransaction(em -> User.update(em, requester));
            jpaApi.withTransaction(em -> RecoveryPassword.remove(em, recoveryPassword));

            return ok(RecoveryResult.sucess().asJson());

        }, ec.current());
    }
}
