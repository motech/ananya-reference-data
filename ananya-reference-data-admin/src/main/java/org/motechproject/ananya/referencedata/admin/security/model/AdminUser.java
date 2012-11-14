package org.motechproject.ananya.referencedata.admin.security.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Table(name = "admin_users")
public class AdminUser extends BaseEntity {

    private String name;

    private String password;

    public AdminUser() {
    }

    public AdminUser(String name, String password) {
        this.name = name;
        this.password = hash(password);
    }

    private String hash(String password) {
        String hashWord = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes());
            BigInteger hash = new BigInteger(1, md5.digest());
            hashWord = hash.toString(16);
        }catch (NoSuchAlgorithmException e) {
            // ignore
        }
        return hashWord;
    }

    public boolean passwordIs(String password) {
        return StringUtils.isNotBlank(password) && this.password.equals(hash(password));
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
