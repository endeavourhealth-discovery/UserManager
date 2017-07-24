package org.endeavourhealth.datasharingmanager.api.database.models;

import javax.persistence.*;


@Entity
@Table(name = "range", schema = "information_model", catalog = "")
public class RangeEntity {
    private long id;
    private String operator;
    private double low;
    private double high;
    private long rangeType;

    @Id
    @Column(name = "id", nullable = false)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "operator", nullable = false, length = 4)
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Basic
    @Column(name = "low", nullable = false, precision = 4)
    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    @Basic
    @Column(name = "high", nullable = false, precision = 4)
    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    @Basic
    @Column(name = "range_type", nullable = false)
    public long getRangeType() {
        return rangeType;
    }

    public void setRangeType(long rangeType) {
        this.rangeType = rangeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RangeEntity that = (RangeEntity) o;

        if (id != that.id) return false;
        if (Double.compare(that.low, low) != 0) return false;
        if (Double.compare(that.high, high) != 0) return false;
        if (rangeType != that.rangeType) return false;
        if (operator != null ? !operator.equals(that.operator) : that.operator != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        temp = Double.doubleToLongBits(low);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(high);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (rangeType ^ (rangeType >>> 32));
        return result;
    }
}
