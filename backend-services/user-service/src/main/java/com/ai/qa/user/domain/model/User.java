package com.ai.qa.user.domain.model;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.util.StringUtils;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    // JPA需要一个无参构造函数
    protected User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 这是核心的业务方法，而不是一个简单的setter.
     * 它封装了更改昵称的所有业务规则。
     *
     * @param newNickname 新的昵称
     */
    public void changeNickname(String newNickname) {
        // 业务规则1: 昵称不能为空或仅包含空白字符
        if (!StringUtils.hasText(newNickname)) {
            throw new IllegalArgumentException("昵称不能为空。");
        }
        // 业务规则2: 昵称长度不能超过50个字符 (示例)
        if (newNickname.length() > 50) {
            throw new IllegalArgumentException("昵称长度不能超过50个字符。");
        }
        // 业务规则3: 昵称不能与现有昵称相同
        if (newNickname.equals(this.nickname)) {
            // 或者可以静默处理，这里选择抛出异常作为示例
            throw new IllegalArgumentException("新昵称不能与旧昵称相同。");
        }

        this.nickname = newNickname;
    }

    // --- Getters ---
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }
}