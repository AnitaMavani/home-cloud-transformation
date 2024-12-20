package com.homecloud.mappers;

import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper<T, P> extends BaseMapper<T, P> {

    Integer updateByUserId(@Param("bean") T t, @Param("userId") String userId);

    T selectByEmail(@Param("email") String email);


    T selectByNickName(@Param("nickName") String nickName);
}





