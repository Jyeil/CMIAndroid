package com.univ.lorraine.cmi.synchronize.callContainer.bibliotheque;

import com.univ.lorraine.cmi.database.model.Bibliotheque;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by alexis on 22/05/2016.
 */
public class BibliothequeDeleteCall extends AbstractBibliothequeCall<ResponseBody> {

    public final static String type = dataType + "_DELETE_" + extensionName;

    public BibliothequeDeleteCall(){}

    public BibliothequeDeleteCall(Long idU, Bibliotheque o) {
        super(idU, o);
    }

    @Override
    public String getType() {
        return type;
    }



    @Override
    protected void beforeExecuteCall() {

    }

    @Override
    protected Response<ResponseBody> executeCall(CallMeIshmaelService service) throws IOException, RuntimeException {
        return service.deleteBibliotheque(idUser, ((Bibliotheque)getObjectData()).getIdServeur())
                .execute();
    }

    @Override
    protected void afterExecuteCall(Response<ResponseBody> response) {

    }

}
