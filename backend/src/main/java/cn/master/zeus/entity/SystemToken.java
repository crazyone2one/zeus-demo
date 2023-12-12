package cn.master.zeus.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import com.mybatisflex.core.keygen.KeyGenerators;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * token信息表 实体类。
 *
 * @author 11's papa
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "system_token")
public class SystemToken implements Serializable {

    /**
     * 主键
     */
    @Id
    private String id;

    /**
     * token
     */
    @NotNull(message = "Token is required")
    private String token;

    /**
     * token type
     */
    @NotNull(message = "Token type is required")
    private String tokenType;

    private boolean revoked;

    /**
     * user id
     */
    private String userId;

}
