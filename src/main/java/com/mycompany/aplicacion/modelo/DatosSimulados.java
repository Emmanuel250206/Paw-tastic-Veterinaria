/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.aplicacion.modelo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DatosSimulados {
     private static ObservableList<Mascota> mascotas = null;
     private static ObservableList<Citas> citas = null;
     private static ObservableList<Inventario> inventario = null;

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



    public static ObservableList<Citas> getCitas() {
        if (citas == null) {
            cargarCitas();
        }
        return citas;
    }

    private static void cargarCitas() {
        citas = FXCollections.observableArrayList();

        // Cita 1 - NORMAL
        Citas c1 = new Citas(1, "2025-03-30", "10:00", "Max", "Juan Pérez", "555-1234",
                "Ana López", "Consulta General", "Programada", Citas.Prioridad.NORMAL);

        // Cita 2 - NORMAL (ya completada)
        Citas c2 = new Citas(2, "2025-03-30", "11:30", "Luna", "María García", "555-5678",
                "Carlos Martínez", "Infección urinaria", "Completada", Citas.Prioridad.NORMAL);

        // Cita 3 - URGENTE
        Citas c3 = new Citas(3, "2025-03-31", "09:00", "Rocky", "Pedro Díaz", "555-9012",
                "Ana López", "Reacción alérgica severa", "Programada", Citas.Prioridad.URGENTE);

        // Cita 4 - NORMAL
        Citas c4 = new Citas(4, "2025-04-01", "16:00", "Nala", "Laura Méndez", "555-3456",
                "Carlos Martínez", "Revisión dental", "Programada", Citas.Prioridad.NORMAL);

        // Cita 5 - URGENTE
        Citas c5 = new Citas(5, "2025-04-02", "08:00", "Toby", "Carlos Ruiz", "555-7890",
                "Ana López", "Luxación - dolor agudo", "Programada", Citas.Prioridad.URGENTE);

        citas.addAll(c1, c2, c3, c4, c5);
    }

    
    // DATOS SIMULADOS INVENTARIO
    
    public static ObservableList<Inventario> getInventario() {
        if (inventario == null) {
            cargarInventario();
        }
        return inventario;
    }
    
    private static void cargarInventario() {
        inventario = FXCollections.observableArrayList();
        
        Inventario i1 = new Inventario(1, "Vacuna Antirrábica", "Medicamento", "Vacuna anual", 50, 10, "Dosis", 150.0, 250.0, "2026-12-31", 1);
        Inventario i2 = new Inventario(2, "Alimento Premium 5kg", "Comida", "Alimento seco para perros", 20, 5, "Bolsa", 300.0, 450.0, "2027-05-20", 2);
        Inventario i3 = new Inventario(3, "Collar Antipulgas", "Accesorios", "Collar para perros", 15, 5, "Unidad", 150.0, 300.0, "2030-01-01", 3);
        Inventario i4 = new Inventario(4, "Antibiótico Amoxicilina", "Medicamento", "Uso general", 100, 20, "Caja", 80.0, 150.0, "2025-10-15", 1);
        Inventario i5 = new Inventario(5, "Shampoo Medicado", "Higiene", "Shampoo para dermatitis", 30, 10, "Botella", 100.0, 200.0, "2026-08-01", 4);
        
        inventario.addAll(i1, i2, i3, i4, i5);
    }
}
