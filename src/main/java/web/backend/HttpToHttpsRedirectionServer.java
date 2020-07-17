package web.backend;

import fi.iki.elonen.NanoHTTPD;
import util.PrintUtil;

import java.io.IOException;

public class HttpToHttpsRedirectionServer extends NanoHTTPD {

    public HttpToHttpsRedirectionServer() throws IOException {
        super(80);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println(PrintUtil.getTime() + " Started http-to-https redirection server");
    }

    @Override
    public Response serve(IHTTPSession session) {
        Response r = newFixedLengthResponse(Response.Status.REDIRECT, MIME_HTML, "");
        r.addHeader("Location", "https://tfe.tools" + session.getUri());
        return r;
    }
}
