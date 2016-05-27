package com.univ.lorraine.cmi.asyncTask;

import android.app.Activity;
import android.os.AsyncTask;

import com.univ.lorraine.cmi.BookUtilities;
import com.univ.lorraine.cmi.Utilities;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.database.model.Livre;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import retrofit2.Response;

/**
 * Created by alexis on 27/05/2016.
 */
public abstract class AjouterLivreBibliothequeAsyncTask extends AsyncTask<Void, Integer, Bibliotheque> {

    protected Activity activity;

    protected CmidbaOpenDatabaseHelper dbHelper;

    protected Livre livre;

    private boolean beforeAjoutLivre;

    private boolean beforeTelechargementLivre;

    public AjouterLivreBibliothequeAsyncTask(Activity a, CmidbaOpenDatabaseHelper dbH, Livre l) {
        activity = a;
        dbHelper = dbH;
        livre = l;
        beforeAjoutLivre = false;
        beforeTelechargementLivre = false;
    }

    public boolean isBeforeAjoutLivre() {
        return beforeAjoutLivre;
    }

    public boolean isBeforeTelechargementLivre() {
        return beforeTelechargementLivre;
    }

    @Override
    protected Bibliotheque doInBackground(Void... params) {
        beforeAjoutLivre = false;
        beforeTelechargementLivre = false;
        //TODO récupèrer l'idUser
        Long idUser = Long.valueOf(1);
        Bibliotheque bibliothequeServeur;
        Bibliotheque bibliotheque = new Bibliotheque(livre);
        try {
            // On envoie la création de cette bibliothèque au serveur
            Response<Bibliotheque> response = CallMeIshmaelServiceProvider
                    .getService()
                    .createBibliotheque(idUser, bibliotheque)
                    .execute();

            beforeAjoutLivre = true;
            publishProgress();

            // Erreur
            if (Utilities.isErrorCode(response.code()))
                return null;

            bibliothequeServeur = response.body();
            if (bibliothequeServeur == null)
                return null;

            bibliothequeServeur.setLivre(livre);

            // On sauvegarde le livre et la bibliothèque dans la BDD locale
            BookUtilities.sauverBibliotheque(bibliothequeServeur, dbHelper);

            beforeTelechargementLivre = true;
            publishProgress();

            // On télécharge le livre sur l'appareil
            BookUtilities.downloadBook(activity, livre);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return bibliotheque;
    }

    @Override
    protected abstract void onPreExecute();

    @Override
    protected abstract void onPostExecute(Bibliotheque bibliotheque);

}
