package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

@Service("iCategoryServiceImpl")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//这个分类是可用的

        int resultCount = categoryMapper.insert(category);
        if (resultCount > 0){
            return ServerResponse.createBySuccess("品类插入成功");
        }
        return ServerResponse.createByErrorMessage("品类插入失败");
    }

    public ServerResponse updateCategory(String categoryName, Integer categoryId){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount > 0){
            return ServerResponse.createBySuccess("品类名称更新成功");
        }
        return ServerResponse.createByErrorMessage("品类名称更新失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            //若为空，我们用日志记录
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    /**
     * 递归查询本节点的id以及孩子节点的id
     * @param categoryId
     * @return
     */
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();//该方法是Guawa提供的方法（Guawa对于集合操作还是很强大的）
        findChildCategory(categorySet, categoryId);

        List<Integer> categoryIdList = Lists.newArrayList();//某类别下的所有子类别的Id
        if(categoryId != null){
            for (Category categoryItem : categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);

    }

    //我们要对Category对象的equals和hashcode方法在Category.java中进行重写,递归算法算出子节点。
    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category != null){
            categorySet.add(category);
        }
        //查找子节点，设计递归算法的递归出口
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for( Category categoryItem : categoryList){
            findChildCategory(categorySet, categoryItem.getId());
        }
        return categorySet;
    }
}
