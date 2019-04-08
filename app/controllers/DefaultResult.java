package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import play.Logger;
import play.libs.Json;
import util.JsonValidation;

public class DefaultResult {

    private final static Logger.ALogger appLogger = Logger.of("app");

    protected ObjectNode objectNode;

    protected DefaultResult(int status) {
        initObjectNode(status);
    }

    protected DefaultResult(int status, String msg) {
        initObjectNode(status, msg);
    }

    public DefaultResult(DefaultStatusCodes defaultStatusCodes) {
        initObjectNode(defaultStatusCodes.getCode(), defaultStatusCodes.getDescription());
    }

    private void initObjectNode(int status){
        objectNode = Json.newObject();
        objectNode.put("status", status);
    }

    private void initObjectNode(int status, String msg){
        initObjectNode(status);
        objectNode.put("motivo", msg);
    }

    public JsonNode asJson() {
        return asJson(null, false);
    }

    public JsonNode asJson(Marker reqIdMarker, boolean logJson) {
        JsonNode asJson = Json.toJson(objectNode);

        if (logJson){
            appLogger.info(reqIdMarker, "JSON de retorno gerado: {}", asJson);
        }

        return asJson;
    }

    public static DefaultResult resultForException(Throwable e){
        DefaultResult defaultResult = new DefaultResult(DefaultStatusCodes.INTERNAL_SERVER_ERROR.getCode(),
                e.getMessage());

        appLogger.warn("Retorno com erro padrao: {}", defaultResult.objectNode);

        return defaultResult;
    }

    public static DefaultResult forRequiredInfoNotFilled(JsonValidation.RequiredJsonFieldsNotFilledException e){
        return forRequiredInfoNotFilled(DefaultStatusCodes.BAD_REQUEST.getCode(), e);
    }

    public static DefaultResult forRequiredInfoNotFilled(int code, JsonValidation.RequiredJsonFieldsNotFilledException e){
        return new DefaultResult(code, "Campos obrigatorios nao informados: " + StringUtils.join(e.fieldsNotFilledList, ", "));
    }

    public static DefaultResult forRequiredInfoNotFilled(String message){
        return new DefaultResult(-400, message);
    }

    public static DefaultResult forBadRequest(String motivo){
        return new DefaultResult(DefaultStatusCodes.BAD_REQUEST.getCode(), motivo);
    }

    public static DefaultResult requestWithoutToken(){
        return new DefaultResult(DefaultStatusCodes.BAD_REQUEST.getCode(), "Necessario informar o token no cabecalho");
    }

    public static DefaultResult withoutPermission(){
        return new DefaultResult(DefaultStatusCodes.FORBIDDEN.getCode(), DefaultStatusCodes.FORBIDDEN.getDescription());
    }

}
