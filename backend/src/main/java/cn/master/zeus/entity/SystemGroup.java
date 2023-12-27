package cn.master.zeus.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "system_group")
public class SystemGroup implements Serializable {

    @Id
    private String id;

    private String name;

    /**
     * group code
     */
    //private String groupCode;

    private String description;

    /**
     * 是否是系统用户组
     */
    private Boolean system;

    /**
     * 所属类型
     */
    private String type;

    /**
     * Create timestamp
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    @Column(onInsertValue = "now()",onUpdateValue = "now()")
    private LocalDateTime updateTime;

    /**
     * （0正常 1删除）
     */
    private Boolean delFlag;

    /**
     * 创建人(操作人）
     */
    private String creator;

    /**
     * 应用范围
     */
    private String scopeId;

}
