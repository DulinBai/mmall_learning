package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUsername(String username);

    int checkEmail(String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);
    //@Param的作用是给参数命名,参数命名后就能根据名字得到参数值。在两个以上参数或者方法参数名不同于sql语句里的参数名时使用。

    String selectQuestionByUsername(String username);

    int checkAnswer(@Param("username")String username,@Param("question")String question,@Param("answer")String answer);

    int updatePasswordByUsername(@Param("username")String username,@Param("passwordNew")String passwordNew);

    int checkPassword(@Param(value="password")String password,@Param("userId")Integer userId);//如果不写value也可以，@Param默认设置为value

    int checkEmailByUserId(@Param(value="email")String email,@Param(value = "userId")Integer userId);
}