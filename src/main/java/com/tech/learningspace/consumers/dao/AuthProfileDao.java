package com.tech.learningspace.consumers.dao;

import com.tech.learningspace.Exception.LearningSpaceException;
import com.tech.learningspace.consumers.Request.AddAuthProfileRequest;
import com.tech.learningspace.consumers.Response.AuthProfileResponse;
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
public class AuthProfileDao extends AbstractDao {


    private final String MOBILE_NUMBER="mobile_number";
    private final String EMAIL="email";
    private final String STATUS="status";
    private final String USERID="user_id";
    private final String TENANT_ID="tenant_id";
    private final String ROLE="role";
    private final String USERNAME="user_name";
    private final String PASSWORD="password";
    private final String AUTH_PROFILE_TABLE="authprofile";

    private final String ADD_PROFILE="INSERT INTO "+AUTH_PROFILE_TABLE+" (email,mobile_number,user_name,password,tenant_id,role,status) values "+
            "(:email,:mobile_number,:user_name,:password,:tenant_id,:role,:status)";
    private final String SELECT_PROFILE="SELECT * FROM "+AUTH_PROFILE_TABLE+" WHERE (mobile_number=:mobile_number or email=:email) and tenant_id=:tenant_id ";

    @Autowired
    public AuthProfileDao(DataSource dataSource, @Value("${db.pool.size}") int noOfThreads) {
        super(dataSource, noOfThreads,AuthProfileDao.class.getSimpleName());
    }

    public CompletionStage<Integer>addAuthProfile(AddAuthProfileRequest authProfileRequest){
        return updateAsync(ADD_PROFILE,getAddAuthProfileParams(authProfileRequest))
                .thenApply(
                        num->{
                            if (num==0){
                                throw new LearningSpaceException("Error creating authprofile", ErrorCode.DB_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
                            }
                            return num;
                        });
    }

    public CompletionStage<Optional<AuthProfileResponse>>getProfileByMobileOrEmail(String mobileNumber,String email,Long tenant_id){
        HashMap<String,Object> param=new HashMap<>();
        param.put(MOBILE_NUMBER,mobileNumber);
        param.put(EMAIL,email);
        param.put(TENANT_ID,tenant_id);
        return queryOptionalRowAsync(SELECT_PROFILE,(resultSet,num)->getConsumerMapper(resultSet),param);
    }

    public AuthProfileResponse getConsumerMapper(ResultSet resultSet) throws SQLException {
        return AuthProfileResponse.builder()
                .userId(resultSet.getLong(USERID))
                .mobileNumber(resultSet.getString(MOBILE_NUMBER))
                .email(resultSet.getString(EMAIL))
                .tenantId(resultSet.getString(TENANT_ID))
                .status(resultSet.getString(STATUS))
                .build();
    }

    public Map<String,Object> getAddAuthProfileParams(AddAuthProfileRequest authProfileRequest){
        Map<String,Object>param=new HashMap<>();
        param.put(MOBILE_NUMBER,authProfileRequest.getMobileNumber());
        param.put(EMAIL,authProfileRequest.getEmail());
        param.put(USERNAME,authProfileRequest.getUserName());
        param.put(TENANT_ID,authProfileRequest.getTenantId());
        param.put(PASSWORD,authProfileRequest.getPassword());
        param.put(ROLE,authProfileRequest.getRole());
        param.put(STATUS,ACTIVE.name());

        return param;

    }

}
