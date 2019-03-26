package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.Usuario;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.slf4j.Marker;
import play.Logger;
import play.db.jpa.JPAApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import util.Formatter;
import util.JsonValidation;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.Date;
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
            //RESTRINGIR ESTE MÉTODO
            Marker reqIdMarker = ReqIdAction.getReqIdFromContext(ctx());
            appLogger.info("Listagem de todos usuarios solicitada.");


            //ArrayList<User> users = select do banco
            return ok(UserResult.sucess().asJson()); //tem que retornar uma lista
        }, ec.current());
    }

    public CompletionStage<Result> getUser(String email){
        return CompletableFuture.supplyAsync(() -> {
            appLogger.info("Informacoes de usuario solicitado. Informacoes recebidas {}", email);

            if(StringUtils.isBlank(email)){
                return badRequest();
            }

            Usuario user = jpaApi.withTransaction(em -> Usuario.getByEmail(em, email));

            if(user == null){
                return notFound(UserResult.userNotFound().asJson());
            }

            return ok(UserResult.userFound(user).asJson());
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

            if(jpaApi.withTransaction(em -> Usuario.getByEmail(em, bodyNode.get("email").asText())) != null){
                return forbidden(UserResult.mailAlreadyRegistered().asJson(reqIdMarker, true));
            }

            Usuario user = new Usuario();
            try {
                user.dataNasc = Formatter.stringToDate(bodyNode.get("dataNascimento").asText());
            } catch (ParseException e) {
                appLogger.error("Erro na conversao da data de nascimento");
                return badRequest(UserResult.bornDateInvalid().asJson());
            }
            user.email = bodyNode.get("email").asText();
            user.senha = bodyNode.get("senha").asText();
            user.nome = bodyNode.get("nome").asText();
            user.sobrenome = bodyNode.get("sobrenome").asText();

            user.isAluno = bodyNode.get("isAluno").asBoolean();
            jpaApi.withTransaction(em -> Usuario.insertWithQuery(em, user));
            /*
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
