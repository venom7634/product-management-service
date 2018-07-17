package com.example.productmanagementservice.database.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UsersRepository {

    @Update("UPDATE users SET token = #{token} WHERE login = #{login}")
     void addTokenInDatabase(@Param("token") String token, @Param("login") String login);
}
