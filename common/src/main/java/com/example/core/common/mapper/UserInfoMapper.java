package com.example.core.common.mapper;

import com.example.core.common.entity.UserInfo;
import com.example.core.common.vo.user.SelectListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface UserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserInfo record);

    int insertSelective(UserInfo record);

    UserInfo selectByPrimaryKey(Integer id);

    UserInfo selectByPhone(@Param("phone") String phone);

    UserInfo selectByEmail(@Param("email") String email);

    UserInfo selectById(@Param("userId") String userId);

    UserInfo selectByMarket(@Param("market") String market);

    int selectAllNum();

    int selectActiveNum();

    int updateByPrimaryKeySelective(UserInfo record);

    int updateByPrimaryKey(UserInfo record);

    List<SelectListVO> selectList(Map<String,Object> param);

    List<SelectListVO> selectListNew(Map<String,Object> param);

    List<UserInfo> selectByType(@Param("type") Integer type);

    Integer queryActiveNum(Map<String,Object> map);

//    List<CreateNum> selectCreateNum(@Param("dateStyle")String dateStyle, @Param("startTime")String startTime, @Param("endTime")String endTime);

    UserInfo selectByUserId(String userId);


    // 批量查询活跃数
//    List<CreateNum> batchQueryActiveNum(Map<String, Object> param);

    List<Map<String, Object>> selectAllActiveDateRanges(Map<String, Object> rangeParam);

    List<Map<String, Object>> selectAllActiveUsersWithRange(Map<String, Object> param);
}