package cn.master.zeus.entity;

import cn.master.zeus.dto.UserGroupPermissionDTO;
import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "system_user")
public class SystemUser implements Serializable {

    /**
     * User ID
     */
    @Id
    private String id;

    /**
     * User name
     */
    @NotEmpty(message = "用户名不能为空")
    private String name;

    /**
     * E-Mail address
     */
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    private String password;

    /**
     * User status
     */
    private boolean status;

    /**
     * Create timestamp
     */
    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    /**
     * Update timestamp
     */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private LocalDateTime updateTime;

    private String language;

    private String lastWorkspaceId;

    private String lastOrganizationId;

    /**
     * Phone number of user
     */
    private String phone;

    private String source;

    private String lastProjectId;

    private String createUser;

    private String seleniumServer;

    private Boolean delFlag;

    @Column(ignore = true)
    private UserGroupPermissionDTO userGroupPermissionDTO;

}
