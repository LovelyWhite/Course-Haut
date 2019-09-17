package com.test.course_haut;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

public class Util {
    private static ConcurrentHashMap<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();
    private static String __VIEWSTATE;
    public static OkHttpClient[] clients = new OkHttpClient[9];
    public static List<F__kClass> list = new ArrayList<>();
    private static String name;
    private static String id;
    public static String[] ips = {
            "172.18.254.101",//0
            "172.18.254.111",//1
            "172.18.254.113",//2
            "172.18.254.114",//3
            "172.18.254.115",//4
            "172.18.254.117",//5
            "172.18.254.119",//6
            "172.18.254.121",//7
            "172.18.254.148",//8
    };
    public static ConcurrentHashMap<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }

    public static void setCookieStore(ConcurrentHashMap<String, List<Cookie>> cookieStore) {
        Util.cookieStore = cookieStore;
    }

    public static String get__VIEWSTATE() {
        return __VIEWSTATE;
    }

    public static void set__VIEWSTATE(String __VIEWSTATE) {
        Util.__VIEWSTATE = __VIEWSTATE;
    }

    public static OkHttpClient getClient(int index) {
        return clients[index];
    }

    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        Util.name = name;
    }

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        Util.id = id;
    }
}
