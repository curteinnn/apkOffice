package com.mycompany.admin.desktop;

/** Menyimpan identitas dan JWT untuk request API berikutnya. */
public final class AuthSession {
    private static AuthResult current;
    private AuthSession() {}
    public static void set(AuthResult value) { current = value; }
    public static AuthResult get() { return current; }
    public static String getToken() { return current == null ? null : current.token; }
    public static void clear() { current = null; }
}
