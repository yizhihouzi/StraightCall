package com.arvin.straightcall.bean;

import java.io.Serializable;

public class Contact implements Serializable{

   private Long id;
    private String name;
    private String phone_num;
    private String photo_native_url;
    private String photo_remote_url;

    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Contact() {
    }

    public Contact(Long id) {
        this.id = id;
    }

    public Contact(Long id, String name, String phone_num, String photo_native_url, String photo_remote_url) {
        this.id = id;
        this.name = name;
        this.phone_num = phone_num;
        this.photo_native_url = photo_native_url;
        this.photo_remote_url = photo_remote_url;
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

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_num) {
        this.phone_num = phone_num;
    }

    public String getPhoto_native_url() {
        return photo_native_url;
    }

    public void setPhoto_native_url(String photo_native_url) {
        this.photo_native_url = photo_native_url;
    }

    public String getPhoto_remote_url() {
        return photo_remote_url;
    }

    public void setPhoto_remote_url(String photo_remote_url) {
        this.photo_remote_url = photo_remote_url;
    }

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END


    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone_num='" + phone_num + '\'' +
                ", photo_native_url='" + photo_native_url + '\'' +
                ", photo_remote_url='" + photo_remote_url + '\'' +
                '}';
    }
}
