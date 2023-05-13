package com.tech.learningspace.consumers.dao;

import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.consumers.Request.AddConsumerRequest;
import com.tech.learningspace.consumers.Response.ConsumerResponse;
import com.tech.learningspace.dao.AbstractDao;
import com.tech.learningspace.enums.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static com.tech.learningspace.enums.Status.ACTIVE;

@Repository
public class ConsumersDao extends AbstractDao {

    private final String CONSUMER_TABLE="consumers";
    private final String FIRST_NAME="first_name";
    private final String LAST_NAME="last_name";
    private final String CITY="city";
    private final String STATE="state";
    private final String ADDRESS="address";
    private final String MOBILE_NUMBER="mobile_number";
    private final String EMAIL="email";
    private final String STATUS="status";
    private final String USERID="user_id";
    private final String TENANT_ID="tenant_id";
    private final String COMMA=",";
    private final String INSERT_CONSUMER="INSERT INTO "+CONSUMER_TABLE+" ("
            +FIRST_NAME+COMMA
            +LAST_NAME+COMMA
            +USERID+COMMA
            +MOBILE_NUMBER+COMMA
            +EMAIL+COMMA
            +ADDRESS+COMMA
            +STATE+COMMA
            +CITY+COMMA
            +TENANT_ID+COMMA
            +STATUS
            +") values (:first_name,:last_name,:user_id,:mobile_number,:email,:address,:state,:city,:tenant_id,:status)";

    private final String SELECT_CONSUMER="SELECT * FROM "+CONSUMER_TABLE+" WHERE user_id=:user_id and tenant_id=:tenant_id";

    @Autowired
    public ConsumersDao(DataSource dataSource, @Value("${db.pool.size}") int noOfThreads) {
        super(dataSource, noOfThreads, ConsumersDao.class.getSimpleName());
    }

    public CompletionStage<Integer>addConsumer(AddConsumerRequest consumerRequest, Long userId){
        return updateAsync(INSERT_CONSUMER,getAddConsumerParams(consumerRequest,userId))
                .thenApply(
                        num->{
                         if (num==0){
                             throw new LearningSpaceException("Error creating Consumer", ErrorCode.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                         }
                         return num;
                        });
    }

    public CompletionStage<Optional<ConsumerResponse>>getConsumerByUserId(Long userId, Long tenant_id){
        HashMap<String,Object>param=new HashMap<>();
        param.put(USERID,userId);
        param.put(TENANT_ID,tenant_id);
        return queryOptionalRowAsync(SELECT_CONSUMER,(resultSet,num)->getConsumerMapper(resultSet),param);
    }

    public ConsumerResponse getConsumerMapper(ResultSet resultSet) throws SQLException {
        return ConsumerResponse.builder()
                .firstName(resultSet.getString(FIRST_NAME))
                .lastName(resultSet.getString(LAST_NAME))
                .userId(resultSet.getString(USERID))
                .mobileNumber(resultSet.getString(MOBILE_NUMBER))
                .email(resultSet.getString(EMAIL))
                .address(resultSet.getString(ADDRESS))
                .city(resultSet.getString(CITY))
                .state(resultSet.getString(STATE))
                .tenantId(resultSet.getString(TENANT_ID))
                .status(resultSet.getString(STATUS))
                .build();
    }

    public Map<String,Object>getAddConsumerParams(AddConsumerRequest consumerRequest,Long userId){
        Map<String,Object>param=new HashMap<>();
        param.put(FIRST_NAME,consumerRequest.getFirstName());
        param.put(LAST_NAME,consumerRequest.getLastName());
        param.put(ADDRESS,consumerRequest.getAddress());
        param.put(CITY,consumerRequest.getCity());
        param.put(STATE,consumerRequest.getState());
        param.put(MOBILE_NUMBER,consumerRequest.getMobileNumber());
        param.put(EMAIL,consumerRequest.getEmail());
        param.put(TENANT_ID,consumerRequest.getTenantId());
        param.put(USERID,userId);
        param.put(STATUS,ACTIVE.name());
        return param;

    }

}
