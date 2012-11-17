/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Rommel
 */
@Entity
@Table(name = "organization")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Organization.findAll", query = "SELECT o FROM Organization o")})
public class Organization implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ORG_ID")
    private Integer orgId;
    @Size(max = 60)
    @Column(name = "DISPLAY_NAME")
    private String displayName;
    @Size(max = 60)
    @Column(name = "LEGAL_NAME")
    private String legalName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "ORG_TYPE")
    private String orgType;
    @Size(max = 60)
    @Column(name = "BUSINESS_LINE")
    private String businessLine;
    @Size(max = 20)
    @Column(name = "TELEPHONE")
    private String telephone;
    // @Pattern(regexp="^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$", message="Invalid phone/fax format, should be as xxx-xxx-xxxx")//if the field contains phone or fax number consider using this annotation to enforce field validation
    @Size(max = 20)
    @Column(name = "FAX")
    private String fax;
    @Size(max = 20)
    @Column(name = "MOBILE")
    private String mobile;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 60)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 45)
    @Column(name = "PO_ATTENTION_TO")
    private String poAttentionTo;
    @Size(max = 200)
    @Column(name = "PO_ADDRESS_LINE1")
    private String poAddressLine1;
    @Size(max = 200)
    @Column(name = "PO_ADDRESS_LINE2")
    private String poAddressLine2;
    @Size(max = 45)
    @Column(name = "PO_CITY")
    private String poCity;
    @Size(max = 45)
    @Column(name = "PO_REGION")
    private String poRegion;
    @Size(max = 45)
    @Column(name = "PO_ZIP_CODE")
    private String poZipCode;
    @Size(max = 45)
    @Column(name = "PO_COUNTRY")
    private String poCountry;
    @Size(max = 45)
    @Column(name = "SA_ATTENTION_TO")
    private String saAttentionTo;
    @Size(max = 200)
    @Column(name = "SA_ADDRESS_LINE1")
    private String saAddressLine1;
    @Size(max = 200)
    @Column(name = "sa_address_line2")
    private String saAddressLine2;
    @Size(max = 45)
    @Column(name = "SA_CITY")
    private String saCity;
    @Size(max = 45)
    @Column(name = "SA_REGION")
    private String saRegion;
    @Size(max = 45)
    @Column(name = "SA_ZIP_CODE")
    private String saZipCode;
    @Size(max = 45)
    @Column(name = "SA_COUNTRY")
    private String saCountry;

    public Organization() {
    }

    public Organization(Integer orgId) {
        this.orgId = orgId;
    }

    public Organization(Integer orgId, String orgType) {
        this.orgId = orgId;
        this.orgType = orgType;
    }

    public Integer getOrgId() {
        return orgId;
    }

    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public String getBusinessLine() {
        return businessLine;
    }

    public void setBusinessLine(String businessLine) {
        this.businessLine = businessLine;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPoAttentionTo() {
        return poAttentionTo;
    }

    public void setPoAttentionTo(String poAttentionTo) {
        this.poAttentionTo = poAttentionTo;
    }

    public String getPoAddressLine1() {
        return poAddressLine1;
    }

    public void setPoAddressLine1(String poAddressLine1) {
        this.poAddressLine1 = poAddressLine1;
    }

    public String getPoAddressLine2() {
        return poAddressLine2;
    }

    public void setPoAddressLine2(String poAddressLine2) {
        this.poAddressLine2 = poAddressLine2;
    }

    public String getPoCity() {
        return poCity;
    }

    public void setPoCity(String poCity) {
        this.poCity = poCity;
    }

    public String getPoRegion() {
        return poRegion;
    }

    public void setPoRegion(String poRegion) {
        this.poRegion = poRegion;
    }

    public String getPoZipCode() {
        return poZipCode;
    }

    public void setPoZipCode(String poZipCode) {
        this.poZipCode = poZipCode;
    }

    public String getPoCountry() {
        return poCountry;
    }

    public void setPoCountry(String poCountry) {
        this.poCountry = poCountry;
    }

    public String getSaAttentionTo() {
        return saAttentionTo;
    }

    public void setSaAttentionTo(String saAttentionTo) {
        this.saAttentionTo = saAttentionTo;
    }

    public String getSaAddressLine1() {
        return saAddressLine1;
    }

    public void setSaAddressLine1(String saAddressLine1) {
        this.saAddressLine1 = saAddressLine1;
    }

    public String getSaAddressLine2() {
        return saAddressLine2;
    }

    public void setSaAddressLine2(String saAddressLine2) {
        this.saAddressLine2 = saAddressLine2;
    }

    public String getSaCity() {
        return saCity;
    }

    public void setSaCity(String saCity) {
        this.saCity = saCity;
    }

    public String getSaRegion() {
        return saRegion;
    }

    public void setSaRegion(String saRegion) {
        this.saRegion = saRegion;
    }

    public String getSaZipCode() {
        return saZipCode;
    }

    public void setSaZipCode(String saZipCode) {
        this.saZipCode = saZipCode;
    }

    public String getSaCountry() {
        return saCountry;
    }

    public void setSaCountry(String saCountry) {
        this.saCountry = saCountry;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orgId != null ? orgId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Organization)) {
            return false;
        }
        Organization other = (Organization) object;
        if ((this.orgId == null && other.orgId != null) || (this.orgId != null && !this.orgId.equals(other.orgId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "count.domain.Organization[ orgId=" + orgId + " ]";
    }
    
}
