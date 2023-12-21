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
@Table(value = "project_version")
public class ProjectVersion implements Serializable {

    @Id
    private String id;

    private String projectId;

    private String name;

    private String description;

    private String status;

    private Boolean latest;

    private LocalDateTime publishTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @Column(onInsertValue = "now()")
    private LocalDateTime createTime;

    private String createUser;

}
