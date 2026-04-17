package application.util;

import application.model.UsuarioModel;

public class UsuarioSessao {

    private static UsuarioModel usuario;

    public static void setUsuario(UsuarioModel u) {
        usuario = u;
    }

    public static UsuarioModel getUsuario() {
        return usuario;
    }

    public static int getId() {
        return usuario != null ? usuario.getId() : 0;
    }

    public static String getNome() {
        return usuario != null ? usuario.getNome() : "";
    }
}