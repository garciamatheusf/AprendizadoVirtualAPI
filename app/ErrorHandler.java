import controllers.DefaultResult;
import controllers.ReqIdAction;
import play.Configuration;
import play.Environment;
import play.Logger;
import play.api.OptionalSourceMapper;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import util.LogMarkersFacade;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public class ErrorHandler extends DefaultHttpErrorHandler {

    private final static Logger.ALogger appLogger = Logger.of("app");

    @Inject
    public ErrorHandler(Configuration configuration, Environment environment,
                        OptionalSourceMapper sourceMapper, Provider<Router> routes) {
        super(configuration, environment, sourceMapper, routes);
    }

    @Override
    protected CompletionStage<Result> onBadRequest(RequestHeader requestHeader, String s) {
        appLogger.warn("Requisicao realizado de forma errada para na URL {} retornou o erro {}",
                requestHeader.uri(),
                s);

        String msg;
        if (s.contains("No content to map")){
            msg = "Requisicao realizada sem conteudo enviado. (" + s + ")";

        } else {
            msg = "Requisicao realizada com conteudo invalido. O parse do conteudo gerou a seguinte excecao: " + s;
        }

        DefaultResult defaultResult = DefaultResult.forBadRequest(msg);
        return CompletableFuture.completedFuture(
                Results.badRequest(defaultResult.asJson(LogMarkersFacade.createReqIdMarker(requestHeader.getHeaders().get(ReqIdAction.REQ_UNIQUE_ID).get()), true))
        );
    }
}