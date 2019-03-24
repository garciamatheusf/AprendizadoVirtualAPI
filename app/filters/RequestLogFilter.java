package filters;

import akka.stream.Materializer;
import controllers.ReqIdAction;
import play.Logger;
import play.mvc.Filter;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class RequestLogFilter extends Filter {

    private final Logger.ALogger accessLogger = Logger.of("access");

    @Inject
    public RequestLogFilter(Materializer mat) {
        super(mat);
    }

    @Override
    public CompletionStage<Result> apply(Function<Http.RequestHeader, CompletionStage<Result>> nextFilter, Http.RequestHeader requestHeader) {
        Instant startInstant = Instant.now();

        String uniqueID = UUID.randomUUID().toString();
        requestHeader.getHeaders().addHeader(ReqIdAction.REQ_UNIQUE_ID, uniqueID);

        return nextFilter.apply(requestHeader).thenApply(result -> {

            Instant endInstant = Instant.now();
            long requestTime = endInstant.toEpochMilli() - startInstant.toEpochMilli();

            accessLogger.info("{} ID {} from {} took {}ms returned {} - {}",
                    requestHeader.method(),
                    uniqueID,
                    requestHeader.remoteAddress(),
                    requestTime,
                    result.status(),
                    requestHeader.uri());

            return result.withHeader("sh_token", "");
        });
    }
}
