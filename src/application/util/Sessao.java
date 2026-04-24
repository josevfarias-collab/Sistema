package application.util; 

import application.model.UsuarioModel;

public class Sessao { 
    private static UsuarioModel usuarioLogado;

    public static void setUsuario(UsuarioModel usuario) {
        usuarioLogado = usuario;
    }

    public static UsuarioModel getUsuario() {
        return usuarioLogado;
    }
}