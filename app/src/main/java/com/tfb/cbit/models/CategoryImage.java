package com.tfb.cbit.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryImage implements Serializable {
    public ArrayList<Category> categoryArrayList = new ArrayList<>();



    public static class Category implements Serializable {
        public int id=0;
        public String name="";

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }



        public ArrayList<SpinningImagesModel.Content> spinningImagesModelArrayList = new ArrayList<>();

    }
}
