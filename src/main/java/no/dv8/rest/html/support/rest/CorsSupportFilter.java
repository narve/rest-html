package no.dv8.rest.html.support.rest;


import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

public class CorsSupportFilter implements ContainerResponseFilter {

    public CorsSupportFilter() {
        System.err.println("Initing CorsSupportFilter!!!!");
    }

//        @Override
//        public ContainerResponse filter(ContainerRequest req, ContainerResponse contResp) {
//
//            javax.ws.rs.core.Response.ResponseBuilder resp =
//                    javax.ws.rs.core.Response.fromResponse(contResp.getResponse())
//                            .header("Access-Control-Allow-Origin", "*")
//                            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//
//            String reqHead = req.getHeaderString("Access-Control-Request-Headers");
//
//            if (null != reqHead && !reqHead.equals(null)) {
//                resp.header("Access-Control-Allow-Headers", reqHead);
//            }
//
////            contResp.setResponse(resp.build());
//            return contResp;
//        }

    @Override
    public void filter(ContainerRequestContext reqCtx, ContainerResponseContext resCtx) throws IOException {
//        resCtx.setStatus(200);
        resCtx.getHeaders().putSingle("Access-Control-Allow-Origin", "*");
        resCtx.getHeaders().putSingle("Access-Control-Allow-Methods", "*");

        String reqHead = reqCtx.getHeaderString("Access-Control-Request-Headers");
        if (null != reqHead && !reqHead.equals(null)) {
            resCtx.getHeaders().putSingle("Access-Control-Allow-Headers", reqHead);
        }
    }
}
