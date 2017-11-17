package org.storm.periscope.repo;

import org.storm.periscope.domain.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserJdbcRepo implements UserRepo {
    private final DataSource _dataSource;

    public UserJdbcRepo(DataSource dataSource){
        _dataSource = dataSource;
    }

    private Connection getConnection(){
        try {
            return _dataSource.getConnection();
        } catch(SQLException se){
            throw new RuntimeException("failed to get DB connection", se);
        }
    }

    private final String SELECT = "SELECT u.username, u.password, " +
            "p.enabled, p.expired, p.locked " +
            "FROM periscope.users u " +
            "JOIN periscope.policy p ON u.id = p.id " +
            "WHERE u.username = ? " +
            "LIMIT 1";

    @Override
    public User findByUsername(final String username) {
        try (PreparedStatement stmt = getConnection().prepareStatement(SELECT)){
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    User.Builder user = User.builder();
                    user.username(rs.getString("username"));
                    user.password(rs.getString("password"));
                    user.enabled(rs.getBoolean("enabled"));
                    user.expired(rs.getBoolean("expired"));
                    user.locked(rs.getBoolean("locked"));
                    return user.build();
                }
            }
        } catch (SQLException se){
            throw new RuntimeException("user lookup failed", se);
        }
        return null;
    }

    @Override
    public User add(User user) {
        return null;
    }

    @Override
    public boolean remove(User user) {
        return false;
    }

    @Override
    public boolean contains(User user) {
        return false;
    }
}
