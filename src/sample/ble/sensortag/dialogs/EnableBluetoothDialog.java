package sample.ble.sensortag.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import sample.ble.sensortag.R;

public class EnableBluetoothDialog extends AppDialog<EnableBluetoothDialog.EnableBluetoothDialogListener> {
    public static final String TAG = EnableBluetoothDialog.class.getSimpleName();

    public interface EnableBluetoothDialogListener {
        public void onEnableBluetooth(EnableBluetoothDialog f);
        public void onCancel(EnableBluetoothDialog f);
    }

    public EnableBluetoothDialog() {
        setCancelable(false);
    }

    @Override
    protected boolean isListenerOptional() {
        return false;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
        .setTitle(null)
        .setMessage(R.string.dialog_enable_bluetooth)
        .setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                getListener().onEnableBluetooth(EnableBluetoothDialog.this);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getListener().onCancel(EnableBluetoothDialog.this);
            }
        })
        .setCancelable(false);

        return builder.create();
    }
}