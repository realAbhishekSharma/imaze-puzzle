package com.elementstore.imazepuzzle.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.elementstore.imazepuzzle.R;
import com.elementstore.imazepuzzle.services.SoundAndVibration;

public class ConfirmDialog {
    Context context;
    AlertDialog.Builder dialogBox;
    AlertDialog alertDialog;
    View root;

    TextView okayButton, cancelButton;
    ConfirmBoxItemClick confirmBoxItemClick;
    int index =0;

    public ConfirmDialog(Context context, ConfirmBoxItemClick confirmBoxItemClick){
        this.context = context;
        this.confirmBoxItemClick = confirmBoxItemClick;
        initializeDialog();
    }

    private void initializeDialog(){

        dialogBox = new AlertDialog.Builder(this.context);
        root = View.inflate(context, R.layout.confirm_dialog_box, null);
        dialogBox.setView(root);

        okayButton = root.findViewById(R.id.okayConfirmBox);
        cancelButton = root.findViewById(R.id.cancelConfirmBox);

        okayButton.setOnClickListener(view -> {
            confirmBoxItemClick.onOkayClick(view, index);
            closeConfirmDialogBox();
        });

        cancelButton.setOnClickListener(view -> {
            confirmBoxItemClick.onCancelClick(view, index);
            closeConfirmDialogBox();
        });

        alertDialog = dialogBox.create();
        alertDialog.setCanceledOnTouchOutside(true);
    }

    public void openConfirmBox(int index){
        this.index = index;
        alertDialog.show();
    }

    public interface ConfirmBoxItemClick{
        void onOkayClick(View view, int index);
        void onCancelClick(View view, int index);
    }

    public void closeConfirmDialogBox(){
        alertDialog.dismiss();
    }
}
