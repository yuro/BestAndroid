package com.mock;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

/**
 * Created by wallace on 14-2-26.
 */
public abstract class MockHttpRunner {
    public abstract MockResult handle(String realUrl, HttpRequest httpRequest, HttpResponse httpResponse, HttpContext httpContext);
}
