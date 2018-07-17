package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface UsersRepository {

    @Update("UPDATE users SET token = #{token} WHERE login = #{login}")
    void addTokenInDatabase(@Param("token") String token, @Param("login") String login);

    @Select("SELECT * FROM users WHERE login = #{login}")
    List<User> getUsersByLogin(@Param("login") String login);

    @Select("SELECT * FROM users WHERE token = #{token}")
    List<User> getUsersByToken(@Param("token") String token);

    @Select("select users.id, login, password, token, security, users.name, users.description " +
            "from users JOIN applications ON users.id = client_id where applications.id = #{id}")
    List<User> getUsersByIdApplication(@Param("id") long idApplication);
}
