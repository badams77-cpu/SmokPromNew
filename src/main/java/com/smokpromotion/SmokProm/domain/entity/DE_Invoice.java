package com.smokpromotion.SmokProm.domain.entity;

import com.majorana.maj_orm.ORM.BaseMajoranaEntity;
import jakarta.persistence.Column;

import java.time.LocalDate;

public class DE_Invoice extends BaseMajoranaEntity {

    private static final String TABLE_NAME = "invoices";

    @Column(name="user_id")
    private int userId;
    @Column(name="invoice_date")
    private LocalDate invoiceDate;
    @Column(name="payed_date")
    private LocalDate payedDate;
    @Column(name="nsent")
    private int nSent;
    @Column(name="amtCharged")
    private double amtCharged;
    @Column(name="amtPaid")
    private double amtPaid;

    public static String getTableNameStatic() {
        return TABLE_NAME;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getPayedDate() {
        return payedDate;
    }

    public void setPayedDate(LocalDate payedDate) {
        this.payedDate = payedDate;
    }

    public int getnSent() {
        return nSent;
    }

    public void setnSent(int nSent) {
        this.nSent = nSent;
    }

    public double getAmtCharged() {
        return amtCharged;
    }

    public void setAmtCharged(double amtCharged) {
        this.amtCharged = amtCharged;
    }

    public double getAmtPaid() {
        return amtPaid;
    }

    public void setAmtPaid(double amtPaid) {
        this.amtPaid = amtPaid;
    }

    @Override
    public String toString() {
        return "DE_Invoice{" +
                "userId=" + userId +
                ", invoiceDate=" + invoiceDate +
                ", payedDate=" + payedDate +
                ", nSent=" + nSent +
                ", amtCharged=" + amtCharged +
                ", amtPaid=" + amtPaid +
                ", id=" + id +
                ", uuid=" + uuid +
                ", deleted=" + deleted +
                ", deletedAt=" + deletedAt +
                ", createdByUserid=" + createdByUserid +
                ", updatedByUserid=" + updatedByUserid +
                ", created=" + created +
                ", updated=" + updated +
                ", createdByUserEmail='" + createdByUserEmail + '\'' +
                ", updatedByUserEmail='" + updatedByUserEmail + '\'' +
                '}';
    }
}
