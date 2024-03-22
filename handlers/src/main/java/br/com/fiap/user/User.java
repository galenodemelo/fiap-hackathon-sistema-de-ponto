package br.com.fiap.user;

import br.com.fiap.util.DatabaseConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class User {

    private Long id;
    private String name;
    private String email;
    private String password;

    public static User findByEmail(String email) throws SQLException {
        PreparedStatement statement = DatabaseConnection.getConnection().prepareStatement(
            "SELECT * FROM user WHERE email = ?"
        );
        statement.setString(1, email);
        statement.setMaxRows(1);

        ResultSet result = statement.executeQuery();
        if (!result.next()) return null;

        User user = new User();
        user.setId(result.getLong("id"));
        user.setName(result.getString("name"));
        user.setEmail(result.getString("email"));
        user.setPassword(result.getString("password"));

        statement.close();

        return user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
