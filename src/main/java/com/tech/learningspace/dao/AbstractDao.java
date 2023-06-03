package com.tech.learningspace.dao;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.Getter;
import org.postgresql.util.PGobject;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutorService;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.tech.learningspace.Utils.CompletableFutureUtils.unwrapCompletionStateException;
import static com.tech.learningspace.Utils.LearningSpaceThreadFactory.createThreadPoolExecutor;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AbstractDao {

    protected static final Gson gson=new Gson();
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcTemplate jdbcTemplate;
    @Getter
    private final ExecutorService executorService;

    private static final Type mapType=new TypeToken<Map<String,String>>(){
    }.getType();

    public AbstractDao(DataSource dataSource, int noOfThreads, String instanceOfClass) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.executorService = createThreadPoolExecutor(instanceOfClass,noOfThreads);
    }

    public CompletionStage<Integer>updateWithGeneratedKeysAsync(String sql, Map<String,?>paramsMap, KeyHolder keyHolder){
        SqlParameterSource parameterSource=new MapSqlParameterSource(paramsMap);
        return supplyAsync(()->namedParameterJdbcTemplate.update(sql,parameterSource,keyHolder),executorService);
    }
    public Integer updateWithGeneratedKeys(String sql, Map<String,?>paramsMap, KeyHolder keyHolder){
        SqlParameterSource parameterSource=new MapSqlParameterSource(paramsMap);
        return namedParameterJdbcTemplate.update(sql,parameterSource,keyHolder);
    }

    public CompletionStage<Integer> updateWithGeneratedKeysWithColumnsAsync(String sql, Map<String,?>paramsMap, KeyHolder keyHolder,String[]column){
        SqlParameterSource parameterSource=new MapSqlParameterSource(paramsMap);
        return supplyAsync(()->namedParameterJdbcTemplate.update(sql,parameterSource,keyHolder,column),executorService);
    }

    public CompletionStage<Integer>updateAsync(String sql, Map<String,?>paramsMap){
        return supplyAsync(()-> update(sql,paramsMap),executorService);
    }

    public Integer update(String sql, Map<String,?>paramsMap){
        return namedParameterJdbcTemplate.update(sql,paramsMap);
    }

    public  <T> CompletionStage<List<T>>queryAsync(String Sql, RowMapper<T> rowMapper,Map<String,?>params){
        return supplyAsync(()-> query(Sql,rowMapper,params),executorService);
    }
    public <T> List<T> query(String sql, RowMapper<T> rowMapper,Map<String,?>params){
      return   namedParameterJdbcTemplate.query(sql,params,rowMapper);
    }

    public <T>CompletionStage<T> queryAsyncWithResultSetExtractor(String sql, ResultSetExtractor<T> rs,Map<String,?>params){
        return supplyAsync(()->query(sql,rs,params),executorService);
    }

    public <T> T queryWithResultSetExtractor(String sql, ResultSetExtractor<T> rs,Map<String,?>params){
        return query(sql,rs,params);
    }
    public <T> T query(String sql, ResultSetExtractor<T> rs,Map<String,?>params){
        return namedParameterJdbcTemplate.query(sql,params,rs);
    }

    public <T>CompletionStage<Optional<T>> queryOptionalRowAsync(String sql,RowMapper<T>rm,Map<String,?>param){
        return querySingleRowAsync(sql,rm,param)
                .thenApply(Optional::ofNullable)
                .exceptionally(t->{
                    t= unwrapCompletionStateException(t);
                    if (t instanceof EmptyResultDataAccessException){
                        return Optional.empty();
                    }
                    throw new CompletionException(t);
                });
    }

    public <T>CompletionStage<T>querySingleRowAsync(String sql,RowMapper<T>rm,Map<String,?>param){
        return supplyAsync(()->querySingleRow(sql,rm,param),executorService);
    }

    public <T> T querySingleRow(String sql,RowMapper<T>rm,Map<String,?>param){
        return namedParameterJdbcTemplate.queryForObject(sql,param,rm);
    }

    public <T>CompletionStage<Optional<T>>queryAnyRowAsync (String sql,RowMapper<T>rm,Map<String,?>param){
        return queryAsync(sql,rm,param)
                .thenApply(rows->rows.stream().findFirst())
                .exceptionally(
                        throwable -> {
                            throwable=unwrapCompletionStateException(throwable);
                            if (throwable instanceof EmptyResultDataAccessException){
                                return Optional.empty();
                            }
                            throw new CompletionException(throwable);
                        }
                );
    }

    public CompletionStage<int[]>batchUpdateParamSourceAsync(String sql,SqlParameterSource[] batchArgs){
        return supplyAsync(()->batchUpdate(sql,batchArgs),executorService);
    }

    public CompletionStage<int[]>batchUpdateAsync(String sql,Map<String,?>[] params){
        return supplyAsync(()->batchUpdate(sql,params),executorService);
    }

    public int[] batchUpdate(String sql,Map<String,?>[] batchValues){
        return batchUpdate(sql, SqlParameterSourceUtils.createBatch(batchValues));
    }

    public int[] batchUpdate(String sql,SqlParameterSource[] batchArgs){
        return namedParameterJdbcTemplate.batchUpdate(sql,batchArgs);
    }

    public PGobject getJsonObject(Object obj){
        String json="{}";
        if(obj!=null)
        json=gson.toJson(obj);
        return getJsonObjectFromString(json);
    }
    public PGobject getJsonObjectFromString(String json){
        PGobject pGobject=new PGobject();
        pGobject.setType("jsonb");
        try {
            pGobject.setValue(Optional.ofNullable(json).orElse("{}"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pGobject;
    }

    public static Map<String,String>jsonToMap(String jsonString){
        return firstNonNull(gson.fromJson(jsonString,mapType), Collections.emptyMap());
    }
}
