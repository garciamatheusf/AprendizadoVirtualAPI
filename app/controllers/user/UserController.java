package controllers.user;

import com.fasterxml.jackson.databind.JsonNode;
import controllers.DefaultResult;
import controllers.ReqIdAction;
import models.entities.Usuario;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
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

                ArrayList<Usuario> users = jpaApi.withTransaction(Usuario::getAll);
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

                Usuario user = jpaApi.withTransaction(em -> Usuario.getByEmail(em, email));

                if (user == null) {
                    return notFound(UserResult.userNotFound().asJson());
                }
                user.senha = null;

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

                Usuario user = jpaApi.withTransaction(em -> Usuario.getByToken(em, token));

                if (user == null) {
                    return notFound(UserResult.userNotFound().asJson());
                }
                user.senha = null;

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
                    JsonValidation.validateRequiredFieldsFilled(bodyNode, "email", "senha", "nome", "sobrenome", "dataNascimento", "isAluno");
                } catch (JsonValidation.RequiredJsonFieldsNotFilledException e) {
                    return badRequest(DefaultResult.forRequiredInfoNotFilled(e).asJson(reqIdMarker, true));
                }

                if (jpaApi.withTransaction(em -> Usuario.getByEmail(em, bodyNode.get("email").asText())) != null) {
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
                user.senha = DigestUtils.sha1Hex(bodyNode.get("senha").asText());
                user.nome = bodyNode.get("nome").asText();
                user.sobrenome = bodyNode.get("sobrenome").asText();

                user.isAluno = bodyNode.get("isAluno").asBoolean();

                jpaApi.withTransaction(() -> Usuario.insertWithObject(jpaApi.em(), user));
                /*
                ENVIA E-MAIL DE BOAS VINDAS
                 */
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

                Usuario solicitante = jpaApi.withTransaction(em -> Usuario.getByToken(em, token));
                if (!solicitante.email.equals(bodyNode.get("email").asText())) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                try {
                    updateInfos(bodyNode, solicitante);
                } catch (ParseException ex) {
                    appLogger.error("Erro na conversao da data de nascimento");
                    return badRequest(UserResult.bornDateInvalid().asJson());
                }

                jpaApi.withTransaction(em -> Usuario.update(em, solicitante));

                return ok(UserResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }

    private void updateInfos(JsonNode bodyNode, Usuario solicitante) throws ParseException {
        if(bodyNode.has("nome")){
            solicitante.nome = bodyNode.get("nome").asText();
        }
        if(bodyNode.has("sobrenome")){
            solicitante.sobrenome = bodyNode.get("sobrenome").asText();
        }
        if(bodyNode.has("senha")){
            solicitante.senha = DigestUtils.sha1Hex(bodyNode.get("senha").asText());
        }
        if(bodyNode.has("isAluno")){
            solicitante.isAluno = bodyNode.get("isAluno").asBoolean();
        }
        if(bodyNode.has("dataNascimento")){
            solicitante.dataNasc = Formatter.stringToDate(bodyNode.get("dataNascimento").asText());
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

                Usuario solicitante = jpaApi.withTransaction(em -> Usuario.getByToken(em, token));
                if (!solicitante.email.equals(bodyNode.get("email").asText())) {
                    return forbidden(DefaultResult.withoutPermission().asJson());
                }

                jpaApi.withTransaction(em -> Usuario.remove(em, solicitante));

                return ok(UserResult.sucess().asJson());
            }catch(Exception e){
                return internalServerError(DefaultResult.resultForException(e).asJson());
            }
        }, ec.current());
    }
}
