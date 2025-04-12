package org.example.javablog.model;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String toString(){
        switch (this){
            case ROLE_USER: return "ROLE_USER";
            case ROLE_ADMIN: return "ROLE_ADMIN";
            default: return super.toString();
        }
    }
}
