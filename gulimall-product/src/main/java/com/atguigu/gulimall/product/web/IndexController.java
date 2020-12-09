package com.atguigu.gulimall.product.web;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author kLjSumi
 * @Date 2020/11/25
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model) {

        //获取一级菜单
        List<CategoryEntity> list =  categoryService.getLevel1Category();
        model.addAttribute("category", list);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {

        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }


    /**
     * 测试Redisson分布式锁 默认加锁时间30s
     * 1、锁的自动续期、如果业务时间超长，运行期间自动给锁续上新的30s
     * 2、加锁的业务只要执行完成，就不会给当前锁续期，即使不手动解锁，也会在锁超时后释放锁
     * @return
     */
    @ResponseBody
    @GetMapping("/hello")
    public R hello() {
        //1、获取一把锁，只要锁名字一样，就是同一把锁
        RLock lock = redissonClient.getLock("my-lock");

        //2、加锁 阻塞式等待
        lock.lock();
//        lock.lock(10,TimeUnit.SECONDS); //10秒钟自动解锁，自动解锁时间一定要大于业务执行时间，不会自动续期
        try {
            System.out.println("加锁成功，执行业务...");
            TimeUnit.SECONDS.sleep(10);
        } catch (Exception e) {

        } finally {
            //释放锁
            System.out.println("释放锁"+ Thread.currentThread().getId());
            lock.unlock();
        }
        return R.ok();
    }
}
