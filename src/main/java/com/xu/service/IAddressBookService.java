package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.entity.AddressBook;
import com.xu.entity.Category;
import com.xu.mapper.AddressBookMapper;

import java.util.List;

public interface IAddressBookService extends IService<AddressBook> {
    /**
     * 添加一个新数据
     * @param addressBook
     * @return
     */
    boolean saveOne(AddressBook addressBook);

    /**
     * 添加一个新数据
     * 同时设置userId
     * @param addressBook
     * @return
     */
    boolean saveOne(AddressBook addressBook, Long userId);

    /**
     * 返回一页分类数据
     * @param page
     * @param pageSize
     * @return
     */
    Page<Category> getPage(int page, int pageSize, String name);

    /**
     * 修改一个分类数据
     * @param category
     * @return
     */
    boolean updateOne(Category category);

    /**
     * 删除一个分类
     * @param id
     * @return
     */
    boolean removeOne(Long id);

    List<AddressBook> getList();

    List<AddressBook> getListByUserId(Long userID);

    boolean setDefault(AddressBook addressBook);
}
