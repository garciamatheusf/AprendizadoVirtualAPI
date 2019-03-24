package controllers;

public enum DefaultStatusCodes {

    OK(200, "Operacao realizada com sucesso"),
    FORBIDDEN(-403, "Sem permissao para realizar esta operacao"),
    UNAUTHORIZED(-401, "Sem permissao para realizar esta operacao"),
    NOT_FOUND(-404, "Recurso requisitado nao encontrado"),
    BAD_REQUEST(-400, "Requisicao realizada de forma incorreta"),
    INTERNAL_SERVER_ERROR(-500, "Erro interno do sistema ao realizar a consulta");

    private final int code;
    private final String description;

    DefaultStatusCodes(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
