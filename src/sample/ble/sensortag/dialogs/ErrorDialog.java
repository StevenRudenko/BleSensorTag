package sample.ble.sensortag.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import sample.ble.sensortag.R;

public class ErrorDialog extends AppDialog<ErrorDialog.ErrorDialogListener> {
    public static final String TAG = ErrorDialog.class.getSimpleName();

    public interface ErrorDialogListener {
        public void onDismiss(ErrorDialog f);
    }

    private int message;

    public static ErrorDialog newInstance(int message) {
        final ErrorDialog dialog = new ErrorDialog();
        dialog.message = message;

        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(null)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getListener() != null)
                            getListener().onDismiss(ErrorDialog.this);
                    }
                })
                .create();
    }
}
