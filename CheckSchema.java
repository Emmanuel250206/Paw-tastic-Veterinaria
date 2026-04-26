import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class CheckSchema {
    public static void main(String[] args) {
        String cadena = "jdbc:mysql://localhost:3306/pawtastic?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conectar = DriverManager.getConnection(cadena, "root", "");
            Statement st = conectar.createStatement();
            
            // Check columns
            ResultSet rs = st.executeQuery("SELECT * FROM tb_usuarios LIMIT 1");
            ResultSetMetaData rsmd = rs.getMetaData();
            int numCols = rsmd.getColumnCount();
            boolean hasUsuario = false;
            for (int i = 1; i <= numCols; i++) {
                String colName = rsmd.getColumnName(i);
                System.out.println(colName + " - " + rsmd.getColumnTypeName(i));
                if (colName.equalsIgnoreCase("usuario")) {
                    hasUsuario = true;
                }
            }
            rs.close();

            if (!hasUsuario) {
                System.out.println("Adding 'usuario' column...");
                st.executeUpdate("ALTER TABLE tb_usuarios ADD COLUMN usuario VARCHAR(50)");
                System.out.println("Column 'usuario' added successfully.");
                
                // Populate existing users with generated username
                rs = st.executeQuery("SELECT id, nombre, apellidos FROM tb_usuarios");
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String n = rs.getString("nombre");
                    String a = rs.getString("apellidos");
                    if (n != null && a != null && !n.isEmpty() && !a.isEmpty()) {
                        String f = n.trim().substring(0, 1).toLowerCase();
                        String l = a.trim().split(" ")[0].toLowerCase();
                        String u = f + l;
                        Statement stUpd = conectar.createStatement();
                        stUpd.executeUpdate("UPDATE tb_usuarios SET usuario = '" + u + "' WHERE id = " + id);
                        stUpd.close();
                    }
                }
                System.out.println("Existing users updated with 'usuario'.");
            }
            
            conectar.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
