package Resources;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.Date;
import java.sql.Timestamp;



public class Customer {
    private int customerId;
    private StringProperty customerName = new SimpleStringProperty();
    private int addressId;
    private StringProperty address = new SimpleStringProperty();
    private StringProperty address2 = new SimpleStringProperty();
    private int cityId;
    private StringProperty city = new SimpleStringProperty();
    private int countryId;
    private StringProperty country = new SimpleStringProperty();
    private StringProperty postalCode = new SimpleStringProperty();
    private StringProperty phone = new SimpleStringProperty();
    private int active;
    private Date createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;

    public StringProperty getAddressProp() {return address;}

    public String getAddress() {
        return address.get();
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public StringProperty getAddress2Prop() {return address2;}

    public String getAddress2() {
        return address2.get();
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public StringProperty getCityProp() { return  city;}

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public StringProperty getCountryProp() { return country; }

    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) { this.country.set(country); }

    public StringProperty getPostalCodeProp() { return postalCode; }

    public String getPostalCode() {
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }

    public StringProperty getPhoneProp() { return phone;}

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public StringProperty getCustomerNameProp() { return customerName;}

    public String getCustomerName() { return customerName.get(); }

    public void setCustomerName(String customerName) { this.customerName.set(customerName); }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }


}
