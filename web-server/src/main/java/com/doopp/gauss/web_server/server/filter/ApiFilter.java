package com.doopp.gauss.web_server.server.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {

        // 不过滤的uri
        String[] notFilterUris = new String[]{
                "/favicon.ico",
                "/css",
                "/img",
                "/js"
        };

        // 请求的uri
        String uri = request.getRequestURI();

        // 是否过滤
        boolean doFilter = true;

        // 如果uri中包含不过滤的uri，则不进行过滤
        for (String notFilterUri : notFilterUris) {
            if (uri.indexOf(notFilterUri)==0) {
                doFilter = false;
                break;
            }
        }

        try {
            if (doFilter) {

                // if filter
                // ...
                // set request
                // request.setAttribute("request-client", client);
            }
            // response.setHeader("Access-Control-Expose-Headers", "Content-Type,Content-Disposition");
            filterChain.doFilter(request, response);

        }
        catch (Exception e) {
            e.printStackTrace();
            writeExceptionResponse(501, response, e.getMessage());
        }
    }

    private static void writeExceptionResponse(int errorCode, HttpServletResponse response, String errorMessage) throws IOException {
        response.setStatus(200);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write("{\"code\":" + errorCode + ", \"msg\":\"" + errorMessage + "\"}");
    }
}
