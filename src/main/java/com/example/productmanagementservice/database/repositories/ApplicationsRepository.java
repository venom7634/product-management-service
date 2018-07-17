package com.example.productmanagementservice.database.repositories;

import com.example.productmanagementservice.entity.Application;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ApplicationsRepository {

    @Insert("INSERT INTO applications " +
            "(client_id, status, product, limit_on_card, amount, time_in_month, description) " +
            "VALUES (#{id}, #{status}, null, null, null, null, null)")
    void createNewApplicationInDatabase(@Param("id") long idUser, @Param("status") int status);

    @Select("select * from applications where id = #{id}")
    List<Application> getApplicationsById(@Param("id") long idApplication);

    @Select("select * from applications where id = #{idApplication} and status = #{status}")
    List<Application> getApplicationsByIdAndStatus(@Param("idApplication") long idApplication, @Param("status") int status);

    @Select("select * from applications where id = #{id} and client_id = #{userId}")
    List<Application> getUserApplicationsById(@Param("id") long idApplication, @Param("userId") long userId);

    @Select("select * from applications where client_id = #{userId} AND status = #{status}")
    List<Application> getAllClientApplications(@Param("userId") long userId, @Param("status") int status);

    @Update("UPDATE applications SET status = #{status} WHERE id = #{id}")
    void sendApplicationToConfirmation(@Param("id") long idApplication, @Param("status") int status);

    @Select("select applications.id, client_id, status, product, limit_on_card as limit, amount, time_in_month " +
            "from applications " +
            "INNER JOIN users ON client_id = users.id " +
            "where client_id = #{userId} AND status = #{status}")
    List<Application> getListSentApplicationsOfDataBase(@Param("userId") long userId, @Param("status") int status);

    @Select("select * from applications where client_id = #{userId} and status = #{status}")
    List<Application> getListApprovedApplicationsOfDatabase(@Param("userId") long userId, @Param("status") int status); // params Application.status.APPROVED.ordinal(

    @Update("UPDATE applications SET status = #{status}, description = 'Approved' WHERE id = #{idApplication}")
    void approveApplication(@Param("idApplication") long idApplication, @Param("status") int status);

    @Update("UPDATE applications SET status = #{status}, " +
            "description = 'One user can have only one product of the same type' WHERE product = #{product}")
    void setNegativeOfAllIdenticalProducts(@Param("product") String product, @Param("status") int status);

    @Update("UPDATE applications SET status = #{status}, description = #{reason} WHERE id = #{idApplication}")
    void negativeApplication(@Param("idApplication") long idApplication, @Param("reason") String reason,
                             @Param("status") int status);

    @Select("select * from applications where product = #{product} and status = #{status}")
    List<Application> getApplicationsByValues(@Param("product") String product, @Param("status") int status);
}
