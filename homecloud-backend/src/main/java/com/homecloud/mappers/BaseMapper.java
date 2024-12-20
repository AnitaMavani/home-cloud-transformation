package com.homecloud.mappers;


 import org.apache.ibatis.annotations.Param;

 import java.util.List;

 interface BaseMapper<T, P> {

	 Integer insert(@Param("bean") T t);
	 Integer selectCount(@Param("query") P p);
	 List<T> selectList(@Param("query") P p);

}
