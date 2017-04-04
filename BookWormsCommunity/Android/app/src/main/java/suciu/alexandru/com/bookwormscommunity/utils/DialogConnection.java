package suciu.alexandru.com.bookwormscommunity.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import suciu.alexandru.com.bookwormscommunity.R;

/**
 * Created by Alexandru on 27.05.2016.
 */
public class DialogConnection {
    private Context context;
    private Dialog dialog;
    private Button btnRetry;
    private ProgressDialog progressDialog;
    private Boolean isConnected = false;

    public DialogConnection(final Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.setTitle("Connection required");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_internet_layout);
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading");
        btnRetry = (Button) dialog.findViewById(R.id.btnRetry);
        isConnected = false;
    }

    public void showMessage(final Activity activity) {

        if (!dialog.isShowing()) {
            dialog.show();
        }

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                progressDialog.show();
                progressDialog.dismiss();
                activity.recreate();
            }
        });
    }



}
