package com.chat.serveur;

public class SalonPrive {
	String aliasHote,aliasInvite;

    public SalonPrive(String aliasHote, String aliasInvite) {
        this.aliasHote = aliasHote;
        this.aliasInvite = aliasInvite;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SalonPrive sp = (SalonPrive) obj;
        return aliasInvite.equals(sp.aliasInvite) && aliasHote.equals(sp.aliasHote);
    }

}
