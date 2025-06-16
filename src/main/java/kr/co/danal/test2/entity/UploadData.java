package kr.co.danal.test2.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "upload_data")
public class UploadData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tid;
    private String orderid;

    private String tdate;      // LocalDate → String
    private String cdate;      // LocalDate → String

    @Column(name = "in_pdate")
    private String inPdate;    // LocalDate → String

    private BigDecimal amt;
    private BigDecimal fee;
    private String cpid;
    private String cpnm;

    public UploadData() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }
    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getOrderid() {
        return orderid;
    }
    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getTdate() {
        return tdate;
    }
    public void setTdate(String tdate) {
        this.tdate = tdate;
    }

    public String getCdate() {
        return cdate;
    }
    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getInPdate() {
        return inPdate;
    }
    public void setInPdate(String inPdate) {
        this.inPdate = inPdate;
    }

    public BigDecimal getAmt() {
        return amt;
    }
    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }

    public BigDecimal getFee() {
        return fee;
    }
    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getCpid() {
        return cpid;
    }
    public void setCpid(String cpid) {
        this.cpid = cpid;
    }

    public String getCpnm() {
        return cpnm;
    }
    public void setCpnm(String cpnm) {
        this.cpnm = cpnm;
    }
}