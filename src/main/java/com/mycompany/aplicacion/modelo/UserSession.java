package com.mycompany.aplicacion.modelo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Singleton que centraliza el estado de sesión del usuario en la aplicación.
 * Almacena el nombre, rol y avatar activo, y ofrece un método utilitario
 * para cargar la imagen de perfil en cualquier ImageView de la UI.
 */
public class UserSession {

    // ═══════════════════════════════════════════
    //  Singleton
    // ═══════════════════════════════════════════
    private static UserSession instance;

    private UserSession() {
        // Valores por defecto de sesión
        userName         = new SimpleStringProperty("Dr. Emmanuel");
        userRole         = new SimpleStringProperty("Veterinario");
        currentAvatarName = new SimpleStringProperty("Ava_huella.png");
    }

    /** Devuelve la única instancia de UserSession (thread-safe básico). */
    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    // ═══════════════════════════════════════════
    //  Propiedades de sesión
    // ═══════════════════════════════════════════

    /** Nombre completo del usuario logueado. */
    private final StringProperty userName;

    /** Rol del usuario (p. ej. "Veterinario", "Staff"). */
    private final StringProperty userRole;

    /** Nombre del archivo de avatar dentro de /images/ (p. ej. "Ava_huella.png"). */
    private final StringProperty currentAvatarName;

    // --- Accessors userName ---
    public StringProperty userNameProperty()       { return userName; }
    public String getUserName()                    { return userName.get(); }
    public void   setUserName(String name)         { userName.set(name); }

    // --- Accessors userRole ---
    public StringProperty userRoleProperty()       { return userRole; }
    public String getUserRole()                    { return userRole.get(); }
    public void   setUserRole(String role)         { userRole.set(role); }

    // --- Accessors currentAvatarName ---
    public StringProperty currentAvatarNameProperty() { return currentAvatarName; }
    public String getCurrentAvatarName()              { return currentAvatarName.get(); }
    public void   setCurrentAvatarName(String name)   { currentAvatarName.set(name); }

    // ═══════════════════════════════════════════
    //  Utilidad de avatar
    // ═══════════════════════════════════════════

    /**
     * Carga la imagen de perfil del avatar activo de la sesión y la aplica
     * al ImageView indicado.
     *
     * <p>La imagen se busca en el classpath bajo {@code /images/<avatarName>}.
     * Si el recurso no existe, el ImageView queda sin imagen (sin lanzar excepción).</p>
     *
     * @param iv El ImageView de destino (puede ser cualquier encabezado de sección).
     */
    public static void loadProfileImage(ImageView iv) {
        if (iv == null) return;

        String avatarName = getInstance().getCurrentAvatarName();
        String path       = "/images/" + avatarName;

        InputStream stream = UserSession.class.getResourceAsStream(path);

        if (stream != null) {
            iv.setImage(new Image(stream));
        } else {
            System.err.println("[UserSession] Recurso de avatar no encontrado: " + path);
        }
    }

    // ═══════════════════════════════════════════
    //  Limpieza de sesión (logout)
    // ═══════════════════════════════════════════

    /**
     * Restablece la instancia Singleton (útil al cerrar sesión para evitar
     * datos residuales entre logins).
     */
    public static void clear() {
        instance = null;
    }
}
