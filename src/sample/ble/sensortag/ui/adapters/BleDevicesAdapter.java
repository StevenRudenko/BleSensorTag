package sample.ble.sensortag.ui.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sample.ble.sensortag.R;

/** Adapter for holding devices found through scanning. */
public class BleDevicesAdapter extends BaseAdapter {
    /** Connected RSSI value. */
    public static final int RSSI_CONNECTED = -1;

    /** Layout inflater. */
    private final LayoutInflater inflater;
    /** List of BLE devices. */
    private final ArrayList<BluetoothDevice> leDevices = new ArrayList<>();
    /** RSSI signals map. */
    private final Map<BluetoothDevice, Integer> rssiMap = new HashMap<>();

    public BleDevicesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setDevices(Map<BluetoothDevice, Integer> devices) {
        rssiMap.clear();
        rssiMap.putAll(devices);
        leDevices.clear();
        leDevices.addAll(rssiMap.keySet());
    }

    public List<BluetoothDevice> getDevices() {
        return leDevices;
    }

    public BluetoothDevice getDevice(int position) {
        return leDevices.get(position);
    }

    @Override
    public int getCount() {
        return leDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return leDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void clear() {
        leDevices.clear();
        rssiMap.clear();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = inflater.inflate(R.layout.li_device, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = leDevices.get(i);
        final String deviceName = device.getName();
        if (TextUtils.isEmpty(deviceName)) {
            viewHolder.name.setText(device.getAddress());
            viewHolder.address.setVisibility(View.GONE);
            viewHolder.address.setText(null);
        } else {
            viewHolder.name.setText(deviceName);
            viewHolder.address.setText(device.getAddress());
            viewHolder.address.setVisibility(View.VISIBLE);
        }

        final int rssi = rssiMap.get(device);
        if (rssi == RSSI_CONNECTED) {
            viewHolder.signal.setText(R.string.scan_li_signal_connected);
        } else {
            viewHolder.signal.setText(viewGroup.getResources().getString(
                    R.string.scan_li_signal_template,
                    Integer.toString(rssi)));
        }
        return view;
    }

    /** List item view holder. */
    static final class ViewHolder {
        /** Device name. */
        TextView name;
        /** Device address. */
        TextView address;
        /** Device name. */
        TextView signal;

        private ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.device_name);
            address = (TextView) view.findViewById(R.id.device_address);
            signal = (TextView) view.findViewById(R.id.device_rssi);
        }
    }
}
