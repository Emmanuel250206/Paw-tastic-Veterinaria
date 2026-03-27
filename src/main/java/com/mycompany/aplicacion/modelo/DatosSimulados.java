/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.modelo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatosSimulados {
     private static ObservableList<Mascota> mascotas = null;
    
    public static ObservableList<Mascota> getMascotas() {
        if (mascotas == null) {
            cargarDatos();
        }
        return mascotas;
    }
    
    private static void cargarDatos() {
        mascotas = FXCollections.observableArrayList();
        
        // Mascota 1
        Mascota m1 = new Mascota(1, "Max", "Perro", "Labrador", 5,
                "NFC001", "Juan Pérez", "555-1234", "Av. Principal 123",
                "=== HISTORIAL CLÍNICO DE MAX ===\n\n" +
                "15/03/2025 - Consulta programada\n" +
                "Diagnóstico: Otitis externa\n" +
                "Tratamiento: Gotas óticas por 7 días\n\n" +
                "10/01/2025 - Vacunación anual\n" +
                "Aplicada: Antirrábica y Parvovirus\n\n" +
                "05/08/2024 - Urgencia\n" +
                "Diagnóstico: Gastroenteritis\n" +
                "Tratamiento: Dieta blanda y antibiótico por 5 días");
        
        // Mascota 2
        Mascota m2 = new Mascota(2, "Luna", "Gato", "Siamés", 3,
                "NFC002", "María García", "555-5678", "Calle Los Pinos 45",
                "=== HISTORIAL CLÍNICO DE LUNA ===\n\n" +
                "20/03/2025 - Consulta programada\n" +
                "Diagnóstico: Infección urinaria\n" +
                "Tratamiento: Antibiótico por 10 días\n\n" +
                "15/11/2024 - Esterilización\n" +
                "Procedimiento: Ovariohisterectomía\n" +
                "Recuperación sin complicaciones");
        
        // Mascota 3
        Mascota m3 = new Mascota(3, "Rocky", "Perro", "Bulldog Inglés", 7,
                "NFC003", "Pedro Díaz", "555-9012", "Av. Las Palmas 789",
                "=== HISTORIAL CLÍNICO DE ROCKY ===\n\n" +
                "10/03/2025 - Urgencia\n" +
                "Diagnóstico: Dermatitis alérgica\n" +
                "Tratamiento: Antihistamínico y shampoo medicado\n\n" +
                "22/12/2024 - Control\n" +
                "Diagnóstico: Alergia estacional controlada\n\n" +
                "05/06/2024 - Consulta\n" +
                "Diagnóstico: Otitis crónica\n" +
                "Tratamiento: Limpieza semanal y gotas");
        
        // Mascota 4
        Mascota m4 = new Mascota(4, "Nala", "Gato", "Persa", 2,
                "NFC004", "Laura Méndez", "555-3456", "Calle Sol 234",
                "=== HISTORIAL CLÍNICO DE NALA ===\n\n" +
                "01/03/2025 - Consulta programada\n" +
                "Diagnóstico: Control anual\n" +
                "Vacunas: Al día\n" +
                "Estado: Saludable\n\n" +
                "10/10/2024 - Vacunación\n" +
                "Aplicada: Triple felina");
        
        // Mascota 5
        Mascota m5 = new Mascota(5, "Toby", "Perro", "Chihuahua", 4,
                "NFC005", "Carlos Ruiz", "555-7890", "Av. Central 567",
                "=== HISTORIAL CLÍNICO DE TOBY ===\n\n" +
                "05/03/2025 - Urgencia\n" +
                "Diagnóstico: Luxación de rótula\n" +
                "Tratamiento: Antiinflamatorios y reposo\n\n" +
                "18/08/2024 - Consulta\n" +
                "Diagnóstico: Problemas dentales\n" +
                "Tratamiento: Limpieza dental y antibiótico");
        
        mascotas.addAll(m1, m2, m3, m4, m5);
    }
    
    // Buscar por ID de collar (exacto)
    public static Mascota buscarPorCollar(String idCollar) {
        for (Mascota m : getMascotas()) {
            if (m.getIdCollar().equalsIgnoreCase(idCollar)) {
                return m;
            }
        }
        return null;
    }
    
    // Buscar por nombre (coincidencia parcial)
    public static ObservableList<Mascota> buscarPorNombre(String texto) {
        ObservableList<Mascota> resultados = FXCollections.observableArrayList();
        if (texto == null || texto.isEmpty()) {
            return getMascotas();
        }
        for (Mascota m : getMascotas()) {
            if (m.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                resultados.add(m);
            }
        }
        return resultados;
    }
}
