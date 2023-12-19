package cn.master.zeus.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import cn.master.zeus.entity.OperatingLogResource;
import cn.master.zeus.service.OperatingLogResourceService;
import org.springframework.web.bind.annotation.RestController;
import java.io.Serializable;
import java.util.List;

/**
 *  控制层。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@RestController
@RequestMapping("/operatingLogResource")
public class OperatingLogResourceController {

    @Autowired
    private OperatingLogResourceService operatingLogResourceService;

    /**
     * 添加。
     *
     * @param operatingLogResource 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody OperatingLogResource operatingLogResource) {
        return operatingLogResourceService.save(operatingLogResource);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Serializable id) {
        return operatingLogResourceService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param operatingLogResource 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody OperatingLogResource operatingLogResource) {
        return operatingLogResourceService.updateById(operatingLogResource);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<OperatingLogResource> list() {
        return operatingLogResourceService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public OperatingLogResource getInfo(@PathVariable Serializable id) {
        return operatingLogResourceService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<OperatingLogResource> page(Page<OperatingLogResource> page) {
        return operatingLogResourceService.page(page);
    }

}
