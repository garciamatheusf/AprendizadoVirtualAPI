package controllers.account;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import org.slf4j.Marker;
import play.Logger;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import util.JsonValidation;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AccountController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;

    public CompletionStage<Result> login(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Login solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senha");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            /*
            if(naoTemLoginComEsteEmailESenha){
                return unauthorized(AccountResult.loginError().asJson(reqIdMarker, true));
            }
            */
            return ok(AccountResult.sucess().asJson()); //Tem que devolver o token gerado
        }, ec.current());
    }

    public CompletionStage<Result> resetPassword(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Resetar senha solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senhaTemp", "novaSenha");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            /*
            if(naoExisteSolicitaoDeResetComEsteEmail){
                return badRequest(AccountResult.solicitationNotFound().asJson(reqIdMarker, true));
            }
            if(senhaTemp != do banco){
                return forbidden(AccountResult.tempPasswordWrong().asJson(reqIdMarker, true));
            }

            define senha do e-mail com valor de novaSenha
             */

            return ok(AccountResult.sucess().asJson());
        }, ec.current());
    }
}
