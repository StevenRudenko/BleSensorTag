package sample.ble.sensortag.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sample.ble.sensortag.R;
import sample.ble.sensortag.sensor.TiAccelerometerSensor;
import sample.ble.sensortag.sensor.TiGyroscopeSensor;
import sample.ble.sensortag.sensor.TiSensor;
import sample.ble.sensortag.sensor.TiSensors;

/**
 * Created by steven on 9/5/13.
 */
public class BluetoothGattServicesAdapter extends BaseExpandableListAdapter {

    public interface OnDemoClickListener {
        public void onDemoClick(BluetoothGattService service);
    }

    private static final String MODE_READ = "R";
    private static final String MODE_NOTIFY = "N";
    private static final String MODE_WRITE = "W";

    private final ArrayList<BluetoothGattService> services;
    private final HashMap<BluetoothGattService, ArrayList<BluetoothGattCharacteristic>> characteristics;
    private final LayoutInflater inflater;

    private OnDemoClickListener demoClickListener;

    public BluetoothGattServicesAdapter(Context context, List<BluetoothGattService> gattServices) {
        inflater = LayoutInflater.from(context);

        services = new ArrayList<BluetoothGattService>(gattServices.size());
        characteristics = new HashMap<BluetoothGattService, ArrayList<BluetoothGattCharacteristic>>(gattServices.size());
        for (BluetoothGattService gattService : gattServices) {
            final List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            characteristics.put(gattService, new ArrayList<BluetoothGattCharacteristic>(gattCharacteristics));
            services.add(gattService);
        }
    }

    public void setDemoClickListener(OnDemoClickListener listener) {
        this.demoClickListener = listener;
    }

    @Override
    public int getGroupCount() {
        return services.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return characteristics.get(getGroup(groupPosition)).size();
    }

    @Override
    public BluetoothGattService getGroup(int groupPosition) {
        return services.get(groupPosition);
    }

    @Override
    public BluetoothGattCharacteristic getChild(int groupPosition, int childPosition) {
        return characteristics.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 100 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();

            convertView = inflater.inflate(R.layout.listitem_service, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);
            holder.demo = convertView.findViewById(R.id.demo);

            holder.demo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (demoClickListener == null)
                        return;
                    final BluetoothGattService service = (BluetoothGattService) holder.demo.getTag();
                    demoClickListener.onDemoClick(service);
                }
            });

            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }

        final BluetoothGattService item = getGroup(groupPosition);

        final String uuid = item.getUuid().toString();
        final TiSensor<?> sensor = TiSensors.getSensor(uuid);
        final String serviceName = sensor != null ? sensor.getName() : "Unknown";

        holder.name.setText(serviceName);
        holder.uuid.setText(uuid);
        if (isDemoable(sensor)) {
            holder.demo.setTag(item);
            holder.demo.setVisibility(View.VISIBLE);
        } else {
            holder.demo.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();

            convertView = inflater.inflate(R.layout.listitem_characteristic, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);
            holder.modes = (TextView) convertView.findViewById(R.id.modes);

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }

        final BluetoothGattCharacteristic item = getChild(groupPosition, childPosition);
        final TiSensor<?> sensor = TiSensors.getSensor(item.getService().getUuid().toString());

        final String uuid = item.getUuid().toString();
        final String name = sensor != null ? sensor.getCharacteristicName(uuid) : "Unknown";
        final String modes = getModeString(item.getProperties());

        holder.name.setText(name);
        holder.uuid.setText(uuid);
        holder.modes.setText(modes);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static boolean isDemoable(TiSensor<?> sensor) {
        if (sensor instanceof TiAccelerometerSensor)
            return true;
        if (sensor instanceof TiGyroscopeSensor)
            return true;
        return false;
    }

    private static String getModeString(int prop) {
        final StringBuilder modeBuilder = new StringBuilder();
        if ((prop & BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
            modeBuilder.append(MODE_READ);
        }
        if ((prop & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            if (modeBuilder.length() > 0)
                modeBuilder.append("/");
            modeBuilder.append(MODE_NOTIFY);
        }
        if ((prop & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) {
            if (modeBuilder.length() > 0)
                modeBuilder.append("/");
            modeBuilder.append(MODE_WRITE);
        }
        return modeBuilder.toString();
    }

    private static class GroupViewHolder {
        public TextView name;
        public TextView uuid;
        public View demo;
    }

    private static class ChildViewHolder {
        public TextView name;
        public TextView uuid;
        public TextView modes;
    }
}
