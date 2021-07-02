package com.example.pki.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name = "gram_user")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User implements UserDetails {
    private transient String administrationRole = "";

    @Id
    @SequenceGenerator(name = "user_sequence_generator", sequenceName = "user_sequence", initialValue = 100)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_sequence_generator")
    @Column(name = "id", unique = true)
    protected Long id;

    @Column(name = "name")
    protected String name;

    @Column(name = "surname")
    protected String surname;

    @Column(name = "email", unique = true)
    protected String email;

    @JsonIgnore
    @Column(name = "password")
    protected String password;

    @Column(name = "enabled")
    protected boolean enabled = false;

    @Column(name = "last_password_reset_date")
    protected Timestamp lastPasswordResetDate;

    @Column(name = "password_reset_code", length = 64)
    private String passwordResetCode;

    @Column(name = "password_reset_failed")
    private int passwordResetFailed = 0;

    @Column(name = "nistagramUsername", unique = true)
    protected String nistagramUsername;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_authority",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<Role> roles;

    @Column(name = "two_factor_auth_secret")
    private String twoFactorAuthSecret;

    @Column(name = "two_factor_auth_count")
    private int twoFactorAuthCount = 0;

    protected User() {
    }

    public String getNistagramUsername() {
        return nistagramUsername;
    }

    public void setNistagramUsername(String nistagramUsername) {
        this.nistagramUsername = nistagramUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Timestamp now = new Timestamp(new Date().getTime());
        this.setLastPasswordResetDate(now);
        this.password = password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Timestamp getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public void setLastPasswordResetDate(Timestamp lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
    }

    public String getPasswordResetCode() {
        return passwordResetCode;
    }

    public void setPasswordResetCode(String passwordResetCode) {
        this.passwordResetCode = passwordResetCode;
    }

    public int getPasswordResetFailed() {
        return passwordResetFailed;
    }

    public void setPasswordResetFailed(int passwordResetFailed) {
        this.passwordResetFailed = passwordResetFailed;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setAdministrationRole(String administrationRole) {
        this.administrationRole = administrationRole;
    }

    public String getAdministrationRole() {
        return this.administrationRole;
    }

    public void Enable() {
        this.enabled = true;
    }

    public void Disable() {
        this.enabled = false;
    }

    public String getTwoFactorAuthSecret() {
        return twoFactorAuthSecret;
    }

    public void setTwoFactorAuthSecret(String twoFactorAuthSecret) {
        this.twoFactorAuthSecret = twoFactorAuthSecret;
    }

    public int getTwoFactorAuthCount() {
        return twoFactorAuthCount;
    }

    public void setTwoFactorAuthCount(int twoFactorAuthCount) {
        this.twoFactorAuthCount = twoFactorAuthCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return enabled == user.enabled &&
                Objects.equals(administrationRole, user.administrationRole) &&
                Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password) &&
                Objects.equals(lastPasswordResetDate, user.lastPasswordResetDate) &&
                Objects.equals(roles, user.roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getGrantedAuthorities();
    }

    @Override
    public String getUsername() {
        return nistagramUsername;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public void incrementPasswordResetFailed() {
        this.passwordResetFailed++;
    }

    public void resetPasswordResetCode() {
        this.passwordResetCode = null;
        this.passwordResetFailed = 0;
    }

    public void incrementTwoAuthFactorCount() {
        this.twoFactorAuthCount++;
    }

    public void resetTwoAuthFactorCount() {
        this.twoFactorAuthSecret = null;
        this.twoFactorAuthCount = 0;
    }

    private List<GrantedAuthority> getGrantedAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : this.getRoles()) {
            for (Privilege privilege : role.getPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }
        return authorities;
    }
}
