package com.pinniboina.storeadmin;

public class Upload {
    private String Id;
    private String Name;
    private String Description;
    private String Cost;
    private String ImageUri;

    public Upload() {
        //empty
    }

    public Upload(String id, String name, String description, String cost, String imageUri) {
        Id = id;
        Name = name;
        Description = description;
        Cost = cost;
        ImageUri = imageUri;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }
}
