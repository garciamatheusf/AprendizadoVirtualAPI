package controllers.recovery;

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

public class RecoveryController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");
    @Inject
    HttpExecutionContext ec;

    public CompletionStage<Result> listAll(){
        return CompletableFuture.supplyAsync(() -> {
            //RESTRINGIR ESTE METODO
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            appLogger.info("Listagem de recuperacoes de senha solicitado.");

            //ArrayList<User> users = select do banco
            return ok(RecoveryResult.sucess().asJson()); //tem que retornar uma lista

        }, ec.current());
    }

    public CompletionStage<Result> createRecovery(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Pedido de recuperacao de senha solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senha");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }
            return ok(RecoveryResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> deleteRecovery(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Delete de recuperacao de senha solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senha");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }
            return ok(RecoveryResult.sucess().asJson());

        }, ec.current());
    }
}
