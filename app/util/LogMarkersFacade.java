package util;

import net.logstash.logback.marker.Markers;
import org.slf4j.Marker;

/**
 * Created by alissonlima on 01/11/17.
 */
public class LogMarkersFacade {

    public static Marker createReqIdMarker(String id){
        return Markers.append("req_id", id);
    }

    public static Marker createShTokenMarker(String token){
        return Markers.append("sh_token", token);
    }
}
