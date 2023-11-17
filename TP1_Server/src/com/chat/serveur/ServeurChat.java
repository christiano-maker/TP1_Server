package com.chat.serveur;

import com.chat.commun.net.Connexion;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Cette classe �tend (h�rite) la classe abstraite Serveur et y ajoute le n�cessaire pour que le
 * serveur soit un serveur de chat.
 *
 * @author Abdelmoum�ne Toudeft (Abdelmoumene.Toudeft@etsmtl.ca)
 * @version 1.0
 * @since 2023-09-15
 */
public class ServeurChat extends Serveur {

    /**
     * Cr�e un serveur de chat qui va �couter sur le port sp�cifi�.
     *
     * @param port int Port d'�coute du serveur
     */
    Vector<String> historique= new Vector<>();
   List<Invitation> invitations= new Vector<>();
    List<SalonPrive> salonPrives= new Vector<>();

    public ServeurChat(int port) {
        super(port);
    }

    @Override
    public synchronized boolean ajouter(Connexion connexion) {
        String hist = this.historique();
        if ("".equals(hist)) {
            connexion.envoyer("OK");
        }
        else {
            connexion.envoyer("HIST " + hist);
        }
        return super.ajouter(connexion);
    }
    
    /**
     * Valide l'arriv�e d'un nouveau client sur le serveur. Cette red�finition
     * de la m�thode h�rit�e de Serveur v�rifie si le nouveau client a envoy�
     * un alias compos� uniquement des caract�res a-z, A-Z, 0-9, - et _.
     *
     * @param connexion Connexion la connexion repr�sentant le client
     * @return boolean true, si le client a valid� correctement son arriv�e, false, sinon
     */
    @Override
    protected boolean validerConnexion(Connexion connexion) {

        String texte = connexion.getAvailableText().trim();
        char c;
        int taille;
        boolean res = true;
        if ("".equals(texte)) {
            return false;
        }
        taille = texte.length();
        for (int i=0;i<taille;i++) {
            c = texte.charAt(i);
            if ((c<'a' || c>'z') && (c<'A' || c>'Z') && (c<'0' || c>'9')
                    && c!='_' && c!='-') {
                res = false;
                break;
            }
        }
        if (!res)
            return false;
        for (Connexion cnx:connectes) {
            if (texte.equalsIgnoreCase(cnx.getAlias())) { //alias d�j� utilis�
                res = false;
                break;
            }
        }
        connexion.setAlias(texte);
        return true;
    }

    /**
     * Retourne la liste des alias des connect�s au serveur dans une cha�ne de caract�res.
     *
     * @return String cha�ne de caract�res contenant la liste des alias des membres connect�s sous la
     * forme alias1:alias2:alias3 ...
     */
    public String list() {
        String s = "";
        for (Connexion cnx:connectes)
            s+=cnx.getAlias()+":";
        return s;
    }
    
    
    
   
    public void envoyerATousSauf(String str, String aliasExpediteur) {
    	for (Connexion cnx:connectes){
    		if(!aliasExpediteur.equalsIgnoreCase(cnx.getAlias())) {
        	cnx.envoyer(aliasExpediteur +">> "+str);;
    		}
    	}
    	ajouterHistorique(aliasExpediteur + ">> " + str);
    }
    
    public void ajouterHistorique(String message){
        historique.add(message);
    }
    /**
     * Retourne la liste des messages de l'historique de chat dans une cha�ne
     * de caract�res.
     *
     * @return String cha�ne de caract�res contenant la liste des alias des membres connect�s sous la
     * forme message1\nmessage2\nmessage3 ...
     */
    public String historique() {
            String s = "";
            for(String history : historique)
                s+=history+"\n";
            return s;
        }
    public Connexion isExistAlias(String alias){
        for (Connexion cnx : connectes) {
            if (alias.equalsIgnoreCase(cnx.getAlias())) {
                return cnx;
            }
        }
        return null;
    }

    public void EnvoyerInvitation(Connexion cnx1, String alias2){
        if(!cnx1.getAlias().equalsIgnoreCase(alias2)) {
            Connexion cnx2 = isExistAlias(alias2);
            if (cnx2 != null) {
                Invitation invitation = new Invitation(cnx1.getAlias(), alias2);
                Invitation invitation2 = new Invitation(alias2, cnx1.getAlias());
                if (invitations.contains(invitation2)) {
                    SalonPrive sp = new SalonPrive(cnx1.getAlias(), alias2);
                    salonPrives.add(sp);
                    invitations.remove(invitation2);
                    cnx2.envoyer(cnx1.getAlias() + " a accepte votre invitation");
                } else {
                    invitations.add(invitation);
                    cnx2.envoyer(cnx1.getAlias() + " Vous a envoyer une invitation de chat prive");
                }
            } else
                cnx1.envoyer("Alias " + alias2 + " inexistant");
        }else
            cnx1.envoyer("Impossible de creer ce chat");
    }

    public void supprimerInvitation(Connexion cnx1, String alias2){
        Connexion cnx2=isExistAlias(alias2);
        if(cnx2!=null){
            Invitation invitation = new Invitation(alias2,cnx1.getAlias());
            if(invitations.contains(invitation) ) {
                invitations.remove(invitation);
                cnx2.envoyer(cnx1.getAlias() + " a refusé votre invitation");
            }else
                cnx1.envoyer("Aucune invitation trouvé");
        }else
            cnx1.envoyer("Alias "+alias2 + " inexistant");

    }

    public void listInvitations(Connexion connexion){
        String s = "";
        for(Invitation invitation : invitations){
            if(invitation.getAliasInvite().equalsIgnoreCase(connexion.getAlias()))
                s+=invitation.getAliasHote()+"\n";
        }
        connexion.envoyer("INV " + s);
    }

    public void EnvoiMessagePrive(Connexion cnx1,String message){
        String[]data =message.trim().split(" ");
        if(data.length>=2) {
            String msg = "";
            for (int i = 1; i < data.length; i++) {
                msg += data[i]+" ";
            }
            Connexion cnx2 = isExistAlias(data[0]);
            if (cnx2 != null) {
                if (salonPrives.contains(new SalonPrive(cnx1.getAlias(), cnx2.getAlias())) ||
                        salonPrives.contains(new SalonPrive(cnx2.getAlias(), cnx1.getAlias()))) {
                    cnx2.envoyer(cnx1.getAlias() + ">>" + msg);
                } else {
                    cnx2.envoyer("Aucun salon privé");
                }
            } else
                cnx1.envoyer("Alias " + data[0] + " inexistant");
        }else
            cnx1.envoyer("Mauvaise syntaxe inexistant");
    }

    public void quitterSalonPrive(Connexion cnx1,String alias2){
        Connexion cnx2=isExistAlias(alias2);
        if(cnx2!=null){
            SalonPrive salonPrive1 = new SalonPrive(alias2,cnx1.getAlias());
            SalonPrive salonPrive2 = new SalonPrive(cnx1.getAlias(),alias2);
            if(salonPrives.contains(salonPrive1) || salonPrives.contains(salonPrive2)) {
                if(salonPrives.contains(salonPrive1))
                    salonPrives.remove(salonPrive1);
                else
                    salonPrives.remove(salonPrive2);
                cnx2.envoyer(cnx1.getAlias() + " a quitté le chat");
            }else
                cnx1.envoyer("Aucune salon trouvé");
        }else
            cnx1.envoyer("Alias "+alias2 + " inexistant");
    }
       
    
    
    
}
