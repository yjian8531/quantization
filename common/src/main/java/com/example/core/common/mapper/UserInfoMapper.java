package com.example.core.common.mapper;

import com.example.core.common.entity.UserInfo;
import com.example.core.common.vo.user.SelectListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户信息数据访问接口
 * 该接口定义了对用户信息进行增删改查操作的方法
 */
public interface UserInfoMapper {
    /**
     * 根据主键删除用户信息
     * @param id 用户ID
     * @return 删除的记录数
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入用户信息
     * @param record 用户信息对象
     * @return 插入的记录数
     */
    int insert(UserInfo record);

    /**
     * 选择性插入用户信息（只插入非空字段）
     * @param record 用户信息对象
     * @return 插入的记录数
     */
    int insertSelective(UserInfo record);

    /**
     * 根据主键查询用户信息
     * @param id 用户ID
     * @return 用户信息对象
     */
    UserInfo selectByPrimaryKey(Integer id);

    /**
     * 根据手机号查询用户信息
     * @param phone 手机号
     * @return 用户信息对象
     */
    UserInfo selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户信息
     * @param email 邮箱
     * @return 用户信息对象
     */
    UserInfo selectByEmail(@Param("email") String email);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息对象
     */
    UserInfo selectById(@Param("userId") String userId);

    /**
     * 根据市场查询用户信息
     * @param 市场
     * @return 用户信息对象
     */
    UserInfo selectByMarket(@Param("market") String market);

    /**
     * 查询用户总数
     * @return 用户总数
     */
    int selectAllNum();

    /**
     * 查询活跃用户数
     * @return 活跃用户数
     */
    int selectActiveNum();

    /**
     * 选择性更新用户信息（只更新非空字段）
     * @param record 用户信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKeySelective(UserInfo record);

    /**
     * 更新用户信息（更新所有字段）
     * @param record 用户信息对象
     * @return 更新的记录数
     */
    int updateByPrimaryKey(UserInfo record);

    /**
     * 根据参数查询用户列表
     * @param param 查询参数
     * @return 用户列表
     */
    List<SelectListVO> selectList(Map<String,Object> param);

    /**
     * 根据参数查询用户列表（新方法）
     * @param param 查询参数
     * @return 用户列表
     */
    List<SelectListVO> selectListNew(Map<String,Object> param);

    /**
     * 根据类型查询用户列表
     * @param type 用户类型
     * @return 用户列表
     */
    List<UserInfo> selectByType(@Param("type") Integer type);

    /**
     * 根据参数查询活跃用户数
     * @param map 查询参数
     * @return 活跃用户数
     */
    Integer queryActiveNum(Map<String,Object> map);

//    List<CreateNum> selectCreateNum(@Param("dateStyle")String dateStyle, @Param("startTime")String startTime, @Param("endTime")String endTime);

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息对象
     */
    UserInfo selectByUserId(String userId);


    // 批量查询活跃数
//    List<CreateNum> batchQueryActiveNum(Map<String, Object> param);

    /**
     * 查询所有活跃日期范围
     * @param rangeParam 范围参数
     * @return 日期范围列表
     */
    List<Map<String, Object>> selectAllActiveDateRanges(Map<String, Object> rangeParam);

    /**
     * 根据范围参数查询所有活跃用户
     * @param param 查询参数
     * @return 活跃用户列表
     */
    List<Map<String, Object>> selectAllActiveUsersWithRange(Map<String, Object> param);
}