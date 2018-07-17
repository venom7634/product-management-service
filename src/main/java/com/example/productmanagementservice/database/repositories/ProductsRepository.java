package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.products.Product;
import com.example.productmanagementservice.entity.products.Statistic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductsRepository {

    @Select("select * from products where id = #{id}")
    Product getProductOfDataBase(@Param("id") long id);

    @Update("UPDATE applications SET product = 'debit-card', limit_on_card = NULL, amount = null, " +
            "time_in_month = null WHERE id = #{id}")
    void addDebitCardToApplication(@Param("id") long idApplication);

    @Update("UPDATE applications SET product = 'credit-card',amount = null, time_in_month = null," +
            " limit_on_card = #{limitOnCard} WHERE id = #{id}")
    void addCreditCardToApplication(@Param("id") long idApplication, @Param("limitOnCard") int limit);

    @Update("UPDATE applications SET product = 'credit-cash',limit_on_card = null,  amount = #{amount}," +
            "time_in_month = #{timeInMonth} WHERE id = #{id}")
    void addCreditCashToApplication(@Param("id") long idApplication, @Param("amount") int amount,
                                           @Param("timeInMonth") int timeInMonth);

    @Select("SELECT products.id, products.name FROM products " +
            "INNER JOIN applications ON applications.product = products.name " +
            "INNER JOIN users ON users.id = applications.id " +
            "WHERE applications.status = #{status} AND users.id = #{id} ")
    List<Product> getProductsForClient(@Param("status") int status, @Param("id") long userId);

    @Select("SELECT COUNT(id) as count, product FROM applications GROUP BY product, status HAVING status = #{status}")
    List<Statistic> getApprovedStatistics(@Param("status") int status);

    @Select("SELECT COUNT(id) as count, description as reason FROM applications GROUP BY description, status HAVING status = #{status}")
    List<Statistic> getNegativeStatistics(@Param("status") int status);
}
