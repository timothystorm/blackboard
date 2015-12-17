package com.fedex.toolbox.core.domain;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Identity {

    public static class UuidIdentity extends Identity {
        public UuidIdentity() {
            super(generate());
        }

        private static Number generate() {
            UUID uuid = UUID.randomUUID();
            String uuidHex = uuid.toString().replace("-", "");
            return new BigInteger(uuidHex, 16);
        }
    }

    public static class RandomIdentity extends Identity {
        public RandomIdentity() {
            super(generate());
        }

        private static Number generate() {
            try {
                SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
                random.nextBytes(new byte[32]);
                return new BigInteger(String.valueOf(random.nextLong())).abs();
            } catch (NoSuchAlgorithmException e) {
                throw new NullPointerException("Unknown randomize alogorithm - " + e.getMessage());
            }
        }
    }

    public Identity(Number id) {
        _id = new BigDecimal(id.toString());
    }

    private final BigDecimal _id;

    public BigDecimal getId() {
        return _id;
    }

    @Override
    public int hashCode() {
        return _id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Identity)) return false;
        if (obj == this) return true;

        Identity other = (Identity) obj;
        EqualsBuilder equals = new EqualsBuilder();
        equals.append(getId(), other.getId());
        return equals.isEquals();
    }

    @Override
    public String toString() {
        ToStringBuilder str = new ToStringBuilder(this, ToStringStyle.JSON_STYLE);
        str.append("id", getId());
        return str.toString();
    }
}
