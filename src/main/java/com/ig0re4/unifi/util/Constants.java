package com.ig0re4.unifi.util;

public class Constants {
    public static final String LOGIN_ENDPOINT = "api/auth/login";
    public static final String SELF_ENDPOINT = "proxy/network/api/s/#site#/self";
    public static final String X_CSRF_TOKEN = "x-csrf-token";
    public static final String RECONNECT_CMD = "kick-sta";

    public static final String CLIENTS_ENDPOINT = "proxy/network/api/s/#site#/stat/sta";
    public static final String DEVICES_ENDPOINT = "proxy/network/api/s/#site#/stat/device-basic";
    public static final String RECONNECT_ENDPOINT = "proxy/network/api/s/#site#/cmd/stamgr";
    public static final String TRAFFIC_ROUTES_ENDPOINT = "proxy/network/v2/api/site/#site#/trafficroutes";
    public static final String HEALTH_ENDPOINT = "proxy/network/api/s/#site#/stat/health";
    public static final String PORT_FORWARD_ENDPOINT = "proxy/network/api/s/#site#/rest/portforward";

}
