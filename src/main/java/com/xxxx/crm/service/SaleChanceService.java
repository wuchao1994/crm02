package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.mapper.SaleChanceMapper;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Autowired(required = false)
    private SaleChanceMapper saleChanceMapper;

    /**
     *
     * @param saleChanceQuery
     * @return
     */
    public Map<String,Object> querySaleChanceByParam(SaleChanceQuery saleChanceQuery){
        //实例化Map
        Map<String,Object> map=new HashMap<>();
        //初始化分页
        PageHelper.startPage(saleChanceQuery.getPage(),saleChanceQuery.getLimit());
        //查询
        List<SaleChance> list = saleChanceMapper.selectByParams(saleChanceQuery);
        //pageInfo
        PageInfo<SaleChance> plist=new PageInfo<SaleChance>(list);
        //构建数据
        map.put("code",0);
        map.put("msg","");
        map.put("count",plist.getTotal());
        map.put("data",plist.getList());
        //map--json
        return map;
    }

    /**
     * 营销机会数据添加
     *   1.参数校验
     *      customerName:非空
     *      linkMan:非空
     *      linkPhone:非空 11位手机号
     *   2.设置相关参数默认值
     *      state:默认未分配  如果选择分配人  state 为已分配
     *      assignTime:如果  如果选择分配人   时间为当前系统时间
     *      devResult:默认未开发 如果选择分配人devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
     *      isValid:默认有效数据(1-有效  0-无效)
     *      createDate updateDate:默认当前系统时间
     *   3.执行添加 判断结果
     */
    /**
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveSaleChance(SaleChance saleChance){
        //验证
        checkParmas(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //未分配
        if(StringUtils.isBlank(saleChance.getAssignMan())){
            //未分配
            saleChance.setState(0);//0--未分配，1，已经分配
            //开发状态 未开发
            saleChance.setDevResult(0);
            //分配时间
            saleChance.setAssignTime(null);
        }
        if(StringUtils.isNotBlank(saleChance.getAssignMan())){
            //已经分配
            saleChance.setState(1);
            //开发中
            saleChance.setDevResult(1);//0-未开发，1--开发中，2，成功，3--失败
            //分配时间
            saleChance.setAssignTime(new Date());
        }
        //系统默认时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //添加是否成功
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance)<1,"添加失败");
    }

    /**
     * 参数的验证
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParmas(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"客户名不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"联系人不能为空");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"电话不能为空");
        AssertUtil.isTrue(!(PhoneUtil.isMobile(linkPhone)),"请输入合法的手机号");
    }

    /**
     * 营销机会数据更新
     *  1.参数校验
     *      id:记录必须存在
     *      customerName:非空
     *      linkMan:非空
     *      linkPhone:非空，11位手机号
     *  2. 设置相关参数值
     *      updateDate:系统当前时间
     *         原始记录 未分配 修改后改为已分配(由分配人决定)
     *            state 0->1
     *            assginTime 系统当前时间
     *            devResult 0-->1
     *         原始记录  已分配  修改后 为未分配
     *            state  1-->0
     *            assignTime  待定  null
     *            devResult 1-->0
     *  3.执行更新 判断结果
     */
    /**
     * userId
     *
     *
     *
     * @param saleChance
     */
    public void changeSaleChance(SaleChance saleChance){
        //判断id是否存在
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断
        AssertUtil.isTrue(temp==null,"修改记录不存在");
        //参数验证
        checkParmas(saleChance.getCustomerName(),saleChance.getLinkMan(),saleChance.getLinkPhone());
        //未分配
        if(StringUtils.isBlank(temp.getAssignMan())&&StringUtils.isNotBlank(saleChance.getAssignMan())){
            //分配状态
            saleChance.setState(1);
            //开发状态
            saleChance.setDevResult(1);
            //分配时间
            saleChance.setAssignTime(new Date());
        }else if(StringUtils.isNotBlank(temp.getAssignMan())&& StringUtils.isBlank(saleChance.getAssignMan())){
            //已经分配
            //原来 分配状态
            saleChance.setState(0);
            //开发状态
            saleChance.setDevResult(0);
            //修改 分配时间
            saleChance.setAssignTime(null);
            //分配人
            saleChance.setAssignMan("");
        }
        //saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());
        //修改是否成功
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)<1,"更新失败");
    }


    /**
     * 批量删除
     * @param array
     */
    public void removeSaleChance(Integer []array){
        AssertUtil.isTrue(array == null || array.length==0,"请选择要删除的数据");
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(array)<1,"批量删除失败了");
    }
}
