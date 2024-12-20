package com.homecloud.mappers;

import com.homecloud.entity.domain.FileInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface FileInfoMapper<T, P> extends BaseMapper<T, P> {


    Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

    Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

    void updateFileStatusWithOldStatus(@Param("oldStatus") Integer oldStatus, @Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

}
