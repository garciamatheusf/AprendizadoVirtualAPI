package controllers.user;

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

public class UserController extends Controller {
    private final static Logger.ALogger appLogger = Logger.of("app");

    @Inject
    HttpExecutionContext ec;

    public CompletionStage<Result> listAll(){
            return CompletableFuture.supplyAsync(() -> {
            //RESTRINGIR ESTE MÉTODO
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            appLogger.info("Listagem de todos usuarios solicitada.");


            //ArrayList<User> users = select do banco
            return ok(UserResult.sucess().asJson()); //tem que retornar uma lista
        }, ec.current());
    }

    public CompletionStage<Result> getUser(String email){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Informacoes de usuario solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            //NAO RETORNAR INFORMAÇÕES PRIVADAS COMO SENHA
            return ok(UserResult.sucess().asJson());
        }, ec.current());
    }

    public CompletionStage<Result> createUser(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Cadastro solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senha", "nome", "sobrenome", "dataNascimento", "isAluno");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }
            /*
            if(emailJaCadastrado){
                return forbidden(UserResult.mailAlreadyRegistered().asJson(reqIdMarker, true));
            }

            ENVIA E-MAIL DE BOAS VINDAS
             */
            return ok(UserResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> updateUser(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Alteracao de infos de usuario solicitada. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "nome", "sobrenome", "dataNascimento", "isAluno", "senha");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            //VERIFICA SE QUEM SOLICITOU FOI O DONO DA CONTA E ALTERA
            return ok(UserResult.sucess().asJson());

        }, ec.current());
    }

    public CompletionStage<Result> deleteUser(){
        return CompletableFuture.supplyAsync(() -> {
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            JsonNode bodyNode = request().body().asJson();
            appLogger.info("Delete de usuario solicitado. Informacoes recebidas {}", bodyNode);

            try {
                JsonValidation.validateRequiredFieldsFilled(bodyNode, "email");
            } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
            }

            //VALIDA SE QUEM SOLICITOU FOI O DONO DA CONTA E DELETA
            return ok(UserResult.sucess().asJson());

        }, ec.current());
    }
}
