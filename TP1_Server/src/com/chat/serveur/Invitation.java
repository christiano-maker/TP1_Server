package com.chat.serveur;

public class Invitation {
	private String aliasHote,aliasInvite;

    @Override
    public String toString() {
        return "Invitation{" +
                "aliasHote='" + aliasHote + '\'' +
                ", aliasInvite='" + aliasInvite + '\'' +
                '}';
    }

    public String getAliasHote() {
        return aliasHote;
    }

    public void setAliasHote(String aliasHote) {
        this.aliasHote = aliasHote;
    }

    public String getAliasInvite() {
        return aliasInvite;
    }

    public void setAliasInvite(String aliasInvite) {
        this.aliasInvite = aliasInvite;
    }

    public Invitation(String aliasHote, String aliasInvite) {
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
        Invitation invitation = (Invitation) obj;
        return aliasInvite.equals(invitation.aliasInvite) && aliasHote.equals(invitation.aliasHote);
    }

}
