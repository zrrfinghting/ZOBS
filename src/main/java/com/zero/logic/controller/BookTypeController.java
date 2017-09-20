package com.zero.logic.controller;

import com.zero.basic.filter.BasicFilter;
import com.zero.logic.dao.BookDao;
import com.zero.logic.dao.BookTypeDao;
import com.zero.logic.dao.LogDao;
import com.zero.logic.domain.Book;
import com.zero.logic.domain.BookType;
import com.zero.logic.domain.Log;
import com.zero.logic.util.CodeGeneratorUtil;
import com.zero.logic.util.DateUtil;
import com.zero.logic.util.JsonUtil;
import com.zero.logic.util.TableUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 图书分类控制类
 *
 * @autherAdmin Deram Zhao
 * @creat 2017/6/15
 */
@RestController
@RequestMapping("bookType")
public class BookTypeController {
    @Autowired
    private BookTypeDao bookTypeDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private LogDao logDao;

    @RequestMapping(value = "/addBookType", method = RequestMethod.POST)
    @ApiOperation(value = "新增图书分类", notes = "新增图书分类")
    public String addBookType(@RequestBody BookType bookType, @RequestParam int level) {
        try {
            /**level新增分类等级：-1--顶级(父节点为空)，0--下级，1--同级
             * bookType对象里的parent字段是前端勾选的分类节点的typeId
             * */
            Iterable<BookType> iterable = bookTypeDao.findAll();
            if (level == -1) {
                bookType.setTypeId(CodeGeneratorUtil.getBookTypeCode("", iterable));//获取分类编号
            } else if (level == 1) {
                BookType oldBookType = bookTypeDao.getBookTypeByTypeId(bookType.getParent());
                bookType.setTypeId(CodeGeneratorUtil.getBookTypeCode(oldBookType.getParent(), iterable));
                bookType.setParent(oldBookType.getParent());
            } else if (level == 0) {
                bookType.setTypeId(CodeGeneratorUtil.getBookTypeCode(bookType.getParent(), iterable));
                bookType.setParent(bookType.getParent());
            }
            bookType.setCreateDate(new Date());
            bookType.setUpdateDate(new Date());
            bookTypeDao.save(bookType);
            //记录日志
            logDao.save(new Log(new Date(), new Date(), "新增" + bookType.getTypeName() + "分类成功", 0, BasicFilter.user_id));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "新增图书分类失败");
        }
        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "新增图书分类成功");
    }

    @RequestMapping(value = "/editBookType", method = RequestMethod.POST)
    @ApiOperation(value = "修改分类", notes = "修改图书分类")
    public String editBookType(@RequestBody BookType bookType) {
        try {
            BookType oldBookType = bookTypeDao.getBookTypeByTypeId(bookType.getTypeId());
            if (null != oldBookType) {
                bookType.setUpdateDate(new Date());//修改时间
                bookType.setCreateDate(DateUtil.parse(DateUtil.FORMAT2, oldBookType.getCreateDate()));
                bookTypeDao.save(bookType);
                //记录日志
                logDao.save(new Log(new Date(), new Date(), "修改" + bookType.getTypeName() + "分类成功", 0, BasicFilter.user_id));
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改图书分类成功");
            } else {
                return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "修改图书分类失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "修改图书分类失败");
        }
    }

    @RequestMapping(value = "/deleteBookType", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除分类", notes = "删除图书分类")
    public String deleteBookType(@RequestParam String typeId) {
        try {

            List<BookType> allBookType = (List<BookType>) bookTypeDao.findAll();
            BookType bookType = bookTypeDao.getBookTypeByTypeId(typeId);
            if (null != bookType) {
                if (getChildList(allBookType, bookType).size() == 0) {//分类是否有子类
                    List<Book> books = bookDao.getBookByTypeId(bookType.getTypeId());
                    if (books.size() == 0) {//分类是否被图书引用
                        bookTypeDao.delete(bookType);
                        //记录日志
                        logDao.save(new Log(new Date(), new Date(), "删除" + bookType.getTypeName() + "分类成功", 0, BasicFilter.user_id));
                        return JsonUtil.returnStr(JsonUtil.RESULT_SUCCESS, "删除分类成功");
                    } else {
                        return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除失败！该分类有图书引用，不能删除");
                    }
                } else {
                    return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除失败！该分类有子类，请先删除子类再来删除");
                }
            } else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除分类失败,该分类不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "删除分类失败");
        }
    }

    @RequestMapping(value = "/getByPage", method = RequestMethod.GET)
    @ApiOperation(value = "分页获取分类", notes = "分页获取分类信息")
    public String getByPage(
            @RequestParam("keyWord") String keyWord,
            @RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize) {
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "typeId");
            Pageable pageable = new PageRequest(pageNum - 1, pageSize, sort);
            Page<BookType> bookTypes = bookTypeDao.findBookTypesByTypeName(keyWord, pageable);
            List<BookType> list = new ArrayList<>();
           /* List<BookType> listRoot = new ArrayList<>();
            for (BookType bookType:bookTypes){
                list.add(bookType);
                if (bookType.getParent()==null || "".equals(bookType.getParent())){
                    listRoot.add(bookType);
                }
            }*/

            long total = bookTypeDao.countByTypeName(keyWord);//总记录数
            long totalPage = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;//总页数
            return TableUtil.createTableDate(list, total, pageNum, totalPage, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "分页获取分类失败");
        }
    }


    @RequestMapping(value = "/getTree", method = RequestMethod.GET)
    @ApiOperation(value = "获取分类树", notes = "获取分类树")
    public String getTree() {
        try {
            List<BookType> list = new ArrayList<>();
            List<BookType> listRoot = new ArrayList<>();
            List<String> listTrees = new ArrayList<>();
            Iterable<BookType> allBookType = bookTypeDao.findAll();
            Iterator<BookType> iter = allBookType.iterator();
            while (iter.hasNext()) {
                BookType bookType = iter.next();
                if (bookType.getParent() == null || "".equals(bookType.getParent())) {
                    listRoot.add(bookType);
                } else {
                    list.add(bookType);
                }
            }
            for (BookType rootNote : listRoot) {
                String strJson = null;
                String tree = getTreeList(list, rootNote.getTypeId());//list:所有parent不为空的分类，根目录ID
                Map map = new HashMap();
                map.put("children", tree);
                strJson = JsonUtil.makeJsonBeanByKey(rootNote, map);
                listTrees.add(strJson);
            }
            return JsonUtil.fromArray(listTrees);
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取分类树失败");
        }
    }


    @RequestMapping(value = "/getBookTypeByTypeId", method = RequestMethod.GET)
    @ApiOperation(value = "根据分类ID获取分类", notes = "通过分类ID获取分类信息")
    public String getBookTypeByTypeId(@RequestParam String typeId) {
        try {
            BookType bookType = bookTypeDao.getBookTypeByTypeId(typeId);
            if (null != bookType) {
                return JsonUtil.fromObject(bookType);
            } else {
                return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取分类信息失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.returnStr(JsonUtil.RESULT_FAIL, "获取分类信息失败");
        }
    }


    /**
     * 获取节点以及所属子节点信息列表
     *
     * @param list 所有分类
     * @param node 当前分类节点
     * @return 该分类以及所属子分类信息
     */
    public List<BookType> getChildList(List<BookType> list, BookType node) {
        try {
            List<BookType> nodeList = new ArrayList<>();
            Iterator<BookType> it = list.iterator();
            while (it.hasNext()) {
                BookType bookType = it.next();
                if (node.getTypeId().equals(bookType.getParent())) {
                    nodeList.add(bookType);
                }
            }
            return nodeList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 递归获取分类树
     *
     * @param list   所有的分类
     * @param typeId 分类ID
     * @return treeList 分类树结构
     * @throws Exception
     */
    public String getTreeList(List<BookType> list, String typeId) throws Exception {
        List<String> treeList = new ArrayList<>();
        for (BookType bookType : list) {
            String bookType_str = JsonUtil.fromObject(bookType);
            if (bookType.getParent().equals(typeId)) {
                String children = getTreeList(list, bookType.getTypeId());
                Map map = new HashMap();
                map.put("children", children);
                bookType_str = JsonUtil.makeJsonBeanByKey(bookType_str, map);
                treeList.add(bookType_str);
            }
        }

        return JsonUtil.fromArray(treeList);
    }

}
