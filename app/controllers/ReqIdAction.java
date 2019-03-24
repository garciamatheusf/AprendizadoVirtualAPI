package controllers;

import org.slf4j.Marker;
import play.mvc.Http;
import play.mvc.Result;
import util.LogMarkersFacade;

import java.util.concurrent.CompletionStage;

public class ReqIdAction extends play.mvc.Action.Simple{

    public final static String logMarkerKey = "logMarkerKey";
    public final static String REQ_UNIQUE_ID = "reqUniqueId";

    @Override
    public CompletionStage<Result> call(Http.Context context) {
        Marker reqIdMarker = LogMarkersFacade.createReqIdMarker(context.request().getHeaders().get(REQ_UNIQUE_ID).get());
        context.args.put(logMarkerKey, reqIdMarker);
        return delegate.call(context);
    }

    public static Marker getReqIdFromContext(Http.Context ctx){
        return (Marker) ctx.args.get(logMarkerKey);
    }
}