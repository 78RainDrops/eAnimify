package com.rcm.eanimify.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.PropertyName;
@Entity(tableName = "animals")
public class Animal {

    @PrimaryKey(autoGenerate = true)
    private int id ;

    @ColumnInfo(name = "common_name")
    private String commonName;

    @ColumnInfo(name = "scientific_name")
    private String scientificName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "endanger_level")
    private String endangerLevel;

    @ColumnInfo(name = "family_name")
    private String familyName;

    @ColumnInfo(name = "province")
    private String province;

    @ColumnInfo(name = "taxonomic_group")
    private String taxonomicGroup;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndangerLevel() {
        return endangerLevel;
    }

    public void setEndangerLevel(String endangerLevel) {
        this.endangerLevel = endangerLevel;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getTaxonomicGroup() {
        return taxonomicGroup;
    }

    public void setTaxonomicGroup(String taxonomicGroup) {
        this.taxonomicGroup = taxonomicGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}