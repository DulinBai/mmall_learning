package com.mmall.controller.backend;


import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId){
        //若前端没有传过来parentId的时候，我们用SpringMVC的@RequestParam赋予默认值为0。
        //首先校验是否登录
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        //再检验是否是管理员
        if (iUserService.checkAdminRole(user).isSuccess()){
            //是管理员，则增加处理分类的逻辑
            return iCategoryService.addCategory(categoryName,parentId);
        } else {
            //否则无权限
            return ServerResponse.createByErrorMessage("无权限操作，需要管理员权限");
        }
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public ServerResponse updateCategory(HttpSession session, String categoryName, Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if (user.getRole() == Const.Role.ROLE_ADMIN){
            return iCategoryService.updateCategory(categoryName,categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，该操作需要管理员权限");
        }
    }


    @RequestMapping("get_category.do")
    @ResponseBody
    public ServerResponse getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询子节点的category信息，并且不递归保持平级
            return iCategoryService.getChildrenParallelCategory(categoryId);

        } else {
            return ServerResponse.createByErrorMessage("无权限，该操作需要管理员权限");
        }
    }


    @RequestMapping("get_deep_category.do")
    @ResponseBody
    public ServerResponse getCategoryAndDeepChildrenCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if (user == null ){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆");
        }
        if (iUserService.checkAdminRole(user).isSuccess()){
            //查询当前节点的category信息，以及递归子节点的category信息
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        } else {
            return ServerResponse.createByErrorMessage("无权限，该操作需要管理员权限");
        }
    }

}
